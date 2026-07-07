package pvp.client.client.core;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import pvp.client.client.core.setting.Setting;

public abstract class Module {
	private final String id;
	private final String name;
	private final String description;
	private boolean enabled;
	private boolean liked;
	private final List<Setting> settings = new ArrayList<>();

	protected Module(String id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public String id() {
		return this.id;
	}

	public String name() {
		return this.name;
	}

	public String description() {
		return this.description;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		if (this.enabled == enabled) {
			return;
		}
		this.enabled = enabled;
		if (enabled) {
			this.onEnable();
		} else {
			this.onDisable();
		}
	}

	public void toggle() {
		this.setEnabled(!this.enabled);
	}

	public boolean isLiked() {
		return this.liked;
	}

	public void setLiked(boolean liked) {
		this.liked = liked;
	}

	protected <T extends Setting> T addSetting(T setting) {
		this.settings.add(setting);
		return setting;
	}

	public List<Setting> settings() {
		return this.settings;
	}

	protected void onEnable() {
	}

	protected void onDisable() {
	}

	public void tick(Minecraft minecraft) {
	}
}
