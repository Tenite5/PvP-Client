package pvp.client.client.mixin;

import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pvp.client.client.core.ModuleManager;

@Mixin(Level.class)
public class LevelMixin {
	@Inject(method = "getRainLevel", at = @At("HEAD"), cancellable = true)
	private void pvpClient$rainLevel(float a, CallbackInfoReturnable<Float> cir) {
		if (((Level) (Object) this).isClientSide() && ModuleManager.ATMOSPHERE.isEnabled() && ModuleManager.ATMOSPHERE.overridesWeather()) {
			cir.setReturnValue(ModuleManager.ATMOSPHERE.rainOverride());
		}
	}

	@Inject(method = "getThunderLevel", at = @At("HEAD"), cancellable = true)
	private void pvpClient$thunderLevel(float a, CallbackInfoReturnable<Float> cir) {
		if (((Level) (Object) this).isClientSide() && ModuleManager.ATMOSPHERE.isEnabled() && ModuleManager.ATMOSPHERE.overridesWeather()) {
			cir.setReturnValue(ModuleManager.ATMOSPHERE.thunderOverride());
		}
	}
}
