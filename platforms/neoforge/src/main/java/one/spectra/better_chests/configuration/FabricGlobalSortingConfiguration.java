package one.spectra.better_chests.configuration;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

public class FabricGlobalSortingConfiguration implements ConfigData {
    @Tooltip
    public boolean spread = false;
    @Tooltip
    public boolean sortOnClose = false;
}
