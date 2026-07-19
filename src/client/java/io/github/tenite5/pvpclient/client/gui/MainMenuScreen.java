package io.github.tenite5.pvpclient.client.gui;

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
import io.github.tenite5.pvpclient.client.core.ModuleManager;
import io.github.tenite5.pvpclient.client.core.setting.KeySetting;

public class MainMenuScreen extends Screen {
	private static final int CARD_W = 84;
	private static final int CARD_H = 106;
	private static final int GAP = 8;

	private static final int[] ACCENTS = {
		0xFFFFD33D, 0xFF3FB950, 0xFF9E6BFF, 0xFF39C5CF,
		0xFF4C8DFF, 0xFFFF6BD5, 0xFFE5484D, 0xFFF0883E
	};

	private boolean listeningForMenuKey;
	private double scroll;

	public MainMenuScreen() {
		super(Component.literal("PVP Client"));
	}

	private int columns() {
		int fit = Math.max(1, (this.width - 20 + GAP) / (CARD_W + GAP));
		return Math.min(fit, 4);
	}

	private int gridX() {
		int cols = this.columns();
		return (this.width - (cols * CARD_W + (cols - 1) * GAP)) / 2;
	}

	private int gridY() {
		return 42;
	}

	private int gridBottom() {
		return this.bottomBarY() - 8;
	}

	private int gridContentHeight() {
		int rows = (ModuleManager.all().size() + this.columns() - 1) / this.columns();
		return rows * (CARD_H + GAP) - GAP;
	}

	private double maxScroll() {
		return Math.max(0, this.gridContentHeight() - (this.gridBottom() - this.gridY()));
	}

	private int cardX(int index) {
		return this.gridX() + (index % this.columns()) * (CARD_W + GAP);
	}

	private int cardY(int index) {
		return this.gridY() + (index / this.columns()) * (CARD_H + GAP) - (int) this.scroll;
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
		// Title
		graphics.pose().pushMatrix();
		graphics.pose().translate(this.width / 2.0F, 12.0F);
		graphics.pose().scale(2.0F, 2.0F);
		graphics.centeredText(this.font, "PVP CLIENT", 0, 0, 0xFFFFD33D);
		graphics.pose().popMatrix();
		graphics.centeredText(this.font, "the pvp client for minecraft " + net.minecraft.SharedConstants.getCurrentVersion().name(), this.width / 2, 30, 0xFF8B8B9E);

		graphics.enableScissor(0, this.gridY() - 2, this.width, this.gridBottom());
		List<Module> modules = ModuleManager.all();
		for (int i = 0; i < modules.size(); i++) {
			int y = this.cardY(i);
			if (y + CARD_H < this.gridY() - 2 || y > this.gridBottom()) {
				continue;
			}
			this.renderCard(graphics, modules.get(i), i, mouseX, mouseY);
		}
		graphics.disableScissor();

		// Scrollbar
		double max = this.maxScroll();
		if (max > 0) {
			int trackX = this.gridX() + this.columns() * (CARD_W + GAP) - GAP + 6;
			int trackTop = this.gridY();
			int trackHeight = this.gridBottom() - trackTop;
			int barHeight = Math.max(20, (int) ((double) trackHeight * trackHeight / this.gridContentHeight()));
			int barY = trackTop + (int) ((trackHeight - barHeight) * (this.scroll / max));
			graphics.fill(trackX, trackTop, trackX + 3, trackTop + trackHeight, 0xFF1E1B2E);
			graphics.fill(trackX, barY, trackX + 3, barY + barHeight, 0xFF9E6BFF);
		}

		this.renderBottomBar(graphics, mouseX, mouseY);
	}

