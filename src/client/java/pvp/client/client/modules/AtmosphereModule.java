package pvp.client.client.modules;

import pvp.client.client.core.Module;
import pvp.client.client.core.setting.EnumSetting;

public class AtmosphereModule extends Module {
	public final EnumSetting weather = this.addSetting(new EnumSetting("weather", "Weather", 0, "Vanilla", "Clear", "Rain", "Thunder"));
	public final EnumSetting time = this.addSetting(new EnumSetting("time", "Time of Day", 0, "Vanilla", "Sunrise", "Day", "Noon", "Sunset", "Night", "Midnight"));

	public AtmosphereModule() {
		super("atmosphere", "Atmosphere", "Changes the weather and time of day on your screen only.");
	}

	public boolean overridesWeather() {
		return !this.weather.is("Vanilla");
	}

	public float rainOverride() {
		return this.weather.is("Clear") ? 0.0F : 1.0F;
	}

	public float thunderOverride() {
		return this.weather.is("Thunder") ? 1.0F : 0.0F;
	}

	/** Fixed clock tick within the 24000-tick day, or -1 for no override. */
	public long timeOverride() {
		return switch (this.time.index()) {
			case 1 -> 23000L;
			case 2 -> 1000L;
			case 3 -> 6000L;
			case 4 -> 12400L;
			case 5 -> 14000L;
			case 6 -> 18000L;
			default -> -1L;
		};
	}
}
