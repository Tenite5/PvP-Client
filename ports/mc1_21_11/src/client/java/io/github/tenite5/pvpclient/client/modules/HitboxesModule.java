package io.github.tenite5.pvpclient.client.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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

	@Override
	public void tick(Minecraft minecraft) {
		if (minecraft.level == null || minecraft.getCameraEntity() == null) {
			return;
		}

		Entity cameraEntity = minecraft.getCameraEntity();
		Vec3 cameraPosition = minecraft.gameRenderer.getMainCamera().position();
		int strokeColor = this.color.get();
		int fillColor = withTransparency(strokeColor, this.transparency.get());
		GizmoStyle style = this.fill.get()
			? GizmoStyle.strokeAndFill(strokeColor, 2.0F, fillColor)
			: GizmoStyle.stroke(strokeColor, 2.0F);
		try (var ignored = minecraft.collectPerTickGizmos()) {
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
				Gizmos.cuboid(box, style, false);
				if (!this.boxOnly.get()) {
					addDetails(entity, box, strokeColor);
				}
			}
		}
	}

	private static int withTransparency(int color, double transparencyPercent) {
		float visible = (float) (1.0 - transparencyPercent / 100.0);
		int alpha = Math.round(ARGB.alpha(color) * visible);
		return ARGB.color(alpha, ARGB.red(color), ARGB.green(color), ARGB.blue(color));
	}

	private static void addDetails(Entity entity, AABB box, int color) {
		double eyeY = box.minY + entity.getEyeHeight();
		double centerX = (box.minX + box.maxX) * 0.5;
		double centerZ = (box.minZ + box.maxZ) * 0.5;
		Vec3 eye = new Vec3(centerX, eyeY, centerZ);
		Gizmos.line(new Vec3(box.minX, eyeY, centerZ), new Vec3(box.maxX, eyeY, centerZ), color, 1.0F);
		Gizmos.line(eye, eye.add(entity.getLookAngle().scale(2.0)), color, 1.5F);
	}
}
