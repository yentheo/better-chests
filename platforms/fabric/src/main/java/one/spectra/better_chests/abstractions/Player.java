package one.spectra.better_chests.abstractions;

import one.spectra.better_chests.inventory.Inventory;

public interface Player {
    Inventory getOpenContainer();
    Inventory getInventory();
    <TMessage> void sendTo(TMessage message);
}
