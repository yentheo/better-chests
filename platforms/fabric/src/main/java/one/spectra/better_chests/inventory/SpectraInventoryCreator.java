package one.spectra.better_chests.inventory;

import com.google.inject.Inject;

public class SpectraInventoryCreator implements InventoryCreator {

    private InventoryFactory _inventoryFactory;

    @Inject
    public SpectraInventoryCreator(InventoryFactory inventoryFactory) {
        _inventoryFactory = inventoryFactory;
    }

    public Inventory create(int size) {
        var memoryInventory = new net.minecraft.inventory.SimpleInventory(size);
        return this.create(memoryInventory);
    }

    public Inventory create(net.minecraft.inventory.Inventory container) {
        return _inventoryFactory.create(container);
    }

    public Inventory create(Inventory inventory) {
        var copied = create(inventory.getSize());
        copied.add(inventory.getItemStacks());
        return copied;
    }
}
