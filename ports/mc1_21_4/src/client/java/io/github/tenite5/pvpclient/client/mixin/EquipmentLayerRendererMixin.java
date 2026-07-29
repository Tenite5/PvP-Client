package io.github.tenite5.pvpclient.client.mixin;

import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import io.github.tenite5.pvpclient.client.modules.HitColorModule;

@Mixin(EquipmentLayerRenderer.class)
public class EquipmentLayerRendererMixin {
	private static final String RENDER_LAYERS = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;)V";

	@Redirect(
		method = RENDER_LAYERS,
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/texture/OverlayTexture;NO_OVERLAY:I", opcode = Opcodes.GETSTATIC)
	)
	private int pvpClient$armorOverlay() {
		return HitColorModule.armorFlashing ? OverlayTexture.pack(0, HitColorModule.ARMOR_ROW) : OverlayTexture.NO_OVERLAY;
	}
}
