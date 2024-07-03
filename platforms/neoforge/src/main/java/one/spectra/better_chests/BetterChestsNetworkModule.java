package one.spectra.better_chests;

import com.google.inject.AbstractModule;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import one.spectra.better_chests.communications.MessageRegistrar;

public class BetterChestsNetworkModule extends AbstractModule {

    private PayloadRegistrar payloadRegistrar;

    public BetterChestsNetworkModule(PayloadRegistrar payloadRegistrar) {
        this.payloadRegistrar = payloadRegistrar;
    }

    @Override
    protected void configure() {
        bind(PayloadRegistrar.class).toInstance(payloadRegistrar);
        bind(MessageRegistrar.class).asEagerSingleton();
    }
}
