package one.spectra.better_chests.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import one.spectra.better_chests.common.inventory.Inventory;
import one.spectra.better_chests.common.Configuration;
import one.spectra.better_chests.common.abstractions.ItemStack;
import one.spectra.better_chests.abstractions.SpectraItemStack;

public class SpectraInventory implements Inventory {
    private net.minecraft.world.Container _inventory;
    private int _skipSlots;
    private int _size;

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

    public void add(ItemStack stack) {
        addItem((net.minecraft.world.item.ItemStack) stack.getItemStack());
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

    private List<net.minecraft.world.item.ItemStack> addItem(net.minecraft.world.item.ItemStack[] itemStacks) {
        var stacksLeft = new ArrayList<net.minecraft.world.item.ItemStack>();
        for (var i = 0; i < itemStacks.length; i++) {
            var currentItemStack = itemStacks[i];
            var stackLeft = addItem(currentItemStack);
            if (stackLeft != null && stackLeft.getCount() != 0) {
                stacksLeft.add(stackLeft);
            }
        }
        return stacksLeft;
    }

    private net.minecraft.world.item.ItemStack addItem(net.minecraft.world.item.ItemStack itemStack) {
        var indexesOfNonFullStacks = getIndexesOfNonFullStacks(itemStack);
        var amountLeft = itemStack.getCount();
        var i = 0;

        while (amountLeft > 0 && i < indexesOfNonFullStacks.length) {
            var containerStack = _inventory.getItem(indexesOfNonFullStacks[i]);
            var ableToAdd = containerStack.getMaxStackSize() - containerStack.getCount();
            if (ableToAdd > amountLeft) {
                containerStack.grow(amountLeft);
                itemStack.shrink(amountLeft);
                amountLeft = 0;
            } else {
                containerStack.grow(ableToAdd);
                itemStack.shrink(ableToAdd);
                amountLeft -= ableToAdd;
            }
            i++;
        }

        if (amountLeft > 0) {
            var firstEmptyIndex = getFirstEmptyIndex();
            if (firstEmptyIndex >= 0) {
                _inventory.setItem(firstEmptyIndex, itemStack);
                amountLeft = 0;
            }
        }

        return itemStack.getCount() > 0 ? itemStack : null;
    }

    public Configuration getConfiguration() {
        var blockEntity = getBlockEntity();
        if (blockEntity != null) {
            var persistantData = blockEntity.getPersistentData();
            var spread = getBooleanSafe(persistantData, "better_chests:spread", true);
            var sortOnClose = getBooleanSafe(persistantData, "better_chests:sortOnClose", false);
            return new Configuration(spread, sortOnClose);
        }
        return new Configuration(false, false);
    }

    public void configure(Configuration configuration) {
        var blockEntity = getBlockEntity();
        if (blockEntity != null) {
            var persistentData = blockEntity.getPersistentData();
            persistentData.putBoolean("better_chests:spread", configuration.spread());
            persistentData.putBoolean("better_chests:sortOnClose", configuration.sortOnClose());
        }
    }

    private boolean getBooleanSafe(CompoundTag data, String key, boolean defaultValue) {
        return data.contains(key) ? data.getBoolean(key) : defaultValue;
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

    private int[] getIndexesOfNonFullStacks(net.minecraft.world.item.ItemStack stack) {
        var indexes = new ArrayList<Integer>();
        for (var i = _skipSlots; i < _size + _skipSlots; i++) {
            var itemStackFromInventory = _inventory.getItem(i);
            if (net.minecraft.world.item.ItemStack.isSameItemSameComponents(stack, itemStackFromInventory)
                    && itemStackFromInventory.getCount() < itemStackFromInventory.getMaxStackSize()) {
                indexes.add(i);
            }
        }
        return indexes.stream().mapToInt(i -> i).toArray();
    }

    private int getFirstEmptyIndex() {
        for (var i = _skipSlots; i < _size + _skipSlots; i++) {
            var itemStackFromInventory = _inventory.getItem(i);
            if (itemStackFromInventory.isEmpty())
                return i;
        }
        return -1;
    }

    @Override
    public List<ItemStack> add(List<ItemStack> stacks) {
        var stacksToAdd = stacks.stream().map(x -> x.getItemStack()).toList()
                .toArray(new net.minecraft.world.item.ItemStack[0]);
        var restStacks = addItem(stacksToAdd);
        return restStacks.stream().map(x -> new SpectraItemStack(x)).map(x -> (ItemStack) x).toList();
    }
}
