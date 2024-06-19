package one.spectra.better_chests.inventory.fillers;

import java.util.List;
import org.slf4j.Logger;

import com.google.inject.Inject;

import one.spectra.better_chests.abstractions.ItemStack;
import one.spectra.better_chests.inventory.Inventory;

public class DefaultFiller implements Filler {

    private Logger _logger;

    @Inject
    public DefaultFiller(Logger logger) {
        _logger = logger;
    }

    @Override
    public boolean canFill(Inventory inventory, List<List<ItemStack>> groups) {
        _logger.info("Checking if default filler can fill");
        var stacks = groups.stream().flatMap(List::stream).toList();
        return inventory.getSize() >= stacks.size();
    }

    @Override
    public void fill(Inventory inventory, List<List<ItemStack>> groups, boolean spread) {
        _logger.info("Filling with default filler");
        List<ItemStack> flatStacks = groups.stream().flatMap(List::stream).toList();

        for (var index = 0; index < flatStacks.size(); index++) {
            inventory.putInSlot(index, flatStacks.get(index));
        }
    }

}
