package pvp.client.client.core.setting;

public class BooleanSetting extends Setting {
	private boolean value;
	private final boolean defaultValue;

	public BooleanSetting(String id, String name, boolean defaultValue) {
		super(id, name);
		this.value = defaultValue;
		this.defaultValue = defaultValue;
	}

	public boolean get() {
		return this.value;
	}

	public void set(boolean value) {
		this.value = value;
	}

	public void reset() {
		this.value = this.defaultValue;
	}
}
