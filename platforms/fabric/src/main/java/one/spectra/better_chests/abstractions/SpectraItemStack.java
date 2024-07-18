package one.spectra.better_chests.abstractions;

import net.minecraft.enchantment.EnchantmentHelper;
import one.spectra.better_chests.BetterChests;
import one.spectra.better_chests.common.abstractions.ItemStack;
import java.util.stream.Collectors;

public class SpectraItemStack implements ItemStack {

    private net.minecraft.item.ItemStack itemStack;

    public SpectraItemStack(net.minecraft.item.ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public int getAmount() {
        return this.itemStack.getCount();
    }

    @Override
    public String getMaterialKey() {
        var enchantment = "";
        if (!EnchantmentHelper.getEnchantments(itemStack).isEmpty())
            enchantment = EnchantmentHelper.getEnchantments(itemStack).getEnchantments().stream().map(x -> x.getKeyOrValue().toString()).sorted().collect(Collectors.joining(""));

        return this.itemStack.getItem().toString() + enchantment;
    }

    @Override
    public net.minecraft.item.ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public ItemStack takeOne() {
        this.itemStack.decrement(1);
        var newStack = new net.minecraft.item.ItemStack(this.itemStack.getItem(), 1);
        return new SpectraItemStack(newStack);
    }

    @Override
    public int getDurability() {
        return itemStack.getDamage();
    }
}
