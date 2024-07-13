package one.spectra.better_chests.communications.handlers;

import com.google.inject.Inject;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPayloadHandler;
import one.spectra.better_chests.abstractions.PlayerFactory;
import one.spectra.better_chests.communications.requests.ConfigureChestRequest;

public class ConfigureChestHandler implements PlayPayloadHandler<ConfigureChestRequest> {

    private PlayerFactory playerFactory;

    @Inject
    public ConfigureChestHandler(PlayerFactory playerFactory) {
        this.playerFactory = playerFactory;
    }

    @Override
    public void receive(ConfigureChestRequest payload, Context context) {
        var player = playerFactory.createPlayer(context.player());
        var container = player.getOpenContainer();
        container.configure(payload.containerConfiguration());
    }
    
}
