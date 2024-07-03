package one.spectra.better_chests.communications;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPayloadHandler;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.CustomPayload.Id;

public class MessageRegistrar {

    private PayloadTypeRegistry<RegistryByteBuf> serverPayloadRegistrar;
    private Injector injector;

    @Inject
    public MessageRegistrar(@Named("server") PayloadTypeRegistry<RegistryByteBuf> serverPayloadRegistrar, Injector injector) {
        this.serverPayloadRegistrar = serverPayloadRegistrar;
        this.injector = injector;
    }

    public <T extends CustomPayload, THandler extends PlayPayloadHandler<T>> MessageRegistrar registerPlayToServer(
            Id<T> id, PacketCodec<RegistryByteBuf, T> codec, Class<THandler> handlerClass) {
        this.serverPayloadRegistrar.register(id, codec);
        var handler = injector.getInstance(handlerClass);
        ServerPlayNetworking.registerGlobalReceiver(id, handler);
        return this;
    }
}
