package one.spectra.better_chests.communication.messages;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SortRequest(boolean sortPlayerInventory) implements CustomPayload {
    public static final CustomPayload.Id<SortRequest> ID = new CustomPayload.Id<SortRequest>(
            Identifier.of("better-chest", "sort"));
    public static final PacketCodec<RegistryByteBuf, SortRequest> CODEC = PacketCodec.tuple(PacketCodecs.BOOL,
            SortRequest::sortPlayerInventory, SortRequest::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
