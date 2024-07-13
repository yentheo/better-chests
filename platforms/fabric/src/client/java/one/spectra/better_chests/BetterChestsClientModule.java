package one.spectra.better_chests;

import com.google.inject.AbstractModule;

import one.spectra.better_chests.communications.ClientMessageRegistrar;
import one.spectra.better_chests.configuration.ConfigurationMapper;
import one.spectra.better_chests.screens.CurrentScreenHelper;

public class BetterChestsClientModule  extends AbstractModule {

    @Override
    protected void configure() {
        bind(ClientMessageRegistrar.class).asEagerSingleton();
        bind(CurrentScreenHelper.class).asEagerSingleton();
        bind(ConfigurationMapper.class).asEagerSingleton();
    }
}
