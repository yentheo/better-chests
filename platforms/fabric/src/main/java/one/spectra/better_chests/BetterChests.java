package one.spectra.better_chests;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import one.spectra.better_chests.abstractions.PlayerFactory;
import one.spectra.better_chests.common.Sorter;
import one.spectra.better_chests.communications.MessageRegistrar;
import one.spectra.better_chests.communications.handlers.ConfigureChestHandler;
import one.spectra.better_chests.communications.handlers.GetConfigurationHandler;
import one.spectra.better_chests.communications.handlers.SortRequestHandler;
import one.spectra.better_chests.communications.requests.ConfigureChestRequest;
import one.spectra.better_chests.communications.requests.GetConfigurationRequest;
import one.spectra.better_chests.communications.requests.SortRequest;
import one.spectra.better_chests.communications.responses.GetConfigurationResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class BetterChests implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("better-chests");
	public static Injector INJECTOR;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Initializing Better Chests!");		
        INJECTOR = Guice.createInjector(new BetterChestsModule(PayloadTypeRegistry.playC2S(), PayloadTypeRegistry.playS2C()));

		var messageRegistrar = INJECTOR.getInstance(MessageRegistrar.class);
		messageRegistrar
		.registerPlayToServer(SortRequest.ID, SortRequest.CODEC, SortRequestHandler.class)
		.registerPlayToServer(GetConfigurationRequest.ID, GetConfigurationRequest.CODEC, GetConfigurationHandler.class)
		.registerPlayToServer(ConfigureChestRequest.ID, ConfigureChestRequest.CODEC, ConfigureChestHandler.class)
		.registerPlayFromServer(GetConfigurationResponse.ID, GetConfigurationResponse.CODEC);

	}
}