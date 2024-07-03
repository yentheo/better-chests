package one.spectra.better_chests.communications.handlers;

import com.google.inject.Inject;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import one.spectra.better_chests.abstractions.PlayerFactory;
import one.spectra.better_chests.common.Configuration;
import one.spectra.better_chests.communications.requests.ConfigureChestRequest;

public class ConfigureChestHandler implements IPayloadHandler<ConfigureChestRequest> {

    private PlayerFactory playerFactory;

    @Inject
    public ConfigureChestHandler(PlayerFactory playerFactory) {
        this.playerFactory = playerFactory;
    }

    @Override
    public void handle(ConfigureChestRequest payload, IPayloadContext context) {
        var player = playerFactory.createPlayer(context.player());
        var openContainer = player.getOpenContainer();
        openContainer.configure(new Configuration(payload.spread(), payload.sortOnClose()));
    }
    
}
