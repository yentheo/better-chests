package one.spectra.better_chests.inventory;

import net.minecraft.world.Container;
import one.spectra.better_chests.common.inventory.InMemoryInventory;
import one.spectra.better_chests.common.inventory.Inventory;

public interface InventoryFactory {
    Inventory create(net.minecraft.world.entity.player.Inventory inventory);
    InMemoryInventory create(Container container);
}
