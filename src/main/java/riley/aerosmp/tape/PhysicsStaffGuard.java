package riley.aerosmp.tape;

import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffItem;
import net.minecraft.world.entity.player.Player;

public final class PhysicsStaffGuard {
    private PhysicsStaffGuard() {
    }

    public static boolean isHolding(Player player) {
        return player != null && (
            player.getMainHandItem().getItem() instanceof PhysicsStaffItem ||
            player.getOffhandItem().getItem() instanceof PhysicsStaffItem
        );
    }
}