	private void renderCard(GuiGraphicsExtractor graphics, Module module, int index, int mouseX, int mouseY) {
		int x = this.cardX(index);
		int y = this.cardY(index);
		int accent = ACCENTS[index % ACCENTS.length];

		// Card body with accent border
		graphics.fill(x - 1, y - 1, x + CARD_W + 1, y + CARD_H + 1, accent);
		graphics.fill(x, y, x + CARD_W, y + CARD_H, 0xFF1E1B2E);
		// Accent strip along the top
		graphics.fill(x, y, x + CARD_W, y + 2, accent);

		// Icon
		PixelIcon.draw(graphics, PixelIcon.forModule(module.id()), x + (CARD_W - 32) / 2, y + 8, 4);

		// Name
		graphics.centeredText(this.font, module.name(), x + CARD_W / 2, y + 45, 0xFFFFFFFF);

		// Long activate/deactivate button
		boolean enabled = module.isEnabled();
		int bx = x + 6;
		int by = y + 58;
		int bw = CARD_W - 12;
		boolean hovered = inside(mouseX, mouseY, bx, by, bw, 16);
		int color = enabled
			? (hovered ? 0xFF4CCB60 : 0xFF3FB950)
			: (hovered ? 0xFF57536E : 0xFF433F5A);
		graphics.fill(bx, by, bx + bw, by + 16, color);
		graphics.fill(bx, by + 14, bx + bw, by + 16, 0x40000000);
		graphics.centeredText(this.font, enabled ? "Deactivate" : "Activate", x + CARD_W / 2, by + 4, 0xFFFFFFFF);

		// Like + edit, touching
		int half = bw / 2;
		int ly = by + 20;
		boolean likeHover = inside(mouseX, mouseY, bx, ly, half, 16);
		boolean editHover = inside(mouseX, mouseY, bx + half, ly, bw - half, 16);
		int likeColor = module.isLiked()
			? (likeHover ? 0xFFFF7FDD : 0xFFFF6BD5)
			: (likeHover ? 0xFF57536E : 0xFF433F5A);
		int editColor = editHover ? 0xFF5C9BFF : 0xFF4C8DFF;
		graphics.fill(bx, ly, bx + half, ly + 16, likeColor);
		graphics.fill(bx + half, ly, bx + bw, ly + 16, editColor);
		graphics.fill(bx, ly + 14, bx + bw, ly + 16, 0x40000000);
		graphics.centeredText(this.font, "Like", bx + half / 2, ly + 4, 0xFFFFFFFF);
		graphics.centeredText(this.font, "Edit", bx + half + (bw - half) / 2, ly + 4, 0xFFFFFFFF);
	}

	private int bottomBarY() {
		return this.height - 26;
	}

	private int hudButtonX() {
		return this.width / 2 - 115;
	}

	private int keyButtonX() {
		return this.width / 2 - 15;
	}

	private void renderBottomBar(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		int y = this.bottomBarY();

		int hx = this.hudButtonX();
		boolean hudHover = inside(mouseX, mouseY, hx, y, 90, 18);
		graphics.fill(hx - 1, y - 1, hx + 91, y + 19, 0xFF39C5CF);
		graphics.fill(hx, y, hx + 90, y + 18, hudHover ? 0xFF2A2740 : 0xFF1E1B2E);
		graphics.centeredText(this.font, "Edit HUD", hx + 45, y + 5, 0xFF39C5CF);

		int kx = this.keyButtonX();
		boolean keyHover = inside(mouseX, mouseY, kx, y, 130, 18);
		graphics.fill(kx - 1, y - 1, kx + 131, y + 19, 0xFF9E6BFF);
		graphics.fill(kx, y, kx + 130, y + 18, keyHover ? 0xFF2A2740 : 0xFF1E1B2E);
		String label = this.listeningForMenuKey ? "Press key/button..." : "Menu Key: " + KeySetting.displayOf(ConfigManager.menuKey);
		graphics.centeredText(this.font, label, kx + 65, y + 5, this.listeningForMenuKey ? 0xFFFFD33D : 0xFF9E6BFF);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (this.listeningForMenuKey) {
			ConfigManager.menuKey = KeySetting.fromMouseButton(event.button());
			this.listeningForMenuKey = false;
			this.playClick();
			return true;
		}
		if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			if (event.y() >= this.gridY() - 2 && event.y() < this.gridBottom()) {
				List<Module> modules = ModuleManager.all();
				for (int i = 0; i < modules.size(); i++) {
					Module module = modules.get(i);
					int x = this.cardX(i);
					int y = this.cardY(i);
					int bx = x + 6;
					int bw = CARD_W - 12;
					int half = bw / 2;
					if (inside(event.x(), event.y(), bx, y + 58, bw, 16)) {
						module.toggle();
						this.playClick();
						return true;
					}
					if (inside(event.x(), event.y(), bx, y + 78, half, 16)) {
						module.setLiked(!module.isLiked());
						this.playClick();
						return true;
					}
					if (inside(event.x(), event.y(), bx + half, y + 78, bw - half, 16)) {
						this.playClick();
						this.minecraft.gui.setScreen(new ModuleSettingsScreen(module, this));
						return true;
					}
				}
			}
			int by = this.bottomBarY();
			if (inside(event.x(), event.y(), this.hudButtonX(), by, 90, 18)) {
				this.playClick();
				this.minecraft.gui.setScreen(new HudEditScreen());
				return true;
			}
			if (inside(event.x(), event.y(), this.keyButtonX(), by, 130, 18)) {
				this.playClick();
				this.listeningForMenuKey = true;
				return true;
			}
		}
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
		this.scroll = Math.clamp(this.scroll - scrollY * 20.0, 0.0, this.maxScroll());
		return true;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (this.listeningForMenuKey) {
			ConfigManager.menuKey = event.isEscape() ? GLFW.GLFW_KEY_UNKNOWN : event.key();
			this.listeningForMenuKey = false;
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public void onClose() {
		ConfigManager.save();
		super.onClose();
	}
}
