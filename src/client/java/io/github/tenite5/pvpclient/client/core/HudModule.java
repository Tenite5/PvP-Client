package io.github.tenite5.pvpclient.client.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import io.github.tenite5.pvpclient.client.core.setting.BooleanSetting;
import io.github.tenite5.pvpclient.client.core.setting.ColorSetting;

public abstract class HudModule extends Module {
	// Position stored as fraction of screen size so it survives resizes.
	private double anchorX;
	private double anchorY;
	private float scale = 1.0F;

	public final BooleanSetting textShadow = this.addSetting(new BooleanSetting("textShadow", "Text Shadow", true));
	public final ColorSetting textColor = this.addSetting(new ColorSetting("textColor", "Text Color", 0xFFFFFFFF));
	public final BooleanSetting background = this.addSetting(new BooleanSetting("background", "Background", true));
	public final ColorSetting backgroundColor = this.addSetting(new ColorSetting("backgroundColor", "Background Color", 0x90101020));

	protected HudModule(String id, String name, String description, double defaultAnchorX, double defaultAnchorY) {
		super(id, name, description);
		this.anchorX = defaultAnchorX;
		this.anchorY = defaultAnchorY;
	}

	/** Which point of the box the anchor refers to: 0 = left/top, 0.5 = center, 1 = right/bottom. */
	protected float pivotX() {
		return 0.0F;
	}

	protected float pivotY() {
		return 0.0F;
	}

	public int left(Minecraft minecraft, int guiWidth) {
		return (int) Math.round(this.anchorX * guiWidth - this.pivotX() * this.scaledWidth(minecraft));
	}

	public int top(Minecraft minecraft, int guiHeight) {
		return (int) Math.round(this.anchorY * guiHeight - this.pivotY() * this.scaledHeight(minecraft));
	}

	public void setTopLeft(Minecraft minecraft, double x, double y, int guiWidth, int guiHeight) {
		this.anchorX = (x + this.pivotX() * this.scaledWidth(minecraft)) / guiWidth;
		this.anchorY = (y + this.pivotY() * this.scaledHeight(minecraft)) / guiHeight;
	}

	public double anchorX() {
		return this.anchorX;
	}

	public double anchorY() {
		return this.anchorY;
	}

	public void setAnchors(double x, double y) {
		this.anchorX = x;
		this.anchorY = y;
	}

	public float scale() {
		return this.scale;
	}

	public void setScale(float scale) {
		this.scale = Math.clamp(scale, 0.5F, 3.0F);
	}

	/** Unscaled content size. */
	public abstract int contentWidth(Minecraft minecraft);

	public abstract int contentHeight(Minecraft minecraft);

	public int scaledWidth(Minecraft minecraft) {
		return Math.round(this.contentWidth(minecraft) * this.scale);
	}

	public int scaledHeight(Minecraft minecraft) {
		return Math.round(this.contentHeight(minecraft) * this.scale);
	}

	/** Draws content at origin (0,0) in unscaled coordinates; caller applies translation + scale. */
	protected abstract void renderContent(GuiGraphicsExtractor graphics, Minecraft minecraft);

	public void render(GuiGraphicsExtractor graphics, Minecraft minecraft) {
		int x = this.left(minecraft, graphics.guiWidth());
		int y = this.top(minecraft, graphics.guiHeight());
		graphics.pose().pushMatrix();
		graphics.pose().translate(x, y);
		graphics.pose().scale(this.scale, this.scale);
		if (this.background.get()) {
			graphics.fill(0, 0, this.contentWidth(minecraft), this.contentHeight(minecraft), this.backgroundColor.get());
		}
		this.renderContent(graphics, minecraft);
		graphics.pose().popMatrix();
	}

	protected void drawText(GuiGraphicsExtractor graphics, Minecraft minecraft, String text, int x, int y) {
		graphics.text(minecraft.font, text, x, y, this.textColor.get(), this.textShadow.get());
	}
}
