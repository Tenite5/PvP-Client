package io.github.tenite5.pvpclient.client.core.setting;

import net.minecraft.util.ARGB;

public class ColorSetting extends Setting {
	private int value;
	private final int defaultValue;
	private final Runnable onChange;

	public ColorSetting(String id, String name, int defaultArgb) {
		this(id, name, defaultArgb, null);
	}

	public ColorSetting(String id, String name, int defaultArgb, Runnable onChange) {
		super(id, name);
		this.value = defaultArgb;
		this.defaultValue = defaultArgb;
		this.onChange = onChange;
	}

	public int get() {
		return this.value;
	}

	public void set(int argb) {
		boolean changed = this.value != argb;
		this.value = argb;
		if (changed && this.onChange != null) {
			this.onChange.run();
		}
	}

	public void reset() {
		this.set(this.defaultValue);
	}

	public int alpha() {
		return ARGB.alpha(this.value);
	}

	public int red() {
		return ARGB.red(this.value);
	}

	public int green() {
		return ARGB.green(this.value);
	}

	public int blue() {
		return ARGB.blue(this.value);
	}

	public void setChannel(int channel, int v) {
		v = Math.clamp(v, 0, 255);
		int a = this.alpha();
		int r = this.red();
		int g = this.green();
		int b = this.blue();
		switch (channel) {
			case 0 -> a = v;
			case 1 -> r = v;
			case 2 -> g = v;
			case 3 -> b = v;
		}
		this.set(ARGB.color(a, r, g, b));
	}
}
