package one.spectra.better_chests.inventory;

public interface InventoryCreator {
    Inventory create(int size);
    Inventory create(net.minecraft.inventory.Inventory inventory);
}
