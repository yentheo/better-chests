package one.spectra.better_chests.communications.requests;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class GetConfigurationRequest implements CustomPayload {
    public static final GetConfigurationRequest INSTANCE = new GetConfigurationRequest();

    public static final CustomPayload.Id<GetConfigurationRequest> ID = new CustomPayload.Id<GetConfigurationRequest>(
            Identifier.of("better-chest", "get-configuration"));
    public static final PacketCodec<RegistryByteBuf, GetConfigurationRequest> CODEC = PacketCodec
            .unit(INSTANCE);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}