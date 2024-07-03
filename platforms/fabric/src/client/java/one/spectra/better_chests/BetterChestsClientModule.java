package one.spectra.better_chests;

import com.google.inject.AbstractModule;

import one.spectra.better_chests.communications.ClientMessageRegistrar;

public class BetterChestsClientModule  extends AbstractModule {

    @Override
    protected void configure() {
        bind(ClientMessageRegistrar.class).asEagerSingleton();
    }
}
