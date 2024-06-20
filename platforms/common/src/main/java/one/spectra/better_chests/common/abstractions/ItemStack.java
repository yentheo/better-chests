package one.spectra.better_chests.common.abstractions;

public interface ItemStack {
    int getAmount();
    String getMaterialKey();    
    Object getItemStack();
    ItemStack takeOne();
}
