package io.github.tenite5.pvpclient.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.github.tenite5.pvpclient.client.core.ModuleManager;
import io.github.tenite5.pvpclient.client.modules.ViewModelModule;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {
	private static boolean pvpClient$loweredFire;

	// The pose is only snapshotted inside submitCustomGeometry (last().copy()),
	// so wrapping this call in push/translate/pop is safe.
	@Inject(method = "submitFire", at = @At("HEAD"))
	private static void pvpClient$lowerFireStart(
		PoseStack poseStack, SubmitNodeCollector submitNodeCollector, TextureAtlasSprite sprite, CallbackInfo ci
	) {
		ViewModelModule viewModel = ModuleManager.VIEW_MODEL;
		pvpClient$loweredFire = viewModel.isEnabled() && viewModel.fireLoweness.get() != 0.0;
		if (pvpClient$loweredFire) {
			poseStack.pushPose();
			poseStack.translate(0.0F, -viewModel.fireLoweness.getF(), 0.0F);
		}
	}

	@Inject(method = "submitFire", at = @At("TAIL"))
	private static void pvpClient$lowerFireEnd(
		PoseStack poseStack, SubmitNodeCollector submitNodeCollector, TextureAtlasSprite sprite, CallbackInfo ci
	) {
		if (pvpClient$loweredFire) {
			poseStack.popPose();
			pvpClient$loweredFire = false;
		}
	}

	@Inject(
		method = "renderItemActivationAnimation",
		at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", shift = At.Shift.AFTER)
	)
	private void pvpClient$totemPopSize(PoseStack poseStack, float partialTicks, SubmitNodeCollector submitNodeCollector, CallbackInfo ci) {
		ViewModelModule viewModel = ModuleManager.VIEW_MODEL;
		float size = viewModel.totemSize.getF();
		if (viewModel.isEnabled() && size != 1.0F) {
			poseStack.scale(size, size, size);
		}
	}
}
