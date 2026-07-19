package io.github.tenite5.pvpclient.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.github.tenite5.pvpclient.client.core.ModuleManager;
import io.github.tenite5.pvpclient.client.modules.HitColorModule;

@Mixin(EquipmentLayerRenderer.class)
public class EquipmentLayerRendererMixin {
	private static final String RENDER_LAYERS = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V";

	private static boolean pvpClient$flashArmor;

	@Inject(method = RENDER_LAYERS, at = @At("HEAD"))
	private void pvpClient$captureFlash(
		EquipmentClientInfo.LayerType layerType,
		ResourceKey<?> equipmentAssetId,
		Model<?> model,
		Object state,
		ItemStack itemStack,
		PoseStack poseStack,
		SubmitNodeCollector submitNodeCollector,
		int lightCoords,
		Identifier playerTextureOverride,
		int outlineColor,
		int order,
		CallbackInfo ci
	) {
		pvpClient$flashArmor = ModuleManager.HIT_COLOR.isEnabled()
			&& state instanceof LivingEntityRenderState living
			&& living.hasRedOverlay;
	}

	@Redirect(
		method = RENDER_LAYERS,
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/texture/OverlayTexture;NO_OVERLAY:I", opcode = org.objectweb.asm.Opcodes.GETSTATIC)
	)
	private int pvpClient$armorOverlay() {
		return pvpClient$flashArmor ? OverlayTexture.pack(0, HitColorModule.ARMOR_ROW) : OverlayTexture.NO_OVERLAY;
	}
}
