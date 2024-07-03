package one.spectra.better_chests.communications;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.inject.Inject;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.packet.CustomPayload;

public class MessageService {
    private ClientMessageRegistrar messageRegistrar;

    @Inject
    public MessageService(ClientMessageRegistrar messageRegistrar) {
        this.messageRegistrar = messageRegistrar;
    }

    public <TRequest extends CustomPayload, TResponse extends CustomPayload> Future<TResponse> requestFromServer(TRequest request, Class<TResponse> clazz) {
        var completableFuture = new CompletableFuture<TResponse>();

        Executors.newCachedThreadPool().submit(() -> {
            this.messageRegistrar.registerOnce(r -> {
                completableFuture.complete(r);
            }, clazz);
        });
        ClientPlayNetworking.send(request);

        return completableFuture;
    }
}
