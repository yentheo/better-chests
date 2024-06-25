package one.spectra.better_chests.inventory;

import one.spectra.better_chests.common.inventory.Inventory;
import net.minecraft.world.Container;

public interface InventoryCreator {
    Inventory create(Container inventory);
}
