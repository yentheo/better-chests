package one.spectra.better_chests.communications.responses;

import java.util.Optional;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.PacketEncoder;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import one.spectra.better_chests.common.configuration.ContainerConfiguration;
import one.spectra.better_chests.common.configuration.SortingConfiguration;

public record GetContainerConfigurationResponse(ContainerConfiguration containerConfiguration)
        implements CustomPayload {
    public static final CustomPayload.Id<GetContainerConfigurationResponse> ID = new CustomPayload.Id<GetContainerConfigurationResponse>(
            Identifier.of("better-chest", "get-configuration-response"));
    public static final PacketCodec<RegistryByteBuf, GetContainerConfigurationResponse> CODEC = PacketCodec
            .of(GetContainerConfigurationResponse::write, GetContainerConfigurationResponse::new);

    public GetContainerConfigurationResponse(RegistryByteBuf buf) {
        this(readFromBuf(buf));
    }

    private static ContainerConfiguration readFromBuf(RegistryByteBuf buf) {
        var optionalBooleanEncoder = new PacketDecoder<PacketByteBuf, Boolean>() {
            @Override
            public Boolean decode(PacketByteBuf buf) {
                return buf.readBoolean();
            }
        };
        var sortingConfiguration = new SortingConfiguration(buf.readOptional(optionalBooleanEncoder),
                buf.readOptional(optionalBooleanEncoder));
        return new ContainerConfiguration(sortingConfiguration);
    }

    public void write(RegistryByteBuf buf) {
        var optionalBooleanEncoder = new PacketEncoder<PacketByteBuf, Boolean>() {
            @Override
            public void encode(PacketByteBuf buf, Boolean value) {
                buf.writeBoolean(value);
            }
        };
        var sorting = containerConfiguration.sorting();
        buf.writeOptional(sorting.spread() != null ? sorting.spread() : Optional.empty() , optionalBooleanEncoder);
        buf.writeOptional(sorting.sortOnClose() != null ? sorting.sortOnClose() : Optional.empty(), optionalBooleanEncoder);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}