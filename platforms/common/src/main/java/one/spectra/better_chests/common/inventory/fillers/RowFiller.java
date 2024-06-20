package one.spectra.better_chests.common.inventory.fillers;

import java.util.List;

import com.google.inject.Inject;

import one.spectra.better_chests.common.abstractions.ItemStack;
import one.spectra.better_chests.common.inventory.Inventory;
import one.spectra.better_chests.common.inventory.Spreader;

public class RowFiller implements Filler {

    private Spreader _spreader;

    @Inject
    public RowFiller(Spreader spreader) {
        _spreader = spreader;
    }

    @Override
    public boolean canFill(Inventory inventory, List<List<ItemStack>> groups) {
        double columnCountAsDouble = inventory.getColumns();
        var rowsPerItem = groups.stream().map(x -> Math.ceil(x.size() / columnCountAsDouble));
        var totalRowsNeeded = rowsPerItem.mapToInt(Double::intValue).sum();

        return totalRowsNeeded <= inventory.getRows();
    }

    @Override
    public void fill(Inventory inventory, List<List<ItemStack>> groups, boolean spread) {
        if (spread) {
            groups = _spreader.spread(groups, inventory.getColumns());
        }
        var rowIndex = 0;
        for (var groupIndex = 0; groupIndex < groups.size(); groupIndex++) {
            var stacksInGroup = groups.get(groupIndex);
            var columnIndex = 0;
            for (var stackIndex = 0; stackIndex < stacksInGroup.size(); stackIndex++) {
                if (columnIndex == inventory.getColumns()) {
                    columnIndex = 0;
                    rowIndex++;
                }
                inventory.putInSlot(columnIndex + rowIndex * inventory.getColumns(), stacksInGroup.get(stackIndex));
                columnIndex++;
            }
            rowIndex++;
        }
    }

}
