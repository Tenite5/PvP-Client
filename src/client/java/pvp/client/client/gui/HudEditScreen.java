package pvp.client.client.gui;

import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import pvp.client.client.core.ConfigManager;
import pvp.client.client.core.HudModule;
import pvp.client.client.core.ModuleManager;

public class HudEditScreen extends Screen {
	private static final int BALL = 6;

	private HudModule draggingModule;
	private double grabOffsetX;
	private double grabOffsetY;
	private HudModule resizingModule;

	public HudEditScreen() {
		super(Component.literal("Edit HUD"));
	}

	private int moduleX(HudModule module) {
		return module.left(this.minecraft, this.width);
	}

	private int moduleY(HudModule module) {
		return module.top(this.minecraft, this.height);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		graphics.fill(0, 0, this.width, this.height, 0x50000000);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		graphics.centeredText(this.font, "Drag overlays to move them", this.width / 2, 8, 0xFFFFD33D);
		graphics.centeredText(this.font, "Drag the ball in the corner to resize - ESC when done", this.width / 2, 20, 0xFF8B8B9E);

		for (HudModule module : ModuleManager.hudModules()) {
			if (!module.isEnabled()) {
				continue;
			}
			int x = this.moduleX(module);
			int y = this.moduleY(module);
			int w = module.scaledWidth(this.minecraft);
			int h = module.scaledHeight(this.minecraft);

			module.render(graphics, this.minecraft);

			boolean active = module == this.draggingModule || module == this.resizingModule;
			int border = active ? 0xFFFFD33D : 0xFF39C5CF;
			graphics.fill(x - 1, y - 1, x + w + 1, y, border);
			graphics.fill(x - 1, y + h, x + w + 1, y + h + 1, border);
			graphics.fill(x - 1, y, x, y + h, border);
			graphics.fill(x + w, y, x + w + 1, y + h, border);

			// Resize ball at the bottom-right corner
			int ballX = x + w - BALL / 2;
			int ballY = y + h - BALL / 2;
			graphics.fill(ballX, ballY + 1, ballX + BALL, ballY + BALL - 1, 0xFFFFFFFF);
			graphics.fill(ballX + 1, ballY, ballX + BALL - 1, ballY + BALL, 0xFFFFFFFF);
			graphics.fill(ballX + 1, ballY + 1, ballX + BALL - 1, ballY + BALL - 1, border);
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			List<HudModule> modules = ModuleManager.hudModules();
			for (int i = modules.size() - 1; i >= 0; i--) {
				HudModule module = modules.get(i);
				if (!module.isEnabled()) {
					continue;
				}
				int x = this.moduleX(module);
				int y = this.moduleY(module);
				int w = module.scaledWidth(this.minecraft);
				int h = module.scaledHeight(this.minecraft);

				double ballCenterX = x + w;
				double ballCenterY = y + h;
				if (Math.abs(event.x() - ballCenterX) <= BALL && Math.abs(event.y() - ballCenterY) <= BALL) {
					this.resizingModule = module;
					return true;
				}
				if (event.x() >= x && event.x() < x + w && event.y() >= y && event.y() < y + h) {
					this.draggingModule = module;
					this.grabOffsetX = event.x() - x;
					this.grabOffsetY = event.y() - y;
					return true;
				}
			}
		}
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
		if (this.resizingModule != null) {
			HudModule module = this.resizingModule;
			int x = this.moduleX(module);
			int y = this.moduleY(module);
			float scaleX = (float) ((event.x() - x) / Math.max(1, module.contentWidth(this.minecraft)));
			float scaleY = (float) ((event.y() - y) / Math.max(1, module.contentHeight(this.minecraft)));
			module.setScale(Math.max(scaleX, scaleY));
			return true;
		}
		if (this.draggingModule != null) {
			HudModule module = this.draggingModule;
			int w = module.scaledWidth(this.minecraft);
			int h = module.scaledHeight(this.minecraft);
			double x = Math.clamp(event.x() - this.grabOffsetX, 0, Math.max(0, this.width - w));
			double y = Math.clamp(event.y() - this.grabOffsetY, 0, Math.max(0, this.height - h));
			module.setTopLeft(this.minecraft, x, y, this.width, this.height);
			return true;
		}
		return super.mouseDragged(event, dx, dy);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		this.draggingModule = null;
		this.resizingModule = null;
		return super.mouseReleased(event);
	}

	@Override
	public void onClose() {
		ConfigManager.save();
		this.minecraft.gui.setScreen(new MainMenuScreen());
	}
}
