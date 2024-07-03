package one.spectra.better_chests.communications;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.function.Consumer;

import com.google.inject.Inject;
import com.google.inject.Injector;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import one.spectra.better_chests.BetterChestsMod;

public class MessageRegistrar {
    private PayloadRegistrar payloadRegistrar;
    private Injector injector;

    private Dictionary<Class<CustomPacketPayload>, Consumer<CustomPacketPayload>> _oneTimeConsumers;

    @Inject
    public MessageRegistrar(PayloadRegistrar payloadRegistrar, Injector injector) {
        this.payloadRegistrar = payloadRegistrar;
        this.injector = injector;
        _oneTimeConsumers = new Hashtable<Class<CustomPacketPayload>, Consumer<CustomPacketPayload>>();
    }

    public <T extends CustomPacketPayload, THandler extends IPayloadHandler<T>> MessageRegistrar registerPlayToServer(
            CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> reader,
            Class<THandler> handlerClass) {
        var handler = injector.getInstance(handlerClass);
        this.payloadRegistrar.playToServer(type, reader, handler);
        return this;
    }

    public <T extends CustomPacketPayload> MessageRegistrar registerResponseToClient(Class<T> clazz,
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> reader) {
        this.payloadRegistrar.playToClient(type, reader, new IPayloadHandler<T>() {

            @Override
            public void handle(T payload, IPayloadContext context) {
                BetterChestsMod.LOGGER.info(String.valueOf(_oneTimeConsumers.size()));
                BetterChestsMod.LOGGER.info(clazz.getName());
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
    public <TResponse extends CustomPacketPayload> void registerOnce(Consumer<TResponse> consumer,
            Class<TResponse> clazz) {
        _oneTimeConsumers.put((Class<CustomPacketPayload>) clazz, (Consumer<CustomPacketPayload>) consumer);
    }
}
