package one.spectra.better_chests.inventory;

import one.spectra.better_chests.common.inventory.Inventory;

public interface InventoryFactory {
    Inventory create(net.minecraft.entity.player.PlayerInventory inventory);
    Inventory create(net.minecraft.inventory.Inventory container);
}
