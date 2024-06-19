package one.spectra.better_chests.inventory;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import one.spectra.better_chests.abstractions.ItemStack;

public class Spreader {
    
    public List<List<ItemStack>> spread(List<List<ItemStack>> groups, int lineLength) {
        return groups.stream().map(x -> spreadGroup(x, lineLength)).toList();
    }

    private List<ItemStack> spreadGroup(List<ItemStack> group, int lineLength) {
        var amountOfEmptySlots = lineLength - Math.floorMod(group.size(), lineLength);
        Optional<ItemStack> smallestStackWithMoreThanOne = group.stream()
                .filter(x -> x.getAmount() > 1)
                .sorted(Comparator.comparing(x -> x.getAmount(), Comparator.naturalOrder())).findFirst();

        while (amountOfEmptySlots > 0 && amountOfEmptySlots < lineLength && smallestStackWithMoreThanOne.isPresent()) {
            var newStack = smallestStackWithMoreThanOne.get().takeOne();
            group = Stream.concat(group.stream(), Stream.of(newStack))
                    .toList();
            amountOfEmptySlots = lineLength - Math.floorMod(group.size(), lineLength);
            smallestStackWithMoreThanOne = group.stream()
                    .filter(x -> x.getAmount() > 1)
                    .sorted(Comparator.comparing(x -> x.getAmount(), Comparator.naturalOrder())).findFirst();
        }

        return group.stream().sorted(Comparator.comparing(x -> x.getAmount(), Comparator.reverseOrder())).toList();
    }
}
