package one.spectra.better_chests;

import one.spectra.better_chests.common.configuration.ContainerConfiguration;

public interface ConfigurationBlockEntity {
    void setConfiguration(ContainerConfiguration configuration);
    ContainerConfiguration getConfiguration();
}
