package pvp.client.client.modules;

import net.minecraft.client.Minecraft;
import pvp.client.client.core.Module;
import pvp.client.client.core.setting.BooleanSetting;

public class ToggleSprintModule extends Module {
	public final BooleanSetting alwaysSprint = this.addSetting(new BooleanSetting("alwaysSprint", "Always Sprint", false));

	private boolean captured;
	private boolean originalToggleSprint;

	public ToggleSprintModule() {
		super("togglesprint", "Toggle Sprint", "Tap sprint once to keep sprinting, or sprint always.");
	}

	@Override
	public void tick(Minecraft minecraft) {
		if (!this.captured) {
			this.originalToggleSprint = minecraft.options.toggleSprint().get();
			this.captured = true;
		}
		boolean always = this.alwaysSprint.get();
		boolean wantToggleOption = !always;
		if (minecraft.options.toggleSprint().get() != wantToggleOption) {
			minecraft.options.toggleSprint().set(wantToggleOption);
		}
		if (always && minecraft.player != null) {
			minecraft.options.keySprint.setDown(true);
		}
	}

	@Override
	protected void onDisable() {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft == null || !this.captured) {
			return;
		}
		minecraft.options.toggleSprint().set(this.originalToggleSprint);
		minecraft.options.keySprint.setDown(false);
		this.captured = false;
	}
}
