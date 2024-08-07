package one.spectra.better_chests.communications.requests;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.PacketEncoder;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import one.spectra.better_chests.common.configuration.ContainerConfiguration;
import one.spectra.better_chests.common.configuration.SortingConfiguration;

public record ConfigureChestRequest(ContainerConfiguration containerConfiguration) implements CustomPayload {
    public static final CustomPayload.Id<ConfigureChestRequest> ID = new CustomPayload.Id<ConfigureChestRequest>(
            Identifier.of("better-chest", "configure-chest"));
    public static final PacketCodec<RegistryByteBuf, ConfigureChestRequest> CODEC = PacketCodec
            .of(ConfigureChestRequest::write, ConfigureChestRequest::new);

    public ConfigureChestRequest(RegistryByteBuf buf) {
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
        buf.writeOptional(containerConfiguration.sorting().spread(), optionalBooleanEncoder);
        buf.writeOptional(containerConfiguration.sorting().sortOnClose(), optionalBooleanEncoder);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
