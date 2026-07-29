package io.github.tenite5.pvpclient.client.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.github.tenite5.pvpclient.client.core.ModuleManager;

@Mixin(Hud.class)
public class HudMixin {
	@Inject(method = "extractFood", at = @At("TAIL"))
	private void pvpClient$saturationOverlay(
		GuiGraphicsExtractor graphics, Player player, int y, int right, CallbackInfo ci
	) {
		ModuleManager.SATURATION.render(graphics, player, y, right);
	}
}
