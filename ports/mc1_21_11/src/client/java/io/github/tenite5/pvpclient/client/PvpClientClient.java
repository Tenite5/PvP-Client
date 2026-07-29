package io.github.tenite5.pvpclient.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import io.github.tenite5.pvpclient.PvpClient;
import io.github.tenite5.pvpclient.client.core.ConfigManager;
import io.github.tenite5.pvpclient.client.core.HudModule;
import io.github.tenite5.pvpclient.client.core.Module;
import io.github.tenite5.pvpclient.client.core.ModuleManager;
import io.github.tenite5.pvpclient.client.core.setting.KeySetting;
import io.github.tenite5.pvpclient.client.gui.HudEditScreen;
import io.github.tenite5.pvpclient.client.gui.MainMenuScreen;

public class PvpClientClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ConfigManager.load();

		// Draw all enabled overlays on top of the vanilla HUD.
		HudElementRegistry.addLast(PvpClient.id("overlays"), (graphics, deltaTracker) -> {
			Minecraft minecraft = Minecraft.getInstance();
			if (minecraft.player == null || minecraft.screen instanceof HudEditScreen) {
				return;
			}
			for (HudModule module : ModuleManager.hudModules()) {
				if (module.isEnabled()) {
					module.render(graphics, minecraft);
				}
			}
		});

		// Hide the vanilla potion icons while our Effects overlay handles them.
		HudElementRegistry.replaceElement(VanillaHudElements.STATUS_EFFECTS, original -> (HudElement) (graphics, deltaTracker) -> {
			if (!(ModuleManager.EFFECTS.isEnabled() && ModuleManager.EFFECTS.hideVanilla.get())) {
				original.render(graphics, deltaTracker);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
			for (Module module : ModuleManager.all()) {
				if (module.isEnabled()) {
					module.tick(minecraft);
				}
			}
			if (minecraft.screen == null
				&& minecraft.player != null
				&& KeySetting.isBound(ConfigManager.menuKey)
				&& KeySetting.isCodeDown(minecraft.getWindow(), ConfigManager.menuKey)) {
				minecraft.setScreen(new MainMenuScreen());
			}
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(minecraft -> ConfigManager.save());

		PvpClient.LOGGER.info("pvp client loaded with {} modules", ModuleManager.all().size());
	}
}
