package one.spectra.better_chests.common.inventory.fillers;

import java.util.List;

import com.google.inject.Inject;

import one.spectra.better_chests.common.abstractions.ItemStack;
import one.spectra.better_chests.common.inventory.Inventory;
import one.spectra.better_chests.common.inventory.Spreader;

public class ColumnFiller implements Filler {

    private Spreader _spreader;

    @Inject
    public ColumnFiller(Spreader spreader) {
        _spreader = spreader;
    }

    @Override
    public boolean canFill(Inventory inventory, List<List<ItemStack>> groups) {
        double rowCountAsDouble = inventory.getRows();
        var columnsPerGroup = groups.stream().map(x -> Math.ceil(x.size() / rowCountAsDouble));
        var totalColumsNeeded = columnsPerGroup.mapToInt(Double::intValue).sum();

        return totalColumsNeeded <= inventory.getColumns();
    }

    @Override
    public void fill(Inventory inventory, List<List<ItemStack>> groups, boolean spread) {
        if (spread) {
            groups = _spreader.spread(groups, inventory.getRows());
        }
        var columnIndex = 0;
        for (var groupIndex = 0; groupIndex < groups.size(); groupIndex++) {
            var stacksInGroup = groups.get(groupIndex);
            var rowIndex = 0;
            for (var stackIndex = 0; stackIndex < stacksInGroup.size(); stackIndex++) {
                if (rowIndex == inventory.getRows()) {
                    rowIndex = 0;
                    columnIndex++;
                }
                inventory.putInSlot(columnIndex + rowIndex * inventory.getColumns(), stacksInGroup.get(stackIndex));
                rowIndex++;
            }
            columnIndex++;
        }
    }

}
