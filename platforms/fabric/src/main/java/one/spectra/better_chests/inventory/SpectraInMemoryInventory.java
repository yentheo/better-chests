package one.spectra.better_chests.inventory;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import one.spectra.better_chests.abstractions.SpectraItemStack;
import one.spectra.better_chests.common.abstractions.ItemStack;
import one.spectra.better_chests.common.inventory.InMemoryInventory;

public class SpectraInMemoryInventory extends SpectraInventory implements InMemoryInventory {

    @AssistedInject
    public SpectraInMemoryInventory(@Assisted net.minecraft.inventory.Inventory inventory) {
        super(inventory);
    }

    @Override
    public List<ItemStack> add(List<ItemStack> stacks) {
        var stacksToAdd = stacks.stream().map(x -> x.getItemStack()).toList()
                .toArray(new net.minecraft.item.ItemStack[0]);
        var restStacks = addItem(stacksToAdd);
        return restStacks.stream().map(x -> new SpectraItemStack(x)).map(x -> (ItemStack) x).toList();
    }
    
    public void add(ItemStack stack) {
        addItem((net.minecraft.item.ItemStack) stack.getItemStack());
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

}
