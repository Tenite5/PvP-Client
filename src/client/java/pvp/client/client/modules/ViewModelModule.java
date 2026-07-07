package pvp.client.client.modules;

import pvp.client.client.core.Module;
import pvp.client.client.core.setting.SliderSetting;

public class ViewModelModule extends Module {
	public final SliderSetting offsetX = this.addSetting(new SliderSetting("offsetX", "Position X", 0.0, -1.0, 1.0, 0.05, ""));
	public final SliderSetting offsetY = this.addSetting(new SliderSetting("offsetY", "Position Y", 0.0, -1.0, 1.0, 0.05, ""));
	public final SliderSetting offsetZ = this.addSetting(new SliderSetting("offsetZ", "Position Z", 0.0, -1.0, 1.0, 0.05, ""));
	public final SliderSetting rotationX = this.addSetting(new SliderSetting("rotationX", "Rotation X", 0.0, -90.0, 90.0, 1.0, "°"));
	public final SliderSetting rotationY = this.addSetting(new SliderSetting("rotationY", "Rotation Y", 0.0, -90.0, 90.0, 1.0, "°"));
	public final SliderSetting rotationZ = this.addSetting(new SliderSetting("rotationZ", "Rotation Z", 0.0, -90.0, 90.0, 1.0, "°"));
	public final SliderSetting scale = this.addSetting(new SliderSetting("scale", "Scale", 1.0, 0.3, 2.0, 0.05, "x"));
	public final SliderSetting shieldHeight = this.addSetting(new SliderSetting("shieldHeight", "Shield Height", 0.0, -0.5, 0.5, 0.02, ""));
	public final SliderSetting fireLoweness = this.addSetting(new SliderSetting("fireLoweness", "Fire Loweness", 0.0, 0.0, 0.6, 0.02, ""));
	public final SliderSetting totemSize = this.addSetting(new SliderSetting("totemSize", "Totem Pop Size", 1.0, 0.1, 2.0, 0.05, "x"));

	public ViewModelModule() {
		super("viewmodel", "View Model", "Change the position, rotation and scale of held items.");
	}
}
