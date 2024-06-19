package one.spectra.better_chests.inventory.fillers;

import java.util.List;

import one.spectra.better_chests.abstractions.ItemStack;
import one.spectra.better_chests.inventory.Inventory;

public interface Filler {
    boolean canFill(Inventory inventory, List<List<ItemStack>> stacks);
    void fill(Inventory inventory, List<List<ItemStack>> stacks, boolean spread);
}
