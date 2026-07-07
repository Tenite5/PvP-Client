package pvp.client.client.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import pvp.client.client.modules.HitColorModule;
import pvp.client.client.util.HurtOverlayRecolorable;

@Mixin(OverlayTexture.class)
public class OverlayTextureMixin implements HurtOverlayRecolorable {
	@Shadow
	@Final
	private DynamicTexture texture;

	@Override
	public void pvpClient$setHurtColors(int bodyArgb, int armorArgb) {
		NativeImage pixels = this.texture.getPixels();
		if (pixels == null) {
			return;
		}
		for (int y = 0; y < 8; y++) {
			int argb = y == HitColorModule.ARMOR_ROW ? armorArgb : bodyArgb;
			for (int x = 0; x < 16; x++) {
				pixels.setPixel(x, y, argb);
			}
		}
		this.texture.upload();
	}
}
