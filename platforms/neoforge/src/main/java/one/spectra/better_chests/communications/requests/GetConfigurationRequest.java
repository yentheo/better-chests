package one.spectra.better_chests.communications.requests;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record GetConfigurationRequest() implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<GetConfigurationRequest> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath("better-chest", "get-configuration"));

    public static final StreamCodec<ByteBuf, GetConfigurationRequest> STREAM_CODEC = StreamCodec.unit(new GetConfigurationRequest());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
