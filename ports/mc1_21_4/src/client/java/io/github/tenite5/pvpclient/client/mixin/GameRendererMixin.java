package io.github.tenite5.pvpclient.client.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

	@Inject(
		method = "renderItemActivationAnimation",
		at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", shift = At.Shift.AFTER)
	)
	private void pvpClient$transformTotem(GuiGraphics graphics, float partialTicks, CallbackInfo ci) {
		var viewModel = ModuleManager.VIEW_MODEL;
		if (!viewModel.isEnabled()) {
			return;
		}
		graphics.pose().translate(viewModel.totemOffsetX.getF(), viewModel.totemOffsetY.getF(), 0.0F);
		float size = viewModel.totemSize.getF();
		if (size != 1.0F) {
			graphics.pose().scale(size, size, size);
		}
	}
}
