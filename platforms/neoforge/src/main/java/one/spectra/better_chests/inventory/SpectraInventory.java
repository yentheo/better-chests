package one.spectra.better_chests.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import one.spectra.better_chests.common.inventory.Inventory;
import one.spectra.better_chests.common.abstractions.ItemStack;
import one.spectra.better_chests.common.configuration.ContainerConfiguration;
import one.spectra.better_chests.common.configuration.GlobalConfiguration;
import one.spectra.better_chests.common.configuration.SortingConfiguration;
import one.spectra.better_chests.BetterChestsMod;
import one.spectra.better_chests.abstractions.SpectraItemStack;

public class SpectraInventory implements Inventory {
    protected net.minecraft.world.Container _inventory;
    protected int _skipSlots;
    protected int _size;

    @AssistedInject
    public SpectraInventory(@Assisted net.minecraft.world.entity.player.Inventory playerInventory) {
        this(playerInventory, 9, 27);
    }

    @AssistedInject
    public SpectraInventory(@Assisted net.minecraft.world.Container inventory) {
        this(inventory, 0, inventory.getContainerSize());
    }

    private SpectraInventory(net.minecraft.world.Container inventory, int skipSlots, int size) {
        _inventory = inventory;
        _skipSlots = skipSlots;
        _size = size;
    }

    public int getSize() {
        return _size;
    }

    public void putInSlot(int slot, ItemStack stack) {
        _inventory.setItem(slot + _skipSlots, (net.minecraft.world.item.ItemStack) stack.getItemStack());
    }

    public void clear() {
        for (var i = _skipSlots; i < _size + _skipSlots; i++) {
            _inventory.setItem(i, net.minecraft.world.item.ItemStack.EMPTY);
        }
    }

    public ArrayList<ItemStack> getItemStacks() {
        var stacks = new ArrayList<ItemStack>();
        for (int i = _skipSlots; i < _size + _skipSlots; i++) {
            var stack = _inventory.getItem(i);

            if (stack != null && stack.getItem() != net.minecraft.world.item.Items.AIR) {
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

    public ContainerConfiguration getConfiguration() {
        var blockEntity = getBlockEntity();
        if (blockEntity != null) {
            var persistantData = blockEntity.getPersistentData();
            var spread = getBooleanSafe(persistantData, "better_chests:spread");
            var sortOnClose = getBooleanSafe(persistantData, "better_chests:sortOnClose");
            var sortingConfiguration = new SortingConfiguration(spread, sortOnClose);
            return new ContainerConfiguration(sortingConfiguration);
        }
        return new ContainerConfiguration(new SortingConfiguration(Optional.empty(), Optional.empty()));
    }

    public void configure(ContainerConfiguration configuration) {
        var blockEntity = getBlockEntity();
        if (blockEntity != null) {
            var persistentData = blockEntity.getPersistentData();
            if (configuration.sorting().spread().isPresent())
                persistentData.putBoolean("better_chests:spread", configuration.sorting().spread().get());
            else
                persistentData.remove("better_chests:spread");
            if (configuration.sorting().sortOnClose().isPresent())
                persistentData.putBoolean("better_chests:sortOnClose", configuration.sorting().sortOnClose().get());
            else
                persistentData.remove("better_chests:sortOnClose");

        }
    }

    private Optional<Boolean> getBooleanSafe(CompoundTag data, String key) {
        return data.contains(key) ? Optional.of(data.getBoolean(key)) : Optional.empty();
    }

    private BlockEntity getBlockEntity() {
        if (_inventory instanceof BlockEntity) {
            return (BlockEntity) _inventory;
        } else if (_inventory instanceof CompoundContainer) {
            var compoundContainer = (CompoundContainer) _inventory;
            return getFirstContainer(compoundContainer);
        } else {
            return null;
        }
    }

    private ChestBlockEntity getFirstContainer(CompoundContainer container) {
        try {
            var allFields = CompoundContainer.class.getDeclaredFields();
            var firstChestBlock = Arrays.stream(allFields).filter(x -> x.getType() == Container.class).findFirst();
            if (firstChestBlock.isPresent()) {
                firstChestBlock.get().setAccessible(true);
                return (ChestBlockEntity) firstChestBlock.get().get(container);
            }
        } catch (SecurityException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
