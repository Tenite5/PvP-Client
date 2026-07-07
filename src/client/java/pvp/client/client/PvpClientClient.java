package pvp.client.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import pvp.client.PvpClient;
import pvp.client.client.core.ConfigManager;
import pvp.client.client.core.HudModule;
import pvp.client.client.core.Module;
import pvp.client.client.core.ModuleManager;
import pvp.client.client.core.setting.KeySetting;
import pvp.client.client.gui.HudEditScreen;
import pvp.client.client.gui.MainMenuScreen;

public class PvpClientClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ConfigManager.load();

		// Draw all enabled overlays on top of the vanilla HUD.
		HudElementRegistry.addLast(PvpClient.id("overlays"), (graphics, deltaTracker) -> {
			Minecraft minecraft = Minecraft.getInstance();
			if (minecraft.player == null || minecraft.gui.screen() instanceof HudEditScreen) {
				return;
			}
			for (HudModule module : ModuleManager.hudModules()) {
				if (module.isEnabled()) {
					module.render(graphics, minecraft);
				}
			}
		});

		// Hide the vanilla potion icons while our Effects overlay handles them.
		HudElementRegistry.replaceElement(VanillaHudElements.MOB_EFFECTS, original -> (HudElement) (graphics, deltaTracker) -> {
			if (!(ModuleManager.EFFECTS.isEnabled() && ModuleManager.EFFECTS.hideVanilla.get())) {
				original.extractRenderState(graphics, deltaTracker);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
			for (Module module : ModuleManager.all()) {
				if (module.isEnabled()) {
					module.tick(minecraft);
				}
			}
			if (minecraft.gui.screen() == null
				&& minecraft.player != null
				&& KeySetting.isBound(ConfigManager.menuKey)
				&& KeySetting.isCodeDown(minecraft.getWindow(), ConfigManager.menuKey)) {
				minecraft.gui.setScreen(new MainMenuScreen());
			}
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(minecraft -> ConfigManager.save());

		PvpClient.LOGGER.info("pvp client loaded with {} modules", ModuleManager.all().size());
	}
}
