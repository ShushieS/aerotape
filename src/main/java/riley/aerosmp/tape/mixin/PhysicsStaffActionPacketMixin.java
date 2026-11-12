package riley.aerosmp.tape.mixin;

import foundry.veil.api.network.handler.PacketContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import riley.aerosmp.tape.PhysicsStaffGuard;

/**
 * Silently ignore physics staff actions (lock / stop drag) from players not holding a staff,
 * instead of kicking them with "Invalid packet". Legit clients send STOP_DRAG right after swapping
 * off the staff mid-drag, so the old kick hit normal players. The server already ends drag
 * sessions for players no longer holding a staff (PhysicsStaffServerHandler#tick), so nothing is lost.
 * No logging here since legit clients hit this path routinely.
 *
 * Port of Sycraxe's fix.
 */
@Mixin(targets = "dev.simulated_team.simulated.network.packets.physics_staff.PhysicsStaffActionPacket", remap = false)
public abstract class PhysicsStaffActionPacketMixin {
    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void aerotape$requireStaff(PacketContext context, CallbackInfo ci) {
        if (!PhysicsStaffGuard.isHolding(context.player())) {
            ci.cancel();
        }
    }
}
