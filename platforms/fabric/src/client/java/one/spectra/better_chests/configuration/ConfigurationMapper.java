package one.spectra.better_chests.configuration;

import java.util.Optional;

import one.spectra.better_chests.common.configuration.ContainerConfiguration;
import one.spectra.better_chests.common.configuration.GlobalConfiguration;
import one.spectra.better_chests.common.configuration.SortingConfiguration;

public class ConfigurationMapper {
    public FabricConfiguration map(FabricGlobalConfiguration fabricGlobalConfiguration,
            ContainerConfiguration containerConfiguration) {
        var configuration = new FabricConfiguration();
        var containerSorting = new FabricSortingConfiguration();
        var containerSortOnClose = containerConfiguration.sorting().sortOnClose();
        var containerSpread = containerConfiguration.sorting().spread();
        containerSorting.sortOnClose = containerSortOnClose.isPresent()
                ? containerSortOnClose.get() ? OptionalBoolean.Yes : OptionalBoolean.No
                : OptionalBoolean.Global;
        containerSorting.spread = containerSpread.isPresent()
                ? containerSpread.get() ? OptionalBoolean.Yes : OptionalBoolean.No
                : OptionalBoolean.Global;
        configuration.containerSorting = containerSorting;
        configuration.globalConfiguration = fabricGlobalConfiguration;
        return configuration;
    }

    public GlobalConfiguration map(FabricGlobalConfiguration fabricGlobalConfiguration) {
        var sortingConfiguration = new SortingConfiguration(Optional.of(fabricGlobalConfiguration.sorting.spread),
                Optional.of(fabricGlobalConfiguration.sorting.sortOnClose));
        return new GlobalConfiguration(sortingConfiguration, fabricGlobalConfiguration.showConfigurationButton,
                fabricGlobalConfiguration.showSortButton);
    }

    public Optional<Boolean> map(OptionalBoolean value) {
        switch (value) {
            case OptionalBoolean.Global:
                return Optional.empty();
            case OptionalBoolean.Yes:
                return Optional.of(true);
            case OptionalBoolean.No:
                return Optional.of(false);
            default:
                return Optional.empty();
        }
    }
}
