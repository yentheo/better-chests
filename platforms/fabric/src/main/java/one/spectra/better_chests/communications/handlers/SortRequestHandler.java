package one.spectra.better_chests.communications.handlers;

import com.google.inject.Inject;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPayloadHandler;
import one.spectra.better_chests.abstractions.PlayerFactory;
import one.spectra.better_chests.common.Sorter;
import one.spectra.better_chests.communications.requests.SortRequest;

public class SortRequestHandler implements PlayPayloadHandler<SortRequest> {

    private PlayerFactory playerFactory;
    private Sorter sorter;

    @Inject
    public SortRequestHandler(PlayerFactory playerFactory, Sorter sorter) {
        this.playerFactory = playerFactory;
        this.sorter = sorter;
    }

    @Override
    public void receive(SortRequest payload, Context context) {
        var player = playerFactory.createPlayer(context.player());
        var inventoryToSort = payload.sortPlayerInventory() ? player.getInventory() : player.getOpenContainer();
        sorter.sort(inventoryToSort, true, true);
    }

}
