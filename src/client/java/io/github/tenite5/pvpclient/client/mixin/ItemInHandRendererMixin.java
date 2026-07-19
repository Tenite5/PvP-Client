package io.github.tenite5.pvpclient.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.github.tenite5.pvpclient.client.core.ModuleManager;
import io.github.tenite5.pvpclient.client.modules.ViewModelModule;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
	@Inject(
		method = "submitArmWithItem",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
			shift = At.Shift.AFTER
		)
	)
	private void pvpClient$applyViewModel(
		AbstractClientPlayer player,
		float frameInterp,
		float xRot,
		InteractionHand hand,
		float attack,
		ItemStack itemStack,
		float inverseArmHeight,
		PoseStack poseStack,
		net.minecraft.client.renderer.SubmitNodeCollector submitNodeCollector,
		int lightCoords,
		CallbackInfo ci
	) {
		ViewModelModule viewModel = ModuleManager.VIEW_MODEL;
		if (!viewModel.isEnabled()) {
			return;
		}
		boolean isMainHand = hand == InteractionHand.MAIN_HAND;
		HumanoidArm arm = isMainHand ? player.getMainArm() : player.getMainArm().getOpposite();
		float invert = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;

		poseStack.translate(invert * viewModel.offsetX.getF(), viewModel.offsetY.getF(), viewModel.offsetZ.getF());
		if (viewModel.rotationX.get() != 0.0) {
			poseStack.mulPose(Axis.XP.rotationDegrees(viewModel.rotationX.getF()));
		}
		if (viewModel.rotationY.get() != 0.0) {
			poseStack.mulPose(Axis.YP.rotationDegrees(invert * viewModel.rotationY.getF()));
		}
		if (viewModel.rotationZ.get() != 0.0) {
			poseStack.mulPose(Axis.ZP.rotationDegrees(invert * viewModel.rotationZ.getF()));
		}
		float scale = viewModel.scale.getF();
		if (scale != 1.0F) {
			float pivotX = invert * 0.56F;
			float pivotY = -0.52F;
			float pivotZ = -0.72F;
			poseStack.translate(pivotX * (1.0F - scale), pivotY * (1.0F - scale), pivotZ * (1.0F - scale));
			poseStack.scale(scale, scale, scale);
		}
		if (itemStack.getItem() instanceof ShieldItem && viewModel.shieldHeight.get() != 0.0) {
			poseStack.translate(0.0F, viewModel.shieldHeight.getF(), 0.0F);
		}
	}
}
