package pvp.client.client.mixin;

import net.minecraft.client.ClientClockManager;
import net.minecraft.core.Holder;
import net.minecraft.world.clock.WorldClock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pvp.client.client.core.ModuleManager;

@Mixin(ClientClockManager.class)
public class ClientClockManagerMixin {
	@Inject(method = "getTotalTicks", at = @At("HEAD"), cancellable = true)
	private void pvpClient$fixedTime(Holder<WorldClock> definition, CallbackInfoReturnable<Long> cir) {
		if (ModuleManager.ATMOSPHERE.isEnabled()) {
			long override = ModuleManager.ATMOSPHERE.timeOverride();
			if (override >= 0) {
				cir.setReturnValue(override);
			}
		}
	}
}
