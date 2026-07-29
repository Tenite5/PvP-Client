package io.github.tenite5.pvpclient.client.modules;

import net.minecraft.client.Minecraft;
import io.github.tenite5.pvpclient.client.core.Module;
import io.github.tenite5.pvpclient.client.core.setting.ColorSetting;
import io.github.tenite5.pvpclient.client.util.HurtOverlayRecolorable;

public class HitColorModule extends Module {
	public static final int VANILLA_HURT_COLOR = 0xB2FF0000;
	public static final int ARMOR_ROW = 6;
	public static boolean armorFlashing;
	public final ColorSetting hitColor = this.addSetting(new ColorSetting("hitColor", "Hit Color", 0xB2FF4CE1));
	public final ColorSetting armorColor = this.addSetting(new ColorSetting("armorColor", "Armor Hit Color", 0xB2FF4CE1));
	private int appliedBody = VANILLA_HURT_COLOR;
	private int appliedArmor = VANILLA_HURT_COLOR;

	public HitColorModule() {
		super("hitcolor", "Hit Color", "Changes the damage flash color of entities you hit.");
	}

	@Override
	public void tick(Minecraft minecraft) {
		this.apply(minecraft, toTextureColor(this.hitColor.get()), toTextureColor(this.armorColor.get()));
	}

	@Override
	protected void onDisable() {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft != null && minecraft.gameRenderer != null) {
			this.apply(minecraft, VANILLA_HURT_COLOR, VANILLA_HURT_COLOR);
		}
	}

	private static int toTextureColor(int argb) {
		int intensity = argb >>> 24;
		return (255 - intensity) << 24 | argb & 0xFFFFFF;
	}

	private void apply(Minecraft minecraft, int body, int armor) {
		if (this.appliedBody == body && this.appliedArmor == armor) {
			return;
		}
		if (minecraft.gameRenderer.overlayTexture() instanceof HurtOverlayRecolorable recolorable) {
			recolorable.pvpClient$setHurtColors(body, armor);
			this.appliedBody = body;
			this.appliedArmor = armor;
		}
	}
}
