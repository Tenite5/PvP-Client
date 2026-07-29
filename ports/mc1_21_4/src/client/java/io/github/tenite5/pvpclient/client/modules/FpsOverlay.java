package io.github.tenite5.pvpclient.client.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import io.github.tenite5.pvpclient.client.core.HudModule;
import io.github.tenite5.pvpclient.client.core.setting.BooleanSetting;

public class FpsOverlay extends HudModule {
	private final BooleanSetting showLabel = this.addSetting(new BooleanSetting("showLabel", "Show 'FPS' Label", true));

	public FpsOverlay() {
		super("fps", "FPS", "Shows your frames per second.", 0.01, 0.02);
	}

	private String text(Minecraft minecraft) {
		int fps = minecraft.getFps();
		return this.showLabel.get() ? fps + " FPS" : Integer.toString(fps);
	}

	@Override
	public int contentWidth(Minecraft minecraft) {
		return minecraft.font.width(this.text(minecraft)) + 8;
	}

	@Override
	public int contentHeight(Minecraft minecraft) {
		return 9 + 6;
	}

	@Override
	protected void renderContent(GuiGraphics graphics, Minecraft minecraft) {
		this.drawText(graphics, minecraft, this.text(minecraft), 4, 3);
	}
}
