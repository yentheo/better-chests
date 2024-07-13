package one.spectra.better_chests.configuration;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject;

@Config(name = "one.spectra.better_chests")
public class FabricConfiguration implements ConfigData {
    @Category("container")
    @TransitiveObject
    public FabricSortingConfiguration containerSorting;
    @Category("global")
    @TransitiveObject
    public FabricGlobalConfiguration globalConfiguration;
    
}
