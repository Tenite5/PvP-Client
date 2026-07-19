package io.github.tenite5.pvpclient.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;
import io.github.tenite5.pvpclient.client.core.ConfigManager;
import io.github.tenite5.pvpclient.client.core.Module;
import io.github.tenite5.pvpclient.client.core.setting.BooleanSetting;
import io.github.tenite5.pvpclient.client.core.setting.ColorSetting;
import io.github.tenite5.pvpclient.client.core.setting.EnumSetting;
import io.github.tenite5.pvpclient.client.core.setting.KeySetting;
import io.github.tenite5.pvpclient.client.core.setting.Setting;
import io.github.tenite5.pvpclient.client.core.setting.SliderSetting;

public class ModuleSettingsScreen extends Screen {
	private static final int ROW_H = 20;
	private static final int CONTROL_W = 100;
	private static final String[] CHANNEL_NAMES = {"Alpha", "Red", "Green", "Blue"};
	private static final int[] CHANNEL_COLORS = {0xFFC6C6C6, 0xFFE5484D, 0xFF3FB950, 0xFF4C8DFF};

	/** channel: -1 = plain setting row, -2 = color header, 0..3 = ARGB channel slider. */
	private record Row(Setting setting, int channel) {
	}

	private final Module module;
	private final Screen parent;
	private final List<Row> rows = new ArrayList<>();
	private double scroll;
	private Row dragging;
	private Row listeningKey;

	public ModuleSettingsScreen(Module module, Screen parent) {
		super(Component.literal(module.name() + " Settings"));
		this.module = module;
		this.parent = parent;
		for (Setting setting : module.settings()) {
			if (setting instanceof ColorSetting) {
				this.rows.add(new Row(setting, -2));
				for (int channel = 0; channel < 4; channel++) {
					this.rows.add(new Row(setting, channel));
				}
			} else {
				this.rows.add(new Row(setting, -1));
			}
		}
	}

	private int panelW() {
		return Math.min(320, this.width - 24);
	}

	private int panelX() {
		return (this.width - this.panelW()) / 2;
	}

	private int listTop() {
		return 46;
	}

	private int listBottom() {
		return this.height - 36;
	}

	private int rowY(int index) {
		return this.listTop() + index * ROW_H - (int) Math.round(this.scroll);
	}

	private int controlX() {
		return this.panelX() + this.panelW() - CONTROL_W - 8;
	}

	private int doneX() {
		return this.width / 2 - 50;
	}

	private int doneY() {
		return this.height - 28;
	}

	private double maxScroll() {
		return Math.max(0, this.rows.size() * ROW_H - (this.listBottom() - this.listTop()));
	}

