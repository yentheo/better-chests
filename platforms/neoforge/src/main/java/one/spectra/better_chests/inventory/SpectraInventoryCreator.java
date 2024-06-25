package one.spectra.better_chests.inventory;

import com.google.inject.Inject;

import net.minecraft.world.Container;
import one.spectra.better_chests.common.inventory.Inventory;

public class SpectraInventoryCreator implements InventoryCreator, one.spectra.better_chests.common.inventory.InventoryCreator {

    private InventoryFactory _inventoryFactory;

    @Inject
    public SpectraInventoryCreator(InventoryFactory inventoryFactory) {
        _inventoryFactory = inventoryFactory;
    }

    public Inventory create(int size) {
        var memoryInventory = new net.minecraft.world.SimpleContainer(size);
        return this.create(memoryInventory);
    }

    public Inventory create(Container container) {
        return _inventoryFactory.create(container);
    }

    public Inventory create(Inventory inventory) {
        var copied = create(inventory.getSize());
        copied.add(inventory.getItemStacks());
        return copied;
    }
}
