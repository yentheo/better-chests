package one.spectra.better_chests.communications.requests;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SortRequest(boolean sortPlayerInventory, boolean spread) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SortRequest> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath("better-chest", "sort"));

    public static final StreamCodec<ByteBuf, SortRequest> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, SortRequest::sortPlayerInventory,
        ByteBufCodecs.BOOL, SortRequest::spread,
            SortRequest::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
