package riley.aerosmp.tape.mixin;

import com.mojang.logging.LogUtils;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Patches the 1.20.5-1.21.1 book savestate / dupe / chunkban exploit.
 *
 * ServerboundEditBookPacket accepts titles up to 128 chars and pages up to 8192 chars, but
 * WrittenBookContent only encodes/saves titles up to 32 chars and WritableBookContent only
 * pages up to 1024 chars. An oversized book gets written onto the item, then fails to encode
 * (kicking the player) and fails to serialize (player data or chunk never saves -> savestate,
 * or a crash on chunk save). Vanilla fixed this in 24w33a by tightening the packet limits;
 * we drop any edit that exceeds what the item codecs can actually hold.
 * 
 * Referenced from papermc savestate exploit patch
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class BookSavestateMixin {
    @Unique
    private static final Logger aerotape$LOGGER = LogUtils.getLogger();

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleEditBook", at = @At("HEAD"), cancellable = true)
    private void aerotape$rejectOversizedBook(ServerboundEditBookPacket packet, CallbackInfo ci) {
        String reason = null;

        if (packet.title().isPresent() && packet.title().get().length() > WrittenBookContent.TITLE_MAX_LENGTH) {
            reason = "title length " + packet.title().get().length();
        } else if (packet.pages().size() > WritableBookContent.MAX_PAGES) {
            reason = "page count " + packet.pages().size();
        } else {
            for (String page : packet.pages()) {
                if (page.length() > WritableBookContent.PAGE_EDIT_LENGTH) {
                    reason = "page length " + page.length();
                    break;
                }
            }
        }

        if (reason != null) {
            ci.cancel();
            aerotape$LOGGER.warn(
                "[Aerotape] Rejected oversized book edit from {} ({}) - likely book savestate/dupe attempt",
                this.player.getGameProfile().getName(), reason
            );
        }
    }
}
