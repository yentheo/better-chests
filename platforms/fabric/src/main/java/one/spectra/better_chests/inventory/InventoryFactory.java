package one.spectra.better_chests.inventory;


public interface InventoryFactory {
    Inventory create(net.minecraft.entity.player.PlayerInventory inventory);
    Inventory create(net.minecraft.inventory.Inventory container);
}
