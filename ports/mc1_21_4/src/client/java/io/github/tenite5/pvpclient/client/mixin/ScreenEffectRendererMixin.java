package io.github.tenite5.pvpclient.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.github.tenite5.pvpclient.client.core.ModuleManager;
import io.github.tenite5.pvpclient.client.modules.ViewModelModule;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {
	private static boolean pvpClient$loweredFire;

	@Inject(method = "renderFire", at = @At("HEAD"))
	private static void pvpClient$lowerFireStart(
		PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci
	) {
		ViewModelModule viewModel = ModuleManager.VIEW_MODEL;
		pvpClient$loweredFire = viewModel.isEnabled() && viewModel.fireLoweness.get() != 0.0;
		if (pvpClient$loweredFire) {
			poseStack.pushPose();
			poseStack.translate(0.0F, -viewModel.fireLoweness.getF(), 0.0F);
		}
	}

	@Inject(method = "renderFire", at = @At("TAIL"))
	private static void pvpClient$lowerFireEnd(
		PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci
	) {
		if (pvpClient$loweredFire) {
			poseStack.popPose();
			pvpClient$loweredFire = false;
		}
	}

}
