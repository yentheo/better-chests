package one.spectra.better_chests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;

public class BetterChestsClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("better-chests");

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}