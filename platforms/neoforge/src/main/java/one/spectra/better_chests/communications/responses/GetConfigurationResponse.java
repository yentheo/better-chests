package one.spectra.better_chests.communications.responses;

import java.util.Optional;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import one.spectra.better_chests.common.configuration.ContainerConfiguration;
import one.spectra.better_chests.common.configuration.SortingConfiguration;

public record GetConfigurationResponse(ContainerConfiguration containerConfiguration) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GetConfigurationResponse> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath("better-chest", "get-configuration-response"));

    public static final StreamCodec<ByteBuf, GetConfigurationResponse> STREAM_CODEC = StreamCodec
            .ofMember(GetConfigurationResponse::encode, GetConfigurationResponse::new);

    private static final StreamCodec<ByteBuf, Optional<Boolean>> OPTIONAL_BOOLEAN_CODEC = ByteBufCodecs.BOOL.apply(ByteBufCodecs::optional);

    public GetConfigurationResponse(ByteBuf buf) {
        this(new ContainerConfiguration(new SortingConfiguration(OPTIONAL_BOOLEAN_CODEC.decode(buf), OPTIONAL_BOOLEAN_CODEC.decode(buf))));
    }

    public void encode(ByteBuf buf) {
        var optionalBooleanEncoder = ByteBufCodecs.BOOL.apply(ByteBufCodecs::optional);
        optionalBooleanEncoder.encode(buf, containerConfiguration.sorting().spread());
        optionalBooleanEncoder.encode(buf, containerConfiguration.sorting().sortOnClose());
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
