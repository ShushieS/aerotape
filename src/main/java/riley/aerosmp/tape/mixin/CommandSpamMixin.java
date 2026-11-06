package riley.aerosmp.tape.mixin;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class CommandSpamMixin {
    @Redirect(
        method = {
            "name=/^lambda\\$handleChatCommand\\$\\d+$/ desc=/ServerboundChatCommandPacket;/",
            "name=/^lambda\\$handleSignedChatCommand\\$\\d+$/ desc=/ServerboundChatCommandSignedPacket;/"
        },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;detectRateSpam()V"),
        require = 0,
        remap = false
    )
    private void aerotape$skipCommandSpamCheck(ServerGamePacketListenerImpl listener) {
    }
}
