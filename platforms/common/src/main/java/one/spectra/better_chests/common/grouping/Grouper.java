package one.spectra.better_chests.common.grouping;

import java.util.List;
import one.spectra.better_chests.common.abstractions.ItemStack;
import java.util.stream.Collectors;

public class Grouper {
    public List<List<ItemStack>> group(GroupSettings setting, List<ItemStack> stacks) {
        return stacks.stream().collect(Collectors.groupingBy(x -> x.getGroupKey(setting))).entrySet().stream()
            .map(x -> x.getValue())
            .toList();
    }
}
