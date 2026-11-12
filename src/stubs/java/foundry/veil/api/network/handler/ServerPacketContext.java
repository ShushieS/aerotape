package foundry.veil.api.network.handler;

import net.minecraft.server.level.ServerPlayer;

/** Compileonly stub of Veil's ServerPacketContext. Not packaged. */
public interface ServerPacketContext extends PacketContext {
    @Override
    ServerPlayer player();
}
