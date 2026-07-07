package pvp.client.client.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pvp.client.client.core.ModuleManager;
import pvp.client.client.modules.FreelookModule;

@Mixin(Camera.class)
public class CameraMixin {
	@Inject(method = "calculateFov", at = @At("RETURN"), cancellable = true)
	private void pvpClient$applyZoom(float partialTicks, CallbackInfoReturnable<Float> cir) {
		float multiplier = ModuleManager.ZOOM.fovMultiplier();
		if (multiplier != 1.0F) {
			cir.setReturnValue(cir.getReturnValueF() * multiplier);
		}
	}

	@Redirect(
		method = "alignWithEntity",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getViewYRot(F)F")
	)
	private float pvpClient$freelookYaw(Entity entity, float partialTicks) {
		FreelookModule freelook = ModuleManager.FREELOOK;
		return freelook.isActive() ? freelook.yaw() : entity.getViewYRot(partialTicks);
	}

	@Redirect(
		method = "alignWithEntity",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getViewXRot(F)F")
	)
	private float pvpClient$freelookPitch(Entity entity, float partialTicks) {
		FreelookModule freelook = ModuleManager.FREELOOK;
		return freelook.isActive() ? freelook.pitch() : entity.getViewXRot(partialTicks);
	}
}
