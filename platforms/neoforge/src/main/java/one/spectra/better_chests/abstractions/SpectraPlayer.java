package one.spectra.better_chests.abstractions;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import one.spectra.better_chests.common.inventory.Inventory;
import one.spectra.better_chests.common.abstractions.Player;
import one.spectra.better_chests.inventory.InventoryCreator;
import one.spectra.better_chests.inventory.InventoryFactory;

public class SpectraPlayer implements Player {
    private net.minecraft.world.entity.player.Player _player;
    private InventoryCreator _inventoryCreator;
    private InventoryFactory _inventoryFactory;

    @Inject
    public SpectraPlayer(@Assisted net.minecraft.world.entity.player.Player player, InventoryCreator inventoryCreator, InventoryFactory inventoryFactory) {
        _player = player;
        _inventoryCreator = inventoryCreator;
        _inventoryFactory = inventoryFactory;
    }

    public Inventory getOpenContainer() {
        if (_player.hasContainerOpen()) {
            if (_player.containerMenu instanceof ChestMenu) {
                var chestMenu = (ChestMenu) _player.containerMenu;
                return _inventoryCreator.create(chestMenu.getContainer());
            } else if (_player.containerMenu instanceof ShulkerBoxMenu) {
                var shulkerBoxMenu = (ShulkerBoxMenu) _player.containerMenu;
                return _inventoryCreator.create(shulkerBoxMenu.getSlot(0).container);                
            }
        }
        return null;
    }

    public Inventory getInventory() {
        return _inventoryFactory.create(_player.getInventory());
    }

    @Override
    public <TMessage> void sendTo(TMessage message) {
        if (_player instanceof ServerPlayer) {
        }
    }
}
