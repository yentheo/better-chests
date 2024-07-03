package one.spectra.better_chests.communications.responses;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record GetConfigurationResponse(boolean spread, boolean sortOnClose) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GetConfigurationResponse> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath("better-chest", "get-configuration-response"));

    public static final StreamCodec<ByteBuf, GetConfigurationResponse> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, GetConfigurationResponse::spread,
            ByteBufCodecs.BOOL, GetConfigurationResponse::sortOnClose,
            GetConfigurationResponse::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
