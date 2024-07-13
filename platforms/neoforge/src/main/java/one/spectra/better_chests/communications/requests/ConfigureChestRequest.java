package one.spectra.better_chests.communications.requests;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ConfigureChestRequest(boolean spread, boolean sortOnClose) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ConfigureChestRequest> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath("better-chest", "configure-chest"));

    public static final StreamCodec<ByteBuf, ConfigureChestRequest> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ConfigureChestRequest::spread,
            ByteBufCodecs.BOOL, ConfigureChestRequest::sortOnClose,
            ConfigureChestRequest::new);

    

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
