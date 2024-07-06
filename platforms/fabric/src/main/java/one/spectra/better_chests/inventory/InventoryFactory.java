package one.spectra.better_chests.inventory;

import net.minecraft.screen.ShulkerBoxScreenHandler;
import one.spectra.better_chests.common.inventory.InMemoryInventory;
import one.spectra.better_chests.common.inventory.Inventory;

public interface InventoryFactory {
    Inventory create(net.minecraft.entity.player.PlayerInventory inventory);
    InMemoryInventory create(net.minecraft.inventory.Inventory container);
    Inventory create(ShulkerBoxScreenHandler shulkerBoxScreenHandler);
}
