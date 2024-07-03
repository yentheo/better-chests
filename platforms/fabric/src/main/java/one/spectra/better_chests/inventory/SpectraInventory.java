package one.spectra.better_chests.inventory;

import java.util.ArrayList;
import java.util.List;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.item.Items;
import one.spectra.better_chests.common.inventory.Inventory;
import one.spectra.better_chests.common.Configuration;
import one.spectra.better_chests.common.abstractions.ItemStack;
import one.spectra.better_chests.BetterChests;
import one.spectra.better_chests.ConfigurationBlockEntity;
import one.spectra.better_chests.abstractions.SpectraItemStack;
import java.util.Arrays;

public class SpectraInventory implements Inventory {
    private net.minecraft.inventory.Inventory _inventory;
    private int _skipSlots;
    private int _size;

    @AssistedInject
    public SpectraInventory(@Assisted net.minecraft.entity.player.PlayerInventory playerInventory) {
        this(playerInventory, 9, 27);
    }

    @AssistedInject
    public SpectraInventory(@Assisted net.minecraft.inventory.Inventory inventory) {
        this(inventory, 0, inventory.size());
    }

    private SpectraInventory(@Assisted net.minecraft.inventory.Inventory inventory, int skipSlots, int size) {
        _inventory = inventory;
        _skipSlots = skipSlots;
        _size = size;
    }

    private ConfigurationBlockEntity getBlockEntity() {
        if (_inventory instanceof PlayerInventory)
            return null;
        var blockEntity = _inventory instanceof DoubleInventory ? getFirstContainer((DoubleInventory) _inventory)
                : (ChestBlockEntity) _inventory;
        if (blockEntity instanceof ConfigurationBlockEntity) {
            return (ConfigurationBlockEntity) blockEntity;
        }
        return null;
    }

    private ChestBlockEntity getFirstContainer(DoubleInventory container) {
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

    public void add(ItemStack stack) {
        addItem((net.minecraft.item.ItemStack) stack.getItemStack());
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
        var blockEntity = getBlockEntity();
        if (blockEntity == null)
            return new Configuration(false, false);

        return blockEntity.getConfiguration();
    }

    public void configure(Configuration configuration) {
        var blockEntity = getBlockEntity();
        blockEntity.setConfiguration(configuration);
    }

    private List<net.minecraft.item.ItemStack> addItem(net.minecraft.item.ItemStack[] itemStacks) {
        var stacksLeft = new ArrayList<net.minecraft.item.ItemStack>();
        for (var i = 0; i < itemStacks.length; i++) {
            var currentItemStack = itemStacks[i];
            var stackLeft = addItem(currentItemStack);
            if (stackLeft != null && stackLeft.getCount() != 0) {
                stacksLeft.add(stackLeft);
            }
        }
        return stacksLeft;
    }

    private net.minecraft.item.ItemStack addItem(net.minecraft.item.ItemStack itemStack) {
        var indexesOfNonFullStacks = getIndexesOfNonFullStacks(itemStack);
        var amountLeft = itemStack.getCount();
        var i = 0;

        while (amountLeft > 0 && i < indexesOfNonFullStacks.length) {
            var containerStack = _inventory.getStack(indexesOfNonFullStacks[i]);
            var ableToAdd = containerStack.getMaxCount() - containerStack.getCount();
            if (ableToAdd > amountLeft) {
                containerStack.increment(amountLeft);
                itemStack.decrement(amountLeft);
                amountLeft = 0;
            } else {
                containerStack.increment(ableToAdd);
                itemStack.decrement(ableToAdd);
                amountLeft -= ableToAdd;
            }
            i++;
        }

        if (amountLeft > 0) {
            var firstEmptyIndex = getFirstEmptyIndex();
            if (firstEmptyIndex >= 0) {
                _inventory.setStack(firstEmptyIndex, itemStack);
                amountLeft = 0;
            }
        }

        return itemStack.getCount() > 0 ? itemStack : null;
    }

    private int[] getIndexesOfNonFullStacks(net.minecraft.item.ItemStack stack) {
        var indexes = new ArrayList<Integer>();
        for (var i = _skipSlots; i < _size + _skipSlots; i++) {
            var itemStackFromInventory = _inventory.getStack(i);
            if (net.minecraft.item.ItemStack.areItemsAndComponentsEqual(stack, itemStackFromInventory)
                    && itemStackFromInventory.getCount() < itemStackFromInventory.getMaxCount()) {
                indexes.add(i);
            }
        }
        return indexes.stream().mapToInt(i -> i).toArray();
    }

    private int getFirstEmptyIndex() {
        for (var i = _skipSlots; i < _size + _skipSlots; i++) {
            var itemStackFromInventory = _inventory.getStack(i);
            if (itemStackFromInventory.isEmpty())
                return i;
        }
        return -1;
    }

    @Override
    public List<ItemStack> add(List<ItemStack> stacks) {
        var stacksToAdd = stacks.stream().map(x -> x.getItemStack()).toList()
                .toArray(new net.minecraft.item.ItemStack[0]);
        var restStacks = addItem(stacksToAdd);
        return restStacks.stream().map(x -> new SpectraItemStack(x)).map(x -> (ItemStack) x).toList();
    }
}
