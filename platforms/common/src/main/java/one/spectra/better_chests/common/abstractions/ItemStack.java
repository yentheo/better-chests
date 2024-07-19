package one.spectra.better_chests.common.abstractions;

import one.spectra.better_chests.common.grouping.GroupSettings;

public interface ItemStack {
    int getAmount();
    String getMaterialKey();
    String getGroupKey(GroupSettings groupSettings);
    String getSortKey();
    Object getItemStack();
    ItemStack takeOne();
    int getDurability();
}
