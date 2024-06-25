package one.spectra.better_chests.abstractions;

import one.spectra.better_chests.common.abstractions.ItemStack;

public class SpectraItemStack implements ItemStack {

    private net.minecraft.world.item.ItemStack _itemStack;

    public SpectraItemStack(net.minecraft.world.item.ItemStack itemStack) {
        _itemStack = itemStack;
    }

    @Override
    public int getAmount() {
        return this._itemStack.getCount();
    }

    @Override
    public String getMaterialKey() {
        return this._itemStack.getItem().toString();
    }

    @Override
    public net.minecraft.world.item.ItemStack getItemStack() {
        return this._itemStack;
    }

    @Override
    public ItemStack takeOne() {
        this._itemStack.setCount(getAmount() - 1);
        var newStack = new net.minecraft.world.item.ItemStack(this._itemStack.getItem(), 1);
        return new SpectraItemStack(newStack);
    }
}
