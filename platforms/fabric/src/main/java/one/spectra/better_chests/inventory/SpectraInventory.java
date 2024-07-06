package one.spectra.better_chests.inventory;

import java.util.ArrayList;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import one.spectra.better_chests.common.inventory.Inventory;
import one.spectra.better_chests.common.Configuration;
import one.spectra.better_chests.common.abstractions.ItemStack;
import one.spectra.better_chests.ConfigurationBlockEntity;
import one.spectra.better_chests.abstractions.SpectraItemStack;
import java.util.Arrays;

public class SpectraInventory implements Inventory {
    protected net.minecraft.inventory.Inventory _inventory;
    protected int _skipSlots;
    protected int _size;

    @AssistedInject
    public SpectraInventory(@Assisted net.minecraft.entity.player.PlayerInventory playerInventory) {
        this(playerInventory, 9, 27);
    }

    @AssistedInject
    public SpectraInventory(@Assisted net.minecraft.inventory.Inventory inventory) {
        this(inventory, 0, inventory.size());
    }

    @AssistedInject
    public SpectraInventory(@Assisted ShulkerBoxScreenHandler shulkerBoxScreenHandler) {
        this(null, 0, 27);
        try {
            var allFields = ShulkerBoxScreenHandler.class.getDeclaredFields();
            var inventory = Arrays.stream(allFields)
                    .filter(x -> x.getType() == net.minecraft.inventory.Inventory.class).findFirst();
            if (inventory.isPresent()) {
                inventory.get().setAccessible(true);
                _inventory = (net.minecraft.inventory.Inventory) inventory.get().get(shulkerBoxScreenHandler);
            }
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
    }

    private SpectraInventory(@Assisted net.minecraft.inventory.Inventory inventory, int skipSlots, int size) {
        _inventory = inventory;
        _skipSlots = skipSlots;
        _size = size;
    }

    private ConfigurationBlockEntity getConfigurationBlockEntity() {
        // no block entity for player inventory
        if (_inventory instanceof PlayerInventory)
            return null;

        var blockEntity = getBlockEntity();
        if (blockEntity != null && blockEntity instanceof ConfigurationBlockEntity) {

            return (ConfigurationBlockEntity) blockEntity;
        }
        return null;
    }

    private BlockEntity getBlockEntity() {
        // if it's a double inventory, we look for the block entity of the first chest
        // to save config to that entity
        if (_inventory instanceof DoubleInventory)
            return getFirstBlockEntity((DoubleInventory) _inventory);

        if (_inventory instanceof ChestBlockEntity)
            return (ChestBlockEntity) _inventory;

        if (_inventory instanceof BarrelBlockEntity)
            return (BarrelBlockEntity) _inventory;

        if (_inventory instanceof ShulkerBoxBlockEntity) {
            return (ShulkerBoxBlockEntity) _inventory;
        }

        return null;
    }

    /**
     * Retrieves the first block entity of a double chest.
     * 
     * @param container a double inventory
     * @return The block entity of the first chest
     */
    private ChestBlockEntity getFirstBlockEntity(DoubleInventory container) {
        try {
            var allFields = DoubleInventory.class.getDeclaredFields();
            var firstChestBlock = Arrays.stream(allFields)
                    .filter(x -> x.getType() == net.minecraft.inventory.Inventory.class).findFirst();
            if (firstChestBlock.isPresent()) {
                firstChestBlock.get().setAccessible(true);
                return (ChestBlockEntity) firstChestBlock.get().get(container);
            }
        } catch (SecurityException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getSize() {
        return _size;
    }

    public void putInSlot(int slot, ItemStack stack) {
        _inventory.setStack(slot + _skipSlots, (net.minecraft.item.ItemStack) stack.getItemStack());
    }

    public void clear() {
        for (var i = _skipSlots; i < _size + _skipSlots; i++) {
            _inventory.setStack(i, net.minecraft.item.ItemStack.EMPTY);
        }
    }

    public ArrayList<ItemStack> getItemStacks() {
        var stacks = new ArrayList<ItemStack>();
        for (int i = _skipSlots; i < _size + _skipSlots; i++) {
            var stack = _inventory.getStack(i);

            if (stack != null && stack.getItem() != Items.AIR) {
                stacks.add(new SpectraItemStack(stack));
            }
        }
        return stacks;
    }

    @Override
    public int getRows() {
        return _size / getColumns();
    }

    @Override
    public int getColumns() {
        return 9;
    }

    public Configuration getConfiguration() {
        var blockEntity = getConfigurationBlockEntity();
        if (blockEntity == null)
            return new Configuration(false, false);

        return blockEntity.getConfiguration();
    }

    public void configure(Configuration configuration) {
        var blockEntity = getConfigurationBlockEntity();
        blockEntity.setConfiguration(configuration);
    }
}
