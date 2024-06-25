package one.spectra.better_chests.inventory;

import net.minecraft.world.Container;
import one.spectra.better_chests.common.inventory.Inventory;

public interface InventoryFactory {
    Inventory create(net.minecraft.world.entity.player.Inventory inventory);
    Inventory create(Container container);
}
