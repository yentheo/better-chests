package one.spectra.better_chests.common.abstractions;

import one.spectra.better_chests.common.inventory.Inventory;

public interface Player {
    Inventory getOpenContainer();
    Inventory getInventory();
}
