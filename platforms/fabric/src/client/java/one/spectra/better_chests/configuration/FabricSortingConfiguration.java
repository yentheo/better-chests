package one.spectra.better_chests.configuration;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;
public class FabricSortingConfiguration implements ConfigData {
    @Tooltip
    @ConfigEntry.Gui.PrefixText
    public OptionalBoolean spread = OptionalBoolean.Global;
    @Tooltip
    public OptionalBoolean sortOnClose = OptionalBoolean.Global;
}
