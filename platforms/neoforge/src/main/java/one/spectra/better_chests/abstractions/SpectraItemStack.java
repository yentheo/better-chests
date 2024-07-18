package one.spectra.better_chests.abstractions;

import net.minecraft.world.item.enchantment.EnchantmentHelper;
import one.spectra.better_chests.common.abstractions.ItemStack;
import java.util.stream.Collectors;

public class SpectraItemStack implements ItemStack {

    private net.minecraft.world.item.ItemStack itemStack;

    public SpectraItemStack(net.minecraft.world.item.ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public int getAmount() {
        return this.itemStack.getCount();
    }

    @Override
    public String getMaterialKey() {
        var enchantment = "";
        if (!EnchantmentHelper.getEnchantmentsForCrafting(itemStack).isEmpty())
            enchantment = EnchantmentHelper.getEnchantmentsForCrafting(itemStack).entrySet().stream().map(x -> x.getKey().getRegisteredName()).sorted().collect(Collectors.joining(""));

        return this.itemStack.getItem().toString() + enchantment;
    }

    @Override
    public net.minecraft.world.item.ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public ItemStack takeOne() {
        this.itemStack.setCount(getAmount() - 1);
        var newStack = new net.minecraft.world.item.ItemStack(this.itemStack.getItem(), 1);
        return new SpectraItemStack(newStack);
    }

    @Override
    public int getDurability() {
        return itemStack.getDamageValue();
    }
}
