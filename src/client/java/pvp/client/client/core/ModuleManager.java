package pvp.client.client.core;

import java.util.ArrayList;
import java.util.List;
import pvp.client.client.modules.ArmorOverlay;
import pvp.client.client.modules.AtmosphereModule;
import pvp.client.client.modules.EffectsOverlay;
import pvp.client.client.modules.FpsOverlay;
import pvp.client.client.modules.FreelookModule;
import pvp.client.client.modules.HitColorModule;
import pvp.client.client.modules.PingOverlay;
import pvp.client.client.modules.ToggleSprintModule;
import pvp.client.client.modules.ViewModelModule;
import pvp.client.client.modules.ZoomModule;

public final class ModuleManager {
	private static final List<Module> MODULES = new ArrayList<>();

	public static final FpsOverlay FPS = register(new FpsOverlay());
	public static final PingOverlay PING = register(new PingOverlay());
	public static final EffectsOverlay EFFECTS = register(new EffectsOverlay());
	public static final ArmorOverlay ARMOR = register(new ArmorOverlay());
	public static final ZoomModule ZOOM = register(new ZoomModule());
	public static final ViewModelModule VIEW_MODEL = register(new ViewModelModule());
	public static final HitColorModule HIT_COLOR = register(new HitColorModule());
	public static final ToggleSprintModule TOGGLE_SPRINT = register(new ToggleSprintModule());
	public static final FreelookModule FREELOOK = register(new FreelookModule());
	public static final AtmosphereModule ATMOSPHERE = register(new AtmosphereModule());

	private static final List<HudModule> HUD_MODULES = buildHudModules();

	private ModuleManager() {
	}

	private static <T extends Module> T register(T module) {
		MODULES.add(module);
		return module;
	}

	private static List<HudModule> buildHudModules() {
		List<HudModule> result = new ArrayList<>();
		for (Module module : MODULES) {
			if (module instanceof HudModule hud) {
				result.add(hud);
			}
		}
		return List.copyOf(result);
	}

	public static List<Module> all() {
		return MODULES;
	}

	public static List<HudModule> hudModules() {
		return HUD_MODULES;
	}
}
