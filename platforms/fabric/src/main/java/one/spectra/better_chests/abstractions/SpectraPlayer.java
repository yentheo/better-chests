package one.spectra.better_chests.abstractions;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import one.spectra.better_chests.common.inventory.Inventory;
import one.spectra.better_chests.common.abstractions.Player;
import one.spectra.better_chests.inventory.InventoryCreator;
import one.spectra.better_chests.inventory.InventoryFactory;

public class SpectraPlayer implements Player {
    private PlayerEntity _player;
    private InventoryCreator _inventoryCreator;
    private InventoryFactory _inventoryFactory;

    @Inject
    public SpectraPlayer(@Assisted PlayerEntity player, InventoryCreator inventoryCreator, InventoryFactory inventoryFactory) {
        _player = player;
        _inventoryCreator = inventoryCreator;
        _inventoryFactory = inventoryFactory;
    }

    public Inventory getOpenContainer() {
        if (_player.currentScreenHandler instanceof GenericContainerScreenHandler) {
            var screenHandler = (GenericContainerScreenHandler)_player.currentScreenHandler;
            return _inventoryCreator.create(screenHandler.getInventory());
        }
        if (_player.currentScreenHandler instanceof ShulkerBoxScreenHandler) {
            var screenHandler = (ShulkerBoxScreenHandler)_player.currentScreenHandler;
            return _inventoryCreator.create(screenHandler);
        }
        return null;
    }

    public Inventory getInventory() {
        return _inventoryFactory.create(_player.getInventory());
    }
}
