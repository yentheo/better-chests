package one.spectra.better_chests.communications.requests;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ConfigureChestRequest(boolean spread, boolean sortOnClose) implements CustomPayload {
    public static final CustomPayload.Id<ConfigureChestRequest> ID = new CustomPayload.Id<ConfigureChestRequest>(
            Identifier.of("better-chest", "configure-chest"));
    public static final PacketCodec<RegistryByteBuf, ConfigureChestRequest> CODEC = PacketCodec
            .tuple(
                    PacketCodecs.BOOL, ConfigureChestRequest::spread,
                    PacketCodecs.BOOL, ConfigureChestRequest::sortOnClose,
                    ConfigureChestRequest::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
