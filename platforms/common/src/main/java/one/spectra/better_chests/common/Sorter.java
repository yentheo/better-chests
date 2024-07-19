package one.spectra.better_chests.common;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.inject.Inject;

import one.spectra.better_chests.common.abstractions.ItemStack;
import one.spectra.better_chests.common.grouping.GroupSettings;
import one.spectra.better_chests.common.grouping.Grouper;
import one.spectra.better_chests.common.inventory.InventoryCreator;
import one.spectra.better_chests.common.inventory.Inventory;
import one.spectra.better_chests.common.inventory.fillers.InventoryFillerProvider;

public class Sorter {

    private InventoryCreator inventoryFactory;
    private InventoryFillerProvider inventoryFillerProvider;
    private Grouper grouper;

    @Inject
    public Sorter(InventoryCreator inventoryFactory, InventoryFillerProvider inventoryFillerProvider,
            Grouper grouper) {
        this.inventoryFactory = inventoryFactory;
        this.inventoryFillerProvider = inventoryFillerProvider;
        this.grouper = grouper;
    }

    public void sort(Inventory inventory, boolean spread) {
        var itemStacks = inventory.getItemStacks();

        var tempInventory = this.inventoryFactory.create(inventory.getSize());
        itemStacks.forEach(x -> tempInventory.add(x));
        inventory.clear();

        var mergedStacks = tempInventory.getItemStacks();

        // inventory comparators
        Comparator<List<ItemStack>> groupStackAmountComparator = Comparator
                .comparing(entry -> entry.size(), Comparator.reverseOrder());
        Comparator<List<ItemStack>> groupItemAmountComparator = Comparator.comparing(
                entry -> entry.stream().mapToInt(l -> l.getAmount()).sum(), Comparator.reverseOrder());
        Comparator<List<ItemStack>> groupNameComparator = Comparator
                .comparing(entry -> entry.getFirst().getMaterialKey());

        // group comparators
        Comparator<ItemStack> amountComparator = Comparator.comparing(stack -> stack.getAmount(),
                Comparator.reverseOrder());
        Comparator<ItemStack> durabilityComparator = Comparator.comparing(entry -> entry.getDurability());
        Comparator<ItemStack> nameComparator = Comparator.comparing(entry -> entry.getSortKey());

        var groupSorter = amountComparator.thenComparing(durabilityComparator).thenComparing(nameComparator);

        var inventorySorter = groupStackAmountComparator.thenComparing(groupItemAmountComparator)
                .thenComparing(groupNameComparator);

        var specificGroups = grouper.group(new GroupSettings(true), mergedStacks);
        var specificGroupFiller = inventoryFillerProvider.getInventoryFiller(inventory, specificGroups);

        if (specificGroupFiller.isPresent()) {
            var sortedGroups = specificGroups.stream().sorted(inventorySorter)
                    .map(x -> x.stream().sorted(groupSorter).toList()).toList();
            specificGroupFiller.get().fill(inventory, sortedGroups, spread);
        } else {
            var groups = grouper.group(new GroupSettings(false), mergedStacks);
            var sortedGropus = groups
                    .stream().sorted(inventorySorter)
                    .map(x -> x.stream()
                            .sorted(groupSorter).toList())
                    .toList();

            var filler = inventoryFillerProvider.getInventoryFiller(inventory, sortedGropus)
                    .orElse(inventoryFillerProvider.getDefaultFiller());

            filler.fill(inventory, sortedGropus, spread);
        }
    }
}
