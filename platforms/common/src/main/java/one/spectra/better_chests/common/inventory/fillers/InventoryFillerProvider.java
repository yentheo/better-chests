package one.spectra.better_chests.common.inventory.fillers;

import java.util.List;
import java.util.Optional;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import one.spectra.better_chests.common.abstractions.ItemStack;
import one.spectra.better_chests.common.inventory.Inventory;

public class InventoryFillerProvider {

    private Filler _defaultFiller;
    private List<Filler> _fillers;

    @Inject
    public InventoryFillerProvider(List<Filler> fillers, @Named("defaultFiller") Filler defaultFiller) {
        _fillers = fillers;
        _defaultFiller = defaultFiller;
    }

    public Optional<Filler> getInventoryFiller(Inventory inventory, List<List<ItemStack>> stacks) {
        var filtered = _fillers.stream().filter(x -> x.canFill(inventory, stacks));
        return filtered.findFirst();
    }

    public Filler getDefaultFiller() {
        return _defaultFiller;
    }
}
