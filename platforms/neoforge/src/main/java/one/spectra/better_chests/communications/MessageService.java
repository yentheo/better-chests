package one.spectra.better_chests.communications;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.inject.Inject;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;

public class MessageService {
    private MessageRegistrar messageRegistrar;

    @Inject
    public MessageService(MessageRegistrar messageRegistrar) {
        this.messageRegistrar = messageRegistrar;
    }

    public <TRequest extends CustomPacketPayload, TResponse extends CustomPacketPayload> Future<TResponse> requestFromServer(TRequest request, Class<TResponse> clazz) {
        PacketDistributor.sendToServer(request);
        var completableFuture = new CompletableFuture<TResponse>();

        Executors.newCachedThreadPool().submit(() -> {
            this.messageRegistrar.registerOnce(r -> {
                completableFuture.complete(r);
            }, clazz);
        });

        return completableFuture;
    }
}
