package io.github.tenite5.pvpclient.client.modules;

import com.google.common.collect.Ordering;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import io.github.tenite5.pvpclient.client.core.HudModule;
import io.github.tenite5.pvpclient.client.core.setting.BooleanSetting;
import io.github.tenite5.pvpclient.client.core.setting.EnumSetting;

public class EffectsOverlay extends HudModule {
	private static final String[] LEVELS = {"", "", " II", " III", " IV", " V", " VI", " VII", " VIII", " IX", " X"};
	public final BooleanSetting showTimer = this.addSetting(new BooleanSetting("showTimer", "Show Timer", true));
	private final BooleanSetting showName = this.addSetting(new BooleanSetting("showName", "Show Name", true));
	public final BooleanSetting hideVanilla = this.addSetting(new BooleanSetting("hideVanilla", "Hide Vanilla Effects", true));
	private final EnumSetting anchor = this.addSetting(
		new EnumSetting("anchor", "Anchor", 1, "Top Left", "Top Right", "Center", "Bottom Left", "Bottom Right")
	);

	private record CachedEffect(Holder<MobEffect> effect, String label, String timer) {
	}

	// Rebuilt once per client tick instead of several times per frame.
	private List<CachedEffect> cached = List.of();
	private int cachedWidth = 60;

	public EffectsOverlay() {
		super("effects", "Effects", "Shows your potion effects with timers.", 0.8, 0.3);
	}

	@Override
	protected float pivotX() {
		return switch (this.anchor.index()) {
			case 1, 4 -> 1.0F;
			case 2 -> 0.5F;
			default -> 0.0F;
		};
	}

	@Override
	protected float pivotY() {
		return switch (this.anchor.index()) {
			case 3, 4 -> 1.0F;
			case 2 -> 0.5F;
			default -> 0.0F;
		};
	}

	private static String label(MobEffectInstance instance) {
		MobEffect effect = instance.getEffect().value();
		String name = effect.getDisplayName().getString();
		int amplifier = instance.getAmplifier() + 1;
		String level = amplifier >= 0 && amplifier < LEVELS.length ? LEVELS[amplifier] : " " + amplifier;
		return name + level;
	}

	private static String timer(MobEffectInstance instance) {
		if (instance.isInfiniteDuration()) {
			return "∞";
		}
		int seconds = instance.getDuration() / 20;
		return (seconds / 60) + ":" + String.format("%02d", seconds % 60);
	}

	@Override
	public void tick(Minecraft minecraft) {
		if (minecraft.player == null) {
			this.cached = List.of();
			this.cachedWidth = 60;
			return;
		}
		List<MobEffectInstance> effects = Ordering.natural().reverse().sortedCopy(minecraft.player.getActiveEffects());
		List<CachedEffect> result = new ArrayList<>(effects.size());
		int width = 0;
		for (MobEffectInstance instance : effects) {
			CachedEffect entry = new CachedEffect(instance.getEffect(), label(instance), timer(instance));
			result.add(entry);
			int w = 24;
			if (this.showName.get()) {
				w += 3 + minecraft.font.width(entry.label());
			}
			if (this.showTimer.get()) {
				w += 3 + minecraft.font.width(entry.timer());
			}
			width = Math.max(width, w);
		}
		this.cached = result;
		this.cachedWidth = result.isEmpty() ? 60 : width + 4;
	}

	@Override
	public int contentWidth(Minecraft minecraft) {
		return this.cachedWidth;
	}

	@Override
	public int contentHeight(Minecraft minecraft) {
		return Math.max(1, this.cached.size()) * 24 + 2;
	}

	@Override
	protected void renderContent(GuiGraphicsExtractor graphics, Minecraft minecraft) {
		if (this.cached.isEmpty()) {
			this.drawText(graphics, minecraft, "No effects", 4, 8);
			return;
		}
		int y = 2;
		for (CachedEffect entry : this.cached) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Hud.getMobEffectSprite(entry.effect()), 2, y + 1, 18, 18);
			int textX = 24;
			int textY = y + 6;
			if (this.showName.get()) {
				this.drawText(graphics, minecraft, entry.label(), textX, textY);
				textX += minecraft.font.width(entry.label()) + 3;
			}
			if (this.showTimer.get()) {
				this.drawText(graphics, minecraft, entry.timer(), textX, textY);
			}
			y += 24;
		}
	}
}
