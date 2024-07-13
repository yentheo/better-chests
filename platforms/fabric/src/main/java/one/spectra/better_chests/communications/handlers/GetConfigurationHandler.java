package one.spectra.better_chests.communications.handlers;

import com.google.inject.Inject;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPayloadHandler;
import one.spectra.better_chests.abstractions.PlayerFactory;
import one.spectra.better_chests.communications.requests.GetConfigurationRequest;
import one.spectra.better_chests.communications.responses.GetContainerConfigurationResponse;

public class GetConfigurationHandler implements PlayPayloadHandler<GetConfigurationRequest> {

    private PlayerFactory playerFactory;

    @Inject
    public GetConfigurationHandler(PlayerFactory playerFactory) {
        this.playerFactory = playerFactory;
    }

    @Override
    public void receive(GetConfigurationRequest payload, Context context) {
        var player = playerFactory.createPlayer(context.player());
        var container = player.getOpenContainer();
        var configuration = container.getConfiguration();
        ServerPlayNetworking.send(context.player(), new GetContainerConfigurationResponse(configuration));
    }
    
}
