package io.github.tenite5.pvpclient.client.modules;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;
import io.github.tenite5.pvpclient.client.core.Module;
import io.github.tenite5.pvpclient.client.core.setting.BooleanSetting;
import io.github.tenite5.pvpclient.client.core.setting.EnumSetting;
import io.github.tenite5.pvpclient.client.core.setting.KeySetting;
import io.github.tenite5.pvpclient.client.core.setting.SliderSetting;

public class FreelookModule extends Module {
	public final KeySetting freelookKey = this.addSetting(new KeySetting("freelookKey", "Freelook Key", GLFW.GLFW_KEY_LEFT_ALT));
	public final EnumSetting mode = this.addSetting(new EnumSetting("mode", "Activation", 0, "Hold", "Toggle"));
	public final EnumSetting perspective = this.addSetting(
		new EnumSetting("perspective", "Perspective", 0, "Third Person Back", "Third Person Front", "Keep Current")
	);
	public final SliderSetting sensitivity = this.addSetting(new SliderSetting("sensitivity", "Sensitivity", 1.0, 0.1, 2.0, 0.05, "x"));
	public final BooleanSetting invertYaw = this.addSetting(new BooleanSetting("invertYaw", "Invert Horizontal", false));
	public final BooleanSetting invertPitch = this.addSetting(new BooleanSetting("invertPitch", "Invert Vertical", false));

	private boolean active;
	private boolean keyWasDown;
	private float yaw;
	private float pitch;
	private CameraType previousCameraType;

	public FreelookModule() {
		super("freelook", "Freelook", "Look around freely without changing where you're moving.");
	}

	public boolean isActive() {
		return this.isEnabled() && this.active;
	}

	public float yaw() {
		return this.yaw;
	}

	public float pitch() {
		return this.pitch;
	}

	/** Receives the raw mouse deltas that would normally rotate the player. */
	public void turn(double xo, double yo) {
		float deltaYaw = (float) (xo * 0.15 * this.sensitivity.get());
		float deltaPitch = (float) (yo * 0.15 * this.sensitivity.get());
		this.yaw += this.invertYaw.get() ? -deltaYaw : deltaYaw;
		this.pitch = Mth.clamp(this.pitch + (this.invertPitch.get() ? -deltaPitch : deltaPitch), -90.0F, 90.0F);
	}

	@Override
	public void tick(Minecraft minecraft) {
		boolean keyDown = minecraft.screen == null
			&& minecraft.player != null
			&& KeySetting.isBound(this.freelookKey.get())
			&& this.freelookKey.isDown(minecraft.getWindow());
		if (this.mode.is("Toggle")) {
			if (keyDown && !this.keyWasDown) {
				if (this.active) {
					this.stop(minecraft);
				} else {
					this.start(minecraft);
				}
			}
		} else if (keyDown && !this.active) {
			this.start(minecraft);
		} else if (!keyDown && this.active) {
			this.stop(minecraft);
		}
		this.keyWasDown = keyDown;
	}

	@Override
	protected void onDisable() {
		Minecraft minecraft = Minecraft.getInstance();
		if (this.active && minecraft != null) {
			this.stop(minecraft);
		}
	}

	private void start(Minecraft minecraft) {
		this.yaw = minecraft.player.getYRot();
		this.pitch = minecraft.player.getXRot();
		if (!this.perspective.is("Keep Current")) {
			this.previousCameraType = minecraft.options.getCameraType();
			minecraft.options.setCameraType(this.perspective.is("Third Person Front") ? CameraType.THIRD_PERSON_FRONT : CameraType.THIRD_PERSON_BACK);
		}
		this.active = true;
	}

	private void stop(Minecraft minecraft) {
		this.active = false;
		if (this.previousCameraType != null) {
			minecraft.options.setCameraType(this.previousCameraType);
			this.previousCameraType = null;
		}
	}
}
