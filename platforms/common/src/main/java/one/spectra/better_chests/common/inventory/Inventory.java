package one.spectra.better_chests.common.inventory;

import java.util.List;

import one.spectra.better_chests.common.Configuration;
import one.spectra.better_chests.common.abstractions.ItemStack;

public interface Inventory {
    void clear();
    List<ItemStack> getItemStacks();
    int getSize();
    int getRows();
    int getColumns();
    void putInSlot(int slot, ItemStack stack);
    Configuration getConfiguration();
    void configure(Configuration configuration);
}
