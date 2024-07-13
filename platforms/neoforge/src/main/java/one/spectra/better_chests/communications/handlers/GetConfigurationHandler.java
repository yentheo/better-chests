package one.spectra.better_chests.communications.handlers;

import com.google.inject.Inject;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import one.spectra.better_chests.abstractions.PlayerFactory;
import one.spectra.better_chests.communications.requests.GetConfigurationRequest;
import one.spectra.better_chests.communications.responses.GetConfigurationResponse;

public class GetConfigurationHandler implements IPayloadHandler<GetConfigurationRequest> {

    private PlayerFactory playerFactory;

    @Inject
    public GetConfigurationHandler(PlayerFactory playerFactory) {
        this.playerFactory = playerFactory;
    }

    @Override
    public void handle(GetConfigurationRequest payload, IPayloadContext context) {
        var player = playerFactory.createPlayer(context.player());
        var openContainer = player.getOpenContainer();
        if (context.player() instanceof ServerPlayer) {
            var configuration = openContainer.getConfiguration();
            PacketDistributor.sendToPlayer((ServerPlayer)context.player(), new GetConfigurationResponse(configuration));
        }
    }

}
