package one.spectra.better_chests.common.inventory;

import java.util.List;

import one.spectra.better_chests.common.abstractions.ItemStack;

public interface InMemoryInventory extends Inventory {
    void add(ItemStack stack);
    List<ItemStack> add(List<ItemStack> stacks);
}