	private static boolean inside(double mx, double my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	private void playClick() {
		this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		graphics.fill(0, 0, this.width, this.height, 0xC80D0B18);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		int px = this.panelX();
		int pw = this.panelW();

		graphics.centeredText(this.font, this.module.name(), this.width / 2, 12, 0xFFFFD33D);
		graphics.centeredText(this.font, this.module.description(), this.width / 2, 26, 0xFF8B8B9E);

		// Panel frame
		graphics.fill(px - 2, this.listTop() - 4, px + pw + 2, this.listBottom() + 4, 0xFF433F5A);
		graphics.fill(px - 1, this.listTop() - 3, px + pw + 1, this.listBottom() + 3, 0xFF1E1B2E);

		graphics.enableScissor(px, this.listTop(), px + pw, this.listBottom());
		for (int i = 0; i < this.rows.size(); i++) {
			int y = this.rowY(i);
			if (y + ROW_H < this.listTop() || y > this.listBottom()) {
				continue;
			}
			this.renderRow(graphics, this.rows.get(i), y, mouseX, mouseY);
		}
		graphics.disableScissor();

		if (this.maxScroll() > 0) {
			int trackH = this.listBottom() - this.listTop();
			int barH = Math.max(10, (int) (trackH * trackH / (double) (this.rows.size() * ROW_H)));
			int barY = this.listTop() + (int) ((trackH - barH) * (this.scroll / this.maxScroll()));
			graphics.fill(px + pw - 2, this.listTop(), px + pw, this.listBottom(), 0xFF2A2740);
			graphics.fill(px + pw - 2, barY, px + pw, barY + barH, 0xFF9E6BFF);
		}

		// Done button
		int dx = this.doneX();
		int dy = this.doneY();
		boolean hover = inside(mouseX, mouseY, dx, dy, 100, 18);
		graphics.fill(dx - 1, dy - 1, dx + 101, dy + 19, 0xFF3FB950);
		graphics.fill(dx, dy, dx + 100, dy + 18, hover ? 0xFF2A2740 : 0xFF1E1B2E);
		graphics.centeredText(this.font, "Done", dx + 50, dy + 5, 0xFF3FB950);
	}

	private void renderRow(GuiGraphicsExtractor graphics, Row row, int y, int mouseX, int mouseY) {
		int px = this.panelX();
		int cx = this.controlX();
		boolean channelRow = row.channel() >= 0;
		int nameX = px + (channelRow ? 16 : 8);
		String name = channelRow ? CHANNEL_NAMES[row.channel()] : row.setting().name();
		graphics.text(this.font, name, nameX, y + 6, channelRow ? 0xFF8B8B9E : 0xFFFFFFFF, false);

		switch (row.setting()) {
			case BooleanSetting s when row.channel() == -1 -> {
				boolean on = s.get();
				boolean hover = inside(mouseX, mouseY, cx, y + 4, CONTROL_W, 12);
				int bg = on ? (hover ? 0xFF4CCB60 : 0xFF3FB950) : (hover ? 0xFF57536E : 0xFF433F5A);
				graphics.fill(cx, y + 4, cx + CONTROL_W, y + 16, bg);
				graphics.centeredText(this.font, on ? "ON" : "OFF", cx + CONTROL_W / 2, y + 6, 0xFFFFFFFF);
			}
			case SliderSetting s when row.channel() == -1 -> {
				this.renderSlider(graphics, cx, y, (s.get() - s.min()) / (s.max() - s.min()), 0xFFFFD33D);
				graphics.text(this.font, s.display(), cx - this.font.width(s.display()) - 6, y + 6, 0xFF8B8B9E, false);
			}
			case EnumSetting s when row.channel() == -1 -> {
				boolean hover = inside(mouseX, mouseY, cx, y + 4, CONTROL_W, 12);
				graphics.fill(cx, y + 4, cx + CONTROL_W, y + 16, hover ? 0xFF5C9BFF : 0xFF4C8DFF);
				graphics.centeredText(this.font, s.get(), cx + CONTROL_W / 2, y + 6, 0xFFFFFFFF);
			}
			case KeySetting s when row.channel() == -1 -> {
				boolean listening = this.listeningKey == row;
				boolean hover = inside(mouseX, mouseY, cx, y + 4, CONTROL_W, 12);
				graphics.fill(cx, y + 4, cx + CONTROL_W, y + 16, listening ? 0xFFFFD33D : (hover ? 0xFFAF82FF : 0xFF9E6BFF));
				graphics.centeredText(this.font, listening ? "Press key/button..." : s.display(), cx + CONTROL_W / 2, y + 6, listening ? 0xFF1E1B2E : 0xFFFFFFFF);
			}
			case ColorSetting s when row.channel() == -2 -> {
				graphics.fill(cx + CONTROL_W - 25, y + 3, cx + CONTROL_W + 1, y + 17, 0xFFFFFFFF);
				graphics.fill(cx + CONTROL_W - 24, y + 4, cx + CONTROL_W, y + 16, s.get());
			}
			case ColorSetting s when row.channel() >= 0 -> {
				int channel = row.channel();
				int value = switch (channel) {
					case 0 -> s.alpha();
					case 1 -> s.red();
					case 2 -> s.green();
					default -> s.blue();
				};
				this.renderSlider(graphics, cx, y, value / 255.0, CHANNEL_COLORS[channel]);
				String text = Integer.toString(value);
				graphics.text(this.font, text, cx - this.font.width(text) - 6, y + 6, 0xFF8B8B9E, false);
			}
			default -> {
			}
		}
	}

	private void renderSlider(GuiGraphicsExtractor graphics, int cx, int y, double fraction, int knobColor) {
		fraction = Math.clamp(fraction, 0.0, 1.0);
		graphics.fill(cx, y + 8, cx + CONTROL_W, y + 12, 0xFF433F5A);
		graphics.fill(cx, y + 8, cx + (int) Math.round(fraction * CONTROL_W), y + 12, (knobColor & 0x00FFFFFF) | 0x80000000);
		int knobX = cx + (int) Math.round(fraction * (CONTROL_W - 4));
		graphics.fill(knobX, y + 4, knobX + 4, y + 16, knobColor);
	}

	private void applySliderDrag(Row row, double mouseX) {
		double fraction = Math.clamp((mouseX - this.controlX()) / CONTROL_W, 0.0, 1.0);
		if (row.setting() instanceof SliderSetting s && row.channel() == -1) {
			s.set(s.min() + fraction * (s.max() - s.min()));
		} else if (row.setting() instanceof ColorSetting s && row.channel() >= 0) {
			s.setChannel(row.channel(), (int) Math.round(fraction * 255.0));
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (this.listeningKey != null) {
			if (this.listeningKey.setting() instanceof KeySetting s) {
				s.set(KeySetting.fromMouseButton(event.button()));
			}
			this.listeningKey = null;
			this.playClick();
			return true;
		}
		if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			if (inside(event.x(), event.y(), this.doneX(), this.doneY(), 100, 18)) {
				this.playClick();
				this.onClose();
				return true;
			}
			if (event.y() >= this.listTop() && event.y() < this.listBottom()) {
				int cx = this.controlX();
				for (int i = 0; i < this.rows.size(); i++) {
					Row row = this.rows.get(i);
					int y = this.rowY(i);
					if (!inside(event.x(), event.y(), cx, y + 2, CONTROL_W, 16)) {
						continue;
					}
					switch (row.setting()) {
						case BooleanSetting s when row.channel() == -1 -> {
							s.set(!s.get());
							this.playClick();
						}
						case EnumSetting s when row.channel() == -1 -> {
							s.cycle();
							this.playClick();
						}
						case KeySetting s when row.channel() == -1 -> {
							this.listeningKey = row;
							this.playClick();
						}
						case SliderSetting s when row.channel() == -1 -> {
							this.dragging = row;
							this.applySliderDrag(row, event.x());
						}
						case ColorSetting s when row.channel() >= 0 -> {
							this.dragging = row;
							this.applySliderDrag(row, event.x());
						}
						default -> {
						}
					}
					return true;
				}
			}
		}
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
		if (this.dragging != null) {
			this.applySliderDrag(this.dragging, event.x());
			return true;
		}
		return super.mouseDragged(event, dx, dy);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		this.dragging = null;
		return super.mouseReleased(event);
	}

	@Override
	public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
		this.scroll = Math.clamp(this.scroll - scrollY * 14.0, 0.0, this.maxScroll());
		return true;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (this.listeningKey != null) {
			if (this.listeningKey.setting() instanceof KeySetting s) {
				s.set(event.isEscape() ? GLFW.GLFW_KEY_UNKNOWN : event.key());
			}
			this.listeningKey = null;
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public void onClose() {
		ConfigManager.save();
		this.minecraft.gui.setScreen(this.parent);
	}
}
