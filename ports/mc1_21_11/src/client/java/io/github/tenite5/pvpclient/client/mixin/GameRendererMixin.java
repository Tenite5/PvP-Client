package io.github.tenite5.pvpclient.client.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import io.github.tenite5.pvpclient.client.core.ModuleManager;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
	private void pvpClient$applyZoom(Camera camera, float partialTicks, boolean useFovSetting, CallbackInfoReturnable<Float> cir) {
		if (useFovSetting) {
			float multiplier = ModuleManager.ZOOM.fovMultiplier();
			if (multiplier != 1.0F) {
				cir.setReturnValue(cir.getReturnValueF() * multiplier);
			}
		}
	}
}
