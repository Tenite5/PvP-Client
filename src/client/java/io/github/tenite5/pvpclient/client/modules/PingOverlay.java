package io.github.tenite5.pvpclient.client.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import io.github.tenite5.pvpclient.client.core.HudModule;
import io.github.tenite5.pvpclient.client.core.setting.BooleanSetting;

public class PingOverlay extends HudModule {
	private final BooleanSetting showLabel = this.addSetting(new BooleanSetting("showLabel", "Show 'ms' Label", true));

	public PingOverlay() {
		super("ping", "Ping", "Shows your latency to the server.", 0.01, 0.07);
	}

	private int ping(Minecraft minecraft) {
		ClientPacketListener connection = minecraft.getConnection();
		if (connection == null || minecraft.player == null) {
			return 0;
		}
		PlayerInfo info = connection.getPlayerInfo(minecraft.player.getUUID());
		return info == null ? 0 : info.getLatency();
	}

	private String text(Minecraft minecraft) {
		int ping = this.ping(minecraft);
		return this.showLabel.get() ? ping + " ms" : Integer.toString(ping);
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
	protected void renderContent(GuiGraphicsExtractor graphics, Minecraft minecraft) {
		this.drawText(graphics, minecraft, this.text(minecraft), 4, 3);
	}
}
