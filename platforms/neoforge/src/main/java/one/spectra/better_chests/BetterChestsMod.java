package one.spectra.better_chests;

import org.slf4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import one.spectra.better_chests.communications.MessageRegistrar;
import one.spectra.better_chests.communications.handlers.ConfigureChestHandler;
import one.spectra.better_chests.communications.handlers.GetConfigurationHandler;
import one.spectra.better_chests.communications.handlers.SortRequestHandler;
import one.spectra.better_chests.communications.requests.ConfigureChestRequest;
import one.spectra.better_chests.communications.requests.GetConfigurationRequest;
import one.spectra.better_chests.communications.requests.SortRequest;
import one.spectra.better_chests.communications.responses.GetConfigurationResponse;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BetterChestsMod.MODID)
public class BetterChestsMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "better_chests";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static Injector INJECTOR;
    public static Injector NETWORK_INJECTOR;

    public BetterChestsMod(IEventBus modEventBus) {
        INJECTOR = Guice.createInjector(new BetterChestsModule());
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onRegisterNetworkPackets);

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Better Chests are enabled");
    }

    public void onRegisterNetworkPackets(RegisterPayloadHandlersEvent event) {
        LOGGER.info("Registering communication common");
        LOGGER.info("Registering communication");
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar("better-chests").versioned("1").optional();
        NETWORK_INJECTOR = INJECTOR.createChildInjector(new BetterChestsNetworkModule(registrar));
        var messageRegistrar = NETWORK_INJECTOR.getInstance(MessageRegistrar.class);

        messageRegistrar.registerPlayToServer(SortRequest.TYPE, SortRequest.STREAM_CODEC, SortRequestHandler.class);
        messageRegistrar.registerPlayToServer(ConfigureChestRequest.TYPE, ConfigureChestRequest.STREAM_CODEC, ConfigureChestHandler.class);

        messageRegistrar.registerPlayToServer(GetConfigurationRequest.TYPE, GetConfigurationRequest.STREAM_CODEC, GetConfigurationHandler.class);
        messageRegistrar.registerResponseToClient(GetConfigurationResponse.class, GetConfigurationResponse.TYPE, GetConfigurationResponse.STREAM_CODEC);
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            NeoForge.EVENT_BUS.register(ScreenEvents.class);
        }

        @SubscribeEvent
        public static void register(final RegisterPayloadHandlersEvent event) {
        }
    }
}
