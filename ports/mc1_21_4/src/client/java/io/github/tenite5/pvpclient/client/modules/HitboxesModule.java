package io.github.tenite5.pvpclient.client.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import io.github.tenite5.pvpclient.client.core.Module;
import io.github.tenite5.pvpclient.client.core.setting.BooleanSetting;
import io.github.tenite5.pvpclient.client.core.setting.ColorSetting;
import io.github.tenite5.pvpclient.client.core.setting.SliderSetting;

public class HitboxesModule extends Module {
	private static final double MAX_DISTANCE_SQUARED = 128.0 * 128.0;

	public final ColorSetting color = this.addSetting(new ColorSetting("color", "Box Color", 0xFF39C5CF));
	public final BooleanSetting fill = this.addSetting(new BooleanSetting("fill", "Fill Boxes", true));
	public final SliderSetting transparency = this.addSetting(new SliderSetting("transparency", "Fill Transparency", 70.0, 0.0, 100.0, 1.0, "%"));
	public final BooleanSetting playersOnly = this.addSetting(new BooleanSetting("playersOnly", "Players Only", false));
	public final BooleanSetting boxOnly = this.addSetting(new BooleanSetting("boxOnly", "Box Only", true));

	public HitboxesModule() {
		super("hitboxes", "Hitboxes", "Draws configurable hitboxes around entities.");
	}

	public void render(WorldRenderContext context) {
		Minecraft minecraft = Minecraft.getInstance();
		if (!this.isEnabled() || minecraft.level == null || minecraft.getCameraEntity() == null || context.matrixStack() == null) {
			return;
		}

		Entity cameraEntity = minecraft.getCameraEntity();
		Vec3 cameraPosition = context.camera().getPosition();
		int strokeColor = this.color.get();
		int fillColor = withTransparency(strokeColor, this.transparency.get());
		PoseStack poseStack = context.matrixStack();
		poseStack.pushPose();
		poseStack.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
		try {
			for (Entity entity : minecraft.level.entitiesForRendering()) {
				if (entity == cameraEntity || entity.isRemoved() || entity.isSpectator()) {
					continue;
				}
				if (this.playersOnly.get() && !(entity instanceof Player)) {
					continue;
				}
				if (entity.distanceToSqr(cameraPosition) > MAX_DISTANCE_SQUARED) {
					continue;
				}

				AABB box = entity.getBoundingBox();
				if (this.fill.get()) {
					ShapeRenderer.addChainedFilledBoxVertices(
						poseStack, context.consumers().getBuffer(RenderType.debugFilledBox()),
						box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ,
						red(fillColor), green(fillColor), blue(fillColor), alpha(fillColor)
					);
				}
				ShapeRenderer.renderLineBox(
					poseStack, context.consumers().getBuffer(RenderType.lines()), box,
					red(strokeColor), green(strokeColor), blue(strokeColor), alpha(strokeColor)
				);
				if (!this.boxOnly.get()) {
					renderDetails(context, poseStack, entity, strokeColor);
				}
			}
		} finally {
			poseStack.popPose();
		}
	}

	private static void renderDetails(WorldRenderContext context, PoseStack poseStack, Entity entity, int color) {
		AABB box = entity.getBoundingBox();
		double centerX = (box.minX + box.maxX) * 0.5;
		double centerZ = (box.minZ + box.maxZ) * 0.5;
		double eyeY = box.minY + entity.getEyeHeight();
		var lines = context.consumers().getBuffer(RenderType.lines());
		ShapeRenderer.renderLineBox(poseStack, lines, box.minX, eyeY, centerZ, box.maxX, eyeY, centerZ, red(color), green(color), blue(color), alpha(color));
		ShapeRenderer.renderVector(
			poseStack, lines,
			new Vector3f((float) centerX, (float) eyeY, (float) centerZ),
			entity.getLookAngle().scale(2.0),
			color
		);
	}

	private static int withTransparency(int color, double transparencyPercent) {
		float visible = (float) (1.0 - transparencyPercent / 100.0);
		int alpha = Math.round(ARGB.alpha(color) * visible);
		return ARGB.color(alpha, ARGB.red(color), ARGB.green(color), ARGB.blue(color));
	}

	private static float red(int color) {
		return ARGB.red(color) / 255.0F;
	}

	private static float green(int color) {
		return ARGB.green(color) / 255.0F;
	}

	private static float blue(int color) {
		return ARGB.blue(color) / 255.0F;
	}

	private static float alpha(int color) {
		return ARGB.alpha(color) / 255.0F;
	}
}
