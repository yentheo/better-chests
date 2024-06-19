package one.spectra.better_chests.inventory.fillers;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import one.spectra.better_chests.abstractions.ItemStack;
import one.spectra.better_chests.inventory.Inventory;

public class InventoryFillerProvider {

    private Filler _defaultFiller;
    private List<Filler> _fillers;

    @Inject
    public InventoryFillerProvider(List<Filler> fillers, @Named("defaultFiller") Filler defaultFiller) {
        _fillers = fillers;
        _defaultFiller = defaultFiller;
    }

    public Filler getInventoryFiller(Inventory inventory, List<List<ItemStack>> stacks) {
        var filtered = _fillers.stream().filter(x -> x.canFill(inventory, stacks));
        return filtered.findFirst().orElse(_defaultFiller);
    }
}
