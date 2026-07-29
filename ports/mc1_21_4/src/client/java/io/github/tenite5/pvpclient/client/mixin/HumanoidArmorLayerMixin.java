package io.github.tenite5.pvpclient.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.github.tenite5.pvpclient.client.core.ModuleManager;
import io.github.tenite5.pvpclient.client.modules.HitColorModule;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {
	@Inject(method = "render", at = @At("HEAD"))
	private void pvpClient$beginArmorFlash(PoseStack poseStack, MultiBufferSource bufferSource, int light, HumanoidRenderState state, float limbAngle, float limbDistance, CallbackInfo ci) {
		HitColorModule.armorFlashing = ModuleManager.HIT_COLOR.isEnabled() && state.hasRedOverlay;
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void pvpClient$endArmorFlash(PoseStack poseStack, MultiBufferSource bufferSource, int light, HumanoidRenderState state, float limbAngle, float limbDistance, CallbackInfo ci) {
		HitColorModule.armorFlashing = false;
	}
}
