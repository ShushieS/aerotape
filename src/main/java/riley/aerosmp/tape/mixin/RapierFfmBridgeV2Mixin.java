package riley.aerosmp.tape.mixin;

import java.util.concurrent.atomic.AtomicLong;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.ryanhcode.sable.physics.ffm.RapierFfmBridgeV2", remap = false)
public abstract class RapierFfmBridgeV2Mixin {
    @Unique
    private static final AtomicLong aerotape$rejections = new AtomicLong();

    @Inject(method = "queueLinearAndAngularImpulse(JDDDDDDZ)V", at = @At("HEAD"), cancellable = true)
    private void aerotape$rejectNonFinite( long bodyId, double forceX, double forceY, double forceZ, double torqueX, double torqueY, double torqueZ, boolean wakeUp, CallbackInfo ci ) {
        if (Double.isFinite(forceX) && Double.isFinite(forceY) && Double.isFinite(forceZ) && Double.isFinite(torqueX) && Double.isFinite(torqueY) && Double.isFinite(torqueZ)) {
            return;
        }
        long count = aerotape$rejections.incrementAndGet();
        if (count <= 10 || count % 1000 == 0) {
            System.err.printf(
                "[Aerotape] Dropped non-finite Sable impulse #%d: bodyId=%d force=(%s,%s,%s) torque=(%s,%s,%s) wakeUp=%s%n",
                count, bodyId, forceX, forceY, forceZ, torqueX, torqueY, torqueZ, wakeUp
            );
        }
        ci.cancel();
    }
}
