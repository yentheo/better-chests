package one.spectra.better_chests.common;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.inject.Inject;

import one.spectra.better_chests.common.abstractions.ItemStack;
import one.spectra.better_chests.common.inventory.InventoryCreator;
import one.spectra.better_chests.common.inventory.Inventory;
import one.spectra.better_chests.common.inventory.fillers.InventoryFillerProvider;

public class Sorter {

    private InventoryCreator _inventoryFactory;
    private InventoryFillerProvider _inventoryFillerProvider;

    @Inject
    public Sorter(InventoryCreator inventoryFactory, InventoryFillerProvider inventoryFillerProvider) {
        _inventoryFactory = inventoryFactory;
        _inventoryFillerProvider = inventoryFillerProvider;
    }

    public void sort(Inventory inventory, boolean defaultSpread, boolean defaultAlphabeticalSort) {
        var itemStacks = inventory.getItemStacks();

        var tempInventory = this._inventoryFactory.create(inventory.getSize());
        itemStacks.forEach(x -> tempInventory.add(x));
        inventory.clear();

        var mergedStacks = tempInventory.getItemStacks();

        Comparator<Entry<String, List<ItemStack>>> groupStackAmountComparator = Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder());
        Comparator<Entry<String, List<ItemStack>>> groupItemAmountComparator = Comparator.comparing(entry -> entry.getValue().stream().mapToInt(l -> l.getAmount()).sum(), Comparator.reverseOrder());
        Comparator<Entry<String, List<ItemStack>>> groupNameComparator = Comparator.comparing(entry -> entry.getKey());

        var sortAlphabetically = defaultAlphabeticalSort;

        var materialSorter = sortAlphabetically ? groupNameComparator : groupStackAmountComparator.thenComparing(groupItemAmountComparator).thenComparing(groupNameComparator);
        var groupedStacks = mergedStacks.stream().collect(Collectors.groupingBy(ItemStack::getMaterialKey)).entrySet().stream().sorted(materialSorter)
                .map(x -> x.getValue().stream().sorted(Comparator.comparing(stack -> stack.getAmount(), Comparator.reverseOrder())).toList().stream().toList()).toList();

        var filler = _inventoryFillerProvider.getInventoryFiller(inventory, groupedStacks);

        var spread = inventory.getConfiguration().spread();

        filler.fill(inventory, groupedStacks, spread);
    }
}
