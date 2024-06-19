package one.spectra.better_chests.inventory.fillers;

import java.util.List;
import org.slf4j.Logger;

import com.google.inject.Inject;

import one.spectra.better_chests.abstractions.ItemStack;
import one.spectra.better_chests.inventory.Inventory;
import one.spectra.better_chests.inventory.Spreader;

public class ColumnFiller implements Filler {

    private Spreader _spreader;
    private Logger _logger;

    @Inject
    public ColumnFiller(Spreader spreader, Logger logger) {
        _spreader = spreader;
        _logger = logger;
    }

    @Override
    public boolean canFill(Inventory inventory, List<List<ItemStack>> groups) {
        double rowCountAsDouble = inventory.getRows();
        var columnsPerGroup = groups.stream().map(x -> Math.ceil(x.size() / rowCountAsDouble));
        var totalColumsNeeded = columnsPerGroup.mapToInt(Double::intValue).sum();
        _logger.info("Total columns needed: " + totalColumsNeeded);

        return totalColumsNeeded <= inventory.getColumns();
    }

    @Override
    public void fill(Inventory inventory, List<List<ItemStack>> groups, boolean spread) {
        if (spread) {
            groups = _spreader.spread(groups, inventory.getRows());
        }
        _logger.info("Filling with column filler");
        var columnIndex = 0;
        for (var groupIndex = 0; groupIndex < groups.size(); groupIndex++) {
            _logger.info("Filling group with index " + groupIndex);
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
