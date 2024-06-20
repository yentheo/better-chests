package one.spectra.better_chests.common.inventory.fillers;

import java.util.List;

import com.google.inject.Inject;

import one.spectra.better_chests.common.abstractions.ItemStack;
import one.spectra.better_chests.common.inventory.Inventory;

public class DefaultFiller implements Filler {

    @Inject
    public DefaultFiller() {
    }

    @Override
    public boolean canFill(Inventory inventory, List<List<ItemStack>> groups) {
        var stacks = groups.stream().flatMap(List::stream).toList();
        return inventory.getSize() >= stacks.size();
    }

    @Override
    public void fill(Inventory inventory, List<List<ItemStack>> groups, boolean spread) {
        List<ItemStack> flatStacks = groups.stream().flatMap(List::stream).toList();

        for (var index = 0; index < flatStacks.size(); index++) {
            inventory.putInSlot(index, flatStacks.get(index));
        }
    }

}
