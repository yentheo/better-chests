package one.spectra.better_chests.abstractions;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.registry.entry.RegistryEntry;
import one.spectra.better_chests.common.abstractions.ItemStack;
import one.spectra.better_chests.common.grouping.GroupSettings;

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
        return this.itemStack.getItem().toString();
    }

    private String getEnchantment() {
        return EnchantmentHelper.getEnchantments(itemStack).getEnchantments().stream().map(x -> x.getIdAsString())
                .sorted().collect(Collectors.joining(" - "));
    }

    private String getEnchantmentWithLevel() {
        return EnchantmentHelper.getEnchantments(itemStack).getEnchantments().stream()
                .map(x -> x.getIdAsString() + getLevel(x, itemStack))
                .sorted().collect(Collectors.joining(" - "));
    }

    private String getLevel(RegistryEntry<Enchantment> enchantment, net.minecraft.item.ItemStack stack) {
        var level = EnchantmentHelper.getEnchantments(itemStack).getLevel(enchantment);
        return String.valueOf(level);
    }

    @Override
    public net.minecraft.item.ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public ItemStack takeOne() {
        return new SpectraItemStack(this.itemStack.split(1));
    }

    @Override
    public int getDurability() {
        return itemStack.getDamage();
    }

    @Override
    public String getGroupKey(GroupSettings groupSettings) {
        return groupSettings.groupEnchantments() ? this.getMaterialKey() + ":" + getEnchantment()
                : this.getMaterialKey();
    }

    @Override
    public String getSortKey() {
        return this.getMaterialKey() + ":" + getEnchantmentWithLevel();
    }

    
}
