package one.spectra.better_chests.common.inventory;

import java.util.List;

import one.spectra.better_chests.common.abstractions.ItemStack;
import one.spectra.better_chests.common.configuration.ContainerConfiguration;

public interface Inventory {
    void clear();
    List<ItemStack> getItemStacks();
    int getSize();
    int getRows();
    int getColumns();
    void putInSlot(int slot, ItemStack stack);
    ContainerConfiguration getConfiguration();
    void configure(ContainerConfiguration configuration);
}
