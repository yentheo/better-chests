package one.spectra.better_chests.communications.handlers;

import com.google.inject.Inject;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import one.spectra.better_chests.abstractions.PlayerFactory;
import one.spectra.better_chests.common.Sorter;
import one.spectra.better_chests.communications.requests.SortRequest;

public class SortRequestHandler implements IPayloadHandler<SortRequest> {

    private PlayerFactory playerFactory;
    private Sorter sorter;

    @Inject
    public SortRequestHandler(PlayerFactory playerFactory, Sorter sorter) {
        this.sorter = sorter;
        this.playerFactory = playerFactory;
    }

    @Override
    public void handle(SortRequest payload, IPayloadContext context) {
        var player = playerFactory.createPlayer(context.player());
        var inventoryToSort = payload.sortPlayerInventory() ? player.getInventory() : player.getOpenContainer();
        sorter.sort(inventoryToSort, true, false);
    }
    
}
