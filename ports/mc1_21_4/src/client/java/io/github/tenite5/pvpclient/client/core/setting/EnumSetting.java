package io.github.tenite5.pvpclient.client.core.setting;

public class EnumSetting extends Setting {
	private final String[] options;
	private int index;
	private final int defaultIndex;

	public EnumSetting(String id, String name, int defaultIndex, String... options) {
		super(id, name);
		this.options = options;
		this.index = defaultIndex;
		this.defaultIndex = defaultIndex;
	}

	public String get() {
		return this.options[this.index];
	}

	public int index() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = Math.floorMod(index, this.options.length);
	}

	public void cycle() {
		this.setIndex(this.index + 1);
	}

	public void reset() {
		this.index = this.defaultIndex;
	}

	public boolean is(String option) {
		return this.get().equalsIgnoreCase(option);
	}
}
