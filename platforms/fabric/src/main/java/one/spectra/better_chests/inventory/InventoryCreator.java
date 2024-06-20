package one.spectra.better_chests.inventory;

import one.spectra.better_chests.common.inventory.Inventory;

public interface InventoryCreator {
    Inventory create(net.minecraft.inventory.Inventory inventory);
}
