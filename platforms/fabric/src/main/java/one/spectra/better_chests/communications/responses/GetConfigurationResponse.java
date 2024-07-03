package one.spectra.better_chests.communications.responses;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record GetConfigurationResponse(boolean spread, boolean sortOnClose) implements CustomPayload {
    public static final CustomPayload.Id<GetConfigurationResponse> ID = new CustomPayload.Id<GetConfigurationResponse>(
            Identifier.of("better-chest", "get-configuration-response"));
    public static final PacketCodec<RegistryByteBuf, GetConfigurationResponse> CODEC = PacketCodec
            .tuple(
                    PacketCodecs.BOOL, GetConfigurationResponse::spread,
                    PacketCodecs.BOOL, GetConfigurationResponse::sortOnClose,
                    GetConfigurationResponse::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}