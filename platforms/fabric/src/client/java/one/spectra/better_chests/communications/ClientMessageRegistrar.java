package one.spectra.better_chests.communications;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.CustomPayload.Id;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.function.Consumer;

public class ClientMessageRegistrar {

    private PayloadTypeRegistry<RegistryByteBuf> clientPayloadRegistrar;
    private Dictionary<Class<CustomPayload>, Consumer<CustomPayload>> _oneTimeConsumers;

    @Inject
    public ClientMessageRegistrar(@Named("client") PayloadTypeRegistry<RegistryByteBuf> clientPayloadRegistrar,
            Injector injector) {
        this.clientPayloadRegistrar = clientPayloadRegistrar;
        _oneTimeConsumers = new Hashtable<Class<CustomPayload>, Consumer<CustomPayload>>();
    }

    public <T extends CustomPayload> ClientMessageRegistrar registerResponseToClient(Class<T> clazz, Id<T> id,
            PacketCodec<RegistryByteBuf, T> codec) {
        ClientPlayNetworking.registerGlobalReceiver(id, new ClientPlayNetworking.PlayPayloadHandler<T>() {

            @Override
            public void receive(T payload, ClientPlayNetworking.Context context) {
                var oneTimeConsumer = _oneTimeConsumers.get(clazz);
                if (oneTimeConsumer != null) {
                    _oneTimeConsumers.remove(oneTimeConsumer);
                    oneTimeConsumer.accept(payload);
                }
            }

        });

        return this;
    }

    @SuppressWarnings("unchecked")
    public <TResponse extends CustomPayload> void registerOnce(Consumer<TResponse> consumer,
            Class<TResponse> clazz) {
        _oneTimeConsumers.put((Class<CustomPayload>) clazz, (Consumer<CustomPayload>) consumer);
    }

}
