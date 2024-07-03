package one.spectra.better_chests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

import net.fabricmc.api.ClientModInitializer;
import one.spectra.better_chests.communications.ClientMessageRegistrar;
import one.spectra.better_chests.communications.responses.GetConfigurationResponse;

public class BetterChestsClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("better-chests");

	public static Injector INJECTOR;

	@Override
	public void onInitializeClient() {
		INJECTOR = BetterChests.INJECTOR.createChildInjector(new BetterChestsClientModule());
		var messageRegistrar = INJECTOR.getInstance(ClientMessageRegistrar.class);
		messageRegistrar.registerResponseToClient(GetConfigurationResponse.class, GetConfigurationResponse.ID, GetConfigurationResponse.CODEC);
	}
}