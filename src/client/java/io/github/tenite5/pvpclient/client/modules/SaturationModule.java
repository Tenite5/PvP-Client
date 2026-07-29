package io.github.tenite5.pvpclient.client.modules;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import io.github.tenite5.pvpclient.client.core.Module;
import io.github.tenite5.pvpclient.client.core.setting.BooleanSetting;
import io.github.tenite5.pvpclient.client.core.setting.ColorSetting;

public class SaturationModule extends Module {
	// AppleSkin-style quarter, half, three-quarter and full hunger-shank outlines.
	// Digits select progressively brighter shades of the configured color.
	private static final String[][] OUTLINES = {
		{
			".........",
			".........",
			".........",
			".........",
			"......1..",
			"......1..",
			".....0.11",
			"......0.1",
			"......00."
		},
		{
			"...3.....",
			"....2....",
			".....1...",
			"......1..",
			"......1..",
			"......1..",
			"....00.11",
			"......0.1",
			"......00."
		},
		{
			"..33.....",
			"....2....",
			".....1...",
			"......1..",
			"......1..",
			"..0...1..",
			"...000.11",
			"......0.1",
			"......00."
		},
		{
			"..33.....",
			".2..2....",
			"1....1...",
			"1.....1..",
			".0....1..",
			"..0...1..",
			"...000.11",
			"......0.1",
			"......00."
		}
	};
	private static final float[] SHADE_BRIGHTNESS = {0.45F, 0.68F, 0.86F, 1.0F};

	public final BooleanSetting showCurrent = this.addSetting(new BooleanSetting("showCurrent", "Show Current Saturation", true));
	public final ColorSetting currentColor = this.addSetting(new ColorSetting("currentColor", "Current Color", 0xFFE9D262));
	public final BooleanSetting showFuture = this.addSetting(new BooleanSetting("showFuture", "Show Food Preview", true));
	public final ColorSetting futureColor = this.addSetting(new ColorSetting("futureColor", "Preview Color", 0xFFE9D262));
	public final BooleanSetting flashFuture = this.addSetting(new BooleanSetting("flashFuture", "Flash Food Preview", true));

	public SaturationModule() {
		super("saturation", "Saturation", "Shows current saturation and the saturation food will add.");
	}

	public void render(GuiGraphicsExtractor graphics, Player player, int y, int right) {
		if (!this.isEnabled()) {
			return;
		}

		FoodData foodData = player.getFoodData();
		float current = Math.clamp(foodData.getSaturationLevel(), 0.0F, 20.0F);
		if (this.showCurrent.get() && current > 0.0F) {
			drawSaturation(graphics, right, y, current, 0.0F, this.currentColor.get());
		}

		if (!this.showFuture.get()) {
			return;
		}
		FoodProperties food = heldFood(player);
		if (food == null || !(foodData.needsFood() || food.canAlwaysEat())) {
			return;
		}

		float resultingFood = Math.min(20.0F, foodData.getFoodLevel() + food.nutrition());
		float resultingSaturation = Math.min(resultingFood, current + food.saturation());
		if (resultingSaturation <= current) {
			return;
		}

		int color = this.futureColor.get();
		if (this.flashFuture.get()) {
			double wave = (Math.sin(System.currentTimeMillis() / 180.0) + 1.0) * 0.5;
			float alphaMultiplier = (float) (0.35 + wave * 0.65);
			color = ARGB.color(
				Math.round(ARGB.alpha(color) * alphaMultiplier),
				ARGB.red(color),
				ARGB.green(color),
				ARGB.blue(color)
			);
		}
		drawSaturation(graphics, right, y, current, resultingSaturation - current, color);
	}

	private static FoodProperties heldFood(Player player) {
		ItemStack mainHand = player.getMainHandItem();
		FoodProperties food = mainHand.get(DataComponents.FOOD);
		if (food != null) {
			return food;
		}
		return player.getOffhandItem().get(DataComponents.FOOD);
	}

	private static void drawSaturation(
		GuiGraphicsExtractor graphics, int right, int y, float saturationLevel, float saturationGained, int color
	) {
		float modifiedSaturation = Math.clamp(saturationLevel + saturationGained, 0.0F, 20.0F);
		int firstIcon = saturationGained == 0.0F ? 0 : (int) Math.max(saturationLevel / 2.0F, 0.0F);
		int lastIcon = (int) Math.ceil(modifiedSaturation / 2.0F);

		for (int icon = firstIcon; icon < lastIcon; icon++) {
			float effective = modifiedSaturation / 2.0F - icon;
			int variant = effective >= 1.0F ? 3 : effective > 0.5F ? 2 : effective > 0.25F ? 1 : 0;
			drawOutline(graphics, right - icon * 8 - 9, y, OUTLINES[variant], color);
		}
	}

	private static void drawOutline(GuiGraphicsExtractor graphics, int x, int y, String[] outline, int color) {
		int[] shades = new int[SHADE_BRIGHTNESS.length];
		for (int i = 0; i < shades.length; i++) {
			float brightness = SHADE_BRIGHTNESS[i];
			shades[i] = ARGB.color(
				ARGB.alpha(color),
				Math.round(ARGB.red(color) * brightness),
				Math.round(ARGB.green(color) * brightness),
				Math.round(ARGB.blue(color) * brightness)
			);
		}

		for (int row = 0; row < outline.length; row++) {
			String pixels = outline[row];
			for (int column = 0; column < pixels.length(); column++) {
				char pixel = pixels.charAt(column);
				if (pixel != '.') {
					graphics.fill(x + column, y + row, x + column + 1, y + row + 1, shades[pixel - '0']);
				}
			}
		}
	}
}
