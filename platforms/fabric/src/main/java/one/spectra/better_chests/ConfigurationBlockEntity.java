package one.spectra.better_chests;

import one.spectra.better_chests.common.Configuration;

public interface ConfigurationBlockEntity {
    void setConfiguration(Configuration configuration);
    Configuration getConfiguration();
}
