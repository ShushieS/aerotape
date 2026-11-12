package riley.aerosmp.tape.mixin;

import foundry.veil.api.network.handler.ServerPacketContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import riley.aerosmp.tape.PhysicsStaffGuard;

/**
 * PhysicsStaffDragPacket never checked that the sender is holding a physics staff, so a modified
 * client could drag any sublevel without one. Drop drag packets from players not holding a staff.
 *
 * Port of Sycraxe's fix.
 */
@Mixin(targets = "dev.simulated_team.simulated.network.packets.physics_staff.PhysicsStaffDragPacket", remap = false)
public abstract class PhysicsStaffDragPacketMixin {
    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void aerotape$requireStaff(ServerPacketContext context, CallbackInfo ci) {
        if (!PhysicsStaffGuard.isHolding(context.player())) {
            ci.cancel();
        }
    }
}
