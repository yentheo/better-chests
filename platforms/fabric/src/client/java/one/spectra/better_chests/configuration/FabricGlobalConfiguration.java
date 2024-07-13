package one.spectra.better_chests.configuration;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

@Config(name = "one.spectra.better_chests")
public class FabricGlobalConfiguration implements ConfigData {
    @CollapsibleObject
    public FabricGlobalSortingConfiguration sorting = new FabricGlobalSortingConfiguration();
    @Tooltip
    public boolean showSortButton = true;
    @Tooltip
    public boolean showConfigurationButton = true;
}
