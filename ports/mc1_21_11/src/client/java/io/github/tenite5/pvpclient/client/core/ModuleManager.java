package io.github.tenite5.pvpclient.client.core;

import java.util.ArrayList;
import java.util.List;
import io.github.tenite5.pvpclient.client.modules.ArmorOverlay;
import io.github.tenite5.pvpclient.client.modules.AtmosphereModule;
import io.github.tenite5.pvpclient.client.modules.EffectsOverlay;
import io.github.tenite5.pvpclient.client.modules.FpsOverlay;
import io.github.tenite5.pvpclient.client.modules.FreelookModule;
import io.github.tenite5.pvpclient.client.modules.HitColorModule;
import io.github.tenite5.pvpclient.client.modules.HitboxesModule;
import io.github.tenite5.pvpclient.client.modules.PingOverlay;
import io.github.tenite5.pvpclient.client.modules.SaturationModule;
import io.github.tenite5.pvpclient.client.modules.ToggleSprintModule;
import io.github.tenite5.pvpclient.client.modules.ViewModelModule;
import io.github.tenite5.pvpclient.client.modules.ZoomModule;

public final class ModuleManager {
	private static final List<Module> MODULES = new ArrayList<>();

	public static final FpsOverlay FPS = register(new FpsOverlay());
	public static final PingOverlay PING = register(new PingOverlay());
	public static final EffectsOverlay EFFECTS = register(new EffectsOverlay());
	public static final ArmorOverlay ARMOR = register(new ArmorOverlay());
	public static final ZoomModule ZOOM = register(new ZoomModule());
	public static final ViewModelModule VIEW_MODEL = register(new ViewModelModule());
	public static final HitColorModule HIT_COLOR = register(new HitColorModule());
	public static final SaturationModule SATURATION = register(new SaturationModule());
	public static final HitboxesModule HITBOXES = register(new HitboxesModule());
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
