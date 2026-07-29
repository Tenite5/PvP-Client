package io.github.tenite5.pvpclient.client.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import io.github.tenite5.pvpclient.client.core.ModuleManager;
import io.github.tenite5.pvpclient.client.modules.FreelookModule;

@Mixin(Camera.class)
public class CameraMixin {
	@Redirect(
		method = "setup",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getViewYRot(F)F")
	)
	private float pvpClient$freelookYaw(Entity entity, float partialTicks) {
		FreelookModule freelook = ModuleManager.FREELOOK;
		return freelook.isActive() ? freelook.yaw() : entity.getViewYRot(partialTicks);
	}

	@Redirect(
		method = "setup",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getViewXRot(F)F")
	)
	private float pvpClient$freelookPitch(Entity entity, float partialTicks) {
		FreelookModule freelook = ModuleManager.FREELOOK;
		return freelook.isActive() ? freelook.pitch() : entity.getViewXRot(partialTicks);
	}
}
