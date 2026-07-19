package io.github.tenite5.pvpclient.client.modules;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import io.github.tenite5.pvpclient.client.core.Module;
import io.github.tenite5.pvpclient.client.core.setting.BooleanSetting;
import io.github.tenite5.pvpclient.client.core.setting.KeySetting;
import io.github.tenite5.pvpclient.client.core.setting.SliderSetting;

public class ZoomModule extends Module {
	public final KeySetting zoomKey = this.addSetting(new KeySetting("zoomKey", "Zoom Key", GLFW.GLFW_KEY_C));
	public final SliderSetting zoomLevel = this.addSetting(new SliderSetting("zoomLevel", "Zoom Level", 4.0, 2.0, 10.0, 0.5, "x"));
	public final BooleanSetting smoothZoom = this.addSetting(new BooleanSetting("smoothZoom", "Smooth Zoom", true));
	public final SliderSetting smoothness = this.addSetting(new SliderSetting("smoothness", "Smoothness", 5.0, 1.0, 10.0, 1.0, ""));
	public final BooleanSetting cinematicCamera = this.addSetting(new BooleanSetting("cinematicCamera", "Cinematic Camera", true));

	private boolean zooming;
	private boolean smoothCameraBackup;
	// Interpolated fov multiplier, updated per frame from the camera mixin.
	private static float currentMultiplier = 1.0F;
	private static long lastFrameNanos;

	public ZoomModule() {
		super("zoom", "Zoom", "Zoom in with a key, like a spyglass without the spyglass.");
	}

	public boolean isZooming() {
		return this.isEnabled() && this.zooming;
	}

	@Override
	public void tick(Minecraft minecraft) {
		boolean keyDown = minecraft.gui.screen() == null
			&& KeySetting.isBound(this.zoomKey.get())
			&& this.zoomKey.isDown(minecraft.getWindow());
		if (keyDown && !this.zooming) {
			this.zooming = true;
			if (this.cinematicCamera.get()) {
				this.smoothCameraBackup = minecraft.options.smoothCamera;
				minecraft.options.smoothCamera = true;
			}
		} else if (!keyDown && this.zooming) {
			this.stopZoom(minecraft);
		}
	}

	@Override
	protected void onDisable() {
		Minecraft minecraft = Minecraft.getInstance();
		if (this.zooming) {
			this.stopZoom(minecraft);
		}
	}

	private void stopZoom(Minecraft minecraft) {
		this.zooming = false;
		if (this.cinematicCamera.get()) {
			minecraft.options.smoothCamera = this.smoothCameraBackup;
		}
	}

	/** Called every frame by CameraMixin; returns the fov multiplier to apply. */
	public float fovMultiplier() {
		float target = this.isZooming() ? 1.0F / this.zoomLevel.getF() : 1.0F;
		if (!this.smoothZoom.get()) {
			currentMultiplier = target;
			return currentMultiplier;
		}
		long now = System.nanoTime();
		float deltaSeconds = lastFrameNanos == 0 ? 0.016F : Math.min((now - lastFrameNanos) / 1.0E9F, 0.1F);
		lastFrameNanos = now;
		// Higher smoothness slider = slower, smoother transition.
		float speed = 26.0F - (float) this.smoothness.get() * 2.0F;
		float t = 1.0F - (float) Math.exp(-speed * deltaSeconds);
		currentMultiplier = currentMultiplier + (target - currentMultiplier) * t;
		if (Math.abs(currentMultiplier - target) < 0.0005F) {
			currentMultiplier = target;
		}
		return currentMultiplier;
	}
}
