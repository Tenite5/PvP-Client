package io.github.tenite5.pvpclient.client.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.glfw.GLFW;
import io.github.tenite5.pvpclient.PvpClient;
import io.github.tenite5.pvpclient.client.core.setting.BooleanSetting;
import io.github.tenite5.pvpclient.client.core.setting.ColorSetting;
import io.github.tenite5.pvpclient.client.core.setting.EnumSetting;
import io.github.tenite5.pvpclient.client.core.setting.KeySetting;
import io.github.tenite5.pvpclient.client.core.setting.Setting;
import io.github.tenite5.pvpclient.client.core.setting.SliderSetting;

public final class ConfigManager {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	public static int menuKey = GLFW.GLFW_KEY_RIGHT_SHIFT;

	private ConfigManager() {
	}

	private static Path configPath() {
		return FabricLoader.getInstance().getConfigDir().resolve("pvp-client.json");
	}

	public static void save() {
		JsonObject root = new JsonObject();
		root.addProperty("menuKey", menuKey);
		JsonObject modules = new JsonObject();
		for (Module module : ModuleManager.all()) {
			JsonObject entry = new JsonObject();
			entry.addProperty("enabled", module.isEnabled());
			entry.addProperty("liked", module.isLiked());
			if (module instanceof HudModule hud) {
				entry.addProperty("x", hud.anchorX());
				entry.addProperty("y", hud.anchorY());
				entry.addProperty("scale", hud.scale());
			}
			JsonObject settings = new JsonObject();
			for (Setting setting : module.settings()) {
				switch (setting) {
					case BooleanSetting s -> settings.addProperty(s.id(), s.get());
					case SliderSetting s -> settings.addProperty(s.id(), s.get());
					case ColorSetting s -> settings.addProperty(s.id(), s.get());
					case EnumSetting s -> settings.addProperty(s.id(), s.index());
					case KeySetting s -> settings.addProperty(s.id(), s.get());
					default -> {
					}
				}
			}
			entry.add("settings", settings);
			modules.add(module.id(), entry);
		}
		root.add("modules", modules);
		try {
			Files.writeString(configPath(), GSON.toJson(root));
		} catch (IOException e) {
			PvpClient.LOGGER.error("Failed to save pvp client config", e);
		}
	}

	public static void load() {
		Path path = configPath();
		if (!Files.exists(path)) {
			return;
		}
		JsonObject root;
		try {
			root = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
		} catch (Exception e) {
			PvpClient.LOGGER.error("Failed to load pvp client config", e);
			return;
		}
		if (root.has("menuKey")) {
			menuKey = root.get("menuKey").getAsInt();
		}
		if (!root.has("modules")) {
			return;
		}
		JsonObject modules = root.getAsJsonObject("modules");
		for (Module module : ModuleManager.all()) {
			JsonElement entryElement = modules.get(module.id());
			if (entryElement == null || !entryElement.isJsonObject()) {
				continue;
			}
			JsonObject entry = entryElement.getAsJsonObject();
			module.setLiked(entry.has("liked") && entry.get("liked").getAsBoolean());
			if (module instanceof HudModule hud) {
				if (entry.has("x") && entry.has("y")) {
					hud.setAnchors(entry.get("x").getAsDouble(), entry.get("y").getAsDouble());
				}
				if (entry.has("scale")) {
					hud.setScale(entry.get("scale").getAsFloat());
				}
			}
			if (entry.has("settings")) {
				JsonObject settings = entry.getAsJsonObject("settings");
				for (Setting setting : module.settings()) {
					JsonElement value = settings.get(setting.id());
					if (value == null) {
						continue;
					}
					try {
						switch (setting) {
							case BooleanSetting s -> s.set(value.getAsBoolean());
							case SliderSetting s -> s.set(value.getAsDouble());
							case ColorSetting s -> s.set(value.getAsInt());
							case EnumSetting s -> s.setIndex(value.getAsInt());
							case KeySetting s -> s.set(value.getAsInt());
							default -> {
							}
						}
					} catch (Exception e) {
						PvpClient.LOGGER.warn("Bad config value for {}.{}", module.id(), setting.id());
					}
				}
			}
			// Enable last so onEnable sees final setting values.
			if (entry.has("enabled")) {
				module.setEnabled(entry.get("enabled").getAsBoolean());
			}
		}
	}
}
