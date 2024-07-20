package one.spectra.better_chests.abstractions;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import one.spectra.better_chests.common.abstractions.ItemStack;
import one.spectra.better_chests.common.grouping.GroupSettings;

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
        return this.itemStack.getItem().toString();
    }

    @Override
    public net.minecraft.world.item.ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public ItemStack takeOne() {
        return new SpectraItemStack(this.itemStack.split(1));
    }

    @Override
    public int getDurability() {
        return itemStack.getDamageValue();
    }

    private String getEnchantment() {
        var enchantment = "";
        if (!EnchantmentHelper.getEnchantmentsForCrafting(itemStack).isEmpty())
            enchantment = EnchantmentHelper.getEnchantmentsForCrafting(itemStack).entrySet().stream()
                    .map(x -> x.getKey().getRegisteredName()).sorted().collect(Collectors.joining(" - "));
        return enchantment;
    }

    private String getEnchantmentWithLevel() {
        var enchantment = "";
        if (!EnchantmentHelper.getEnchantmentsForCrafting(itemStack).isEmpty())
            enchantment = EnchantmentHelper.getEnchantmentsForCrafting(itemStack).keySet().stream()
                    .map(x -> x.getRegisteredName() + getLevel(x, itemStack)).sorted().collect(Collectors.joining(" - "));
        return enchantment;
    }

    private int getLevel(Holder<Enchantment> enchantment, net.minecraft.world.item.ItemStack itemStack) {
        return EnchantmentHelper.getEnchantmentsForCrafting(itemStack).getLevel(enchantment);
    }

    @Override
    public String getGroupKey(GroupSettings groupSettings) {
        return groupSettings.groupEnchantments() ? this.getMaterialKey() + ":" + getEnchantment() : this.getMaterialKey();
    }

    @Override
    public String getSortKey() {
        return this.getMaterialKey() + ":" + this.getEnchantmentWithLevel();
    }
}
