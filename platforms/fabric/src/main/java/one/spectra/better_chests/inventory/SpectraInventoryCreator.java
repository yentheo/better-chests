package one.spectra.better_chests.inventory;

import com.google.inject.Inject;

import net.minecraft.screen.ShulkerBoxScreenHandler;
import one.spectra.better_chests.common.inventory.InMemoryInventory;
import one.spectra.better_chests.common.inventory.Inventory;

public class SpectraInventoryCreator implements InventoryCreator, one.spectra.better_chests.common.inventory.InventoryCreator {

    private InventoryFactory _inventoryFactory;

    @Inject
    public SpectraInventoryCreator(InventoryFactory inventoryFactory) {
        _inventoryFactory = inventoryFactory;
    }

    @Override
    public InMemoryInventory create(int size) {
        var inMemoryInventory = new net.minecraft.inventory.SimpleInventory(size);
        return _inventoryFactory.create(inMemoryInventory);
    }

    @Override
    public Inventory create(net.minecraft.inventory.Inventory container) {
        return _inventoryFactory.create(container);
    }

    @Override
    public Inventory create(ShulkerBoxScreenHandler shulkerBoxScreenHandler) {
        return _inventoryFactory.create(shulkerBoxScreenHandler);
    }
}
