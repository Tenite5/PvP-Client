package pvp.client.client.mixin;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pvp.client.client.core.ModuleManager;
import pvp.client.client.modules.FreelookModule;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
	@Redirect(
		method = "turnPlayer",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V")
	)
	private void pvpClient$freelookTurn(LocalPlayer player, double xo, double yo) {
		FreelookModule freelook = ModuleManager.FREELOOK;
		if (freelook.isActive()) {
			freelook.turn(xo, yo);
		} else {
			player.turn(xo, yo);
		}
	}
}
