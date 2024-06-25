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
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import one.spectra.better_chests.abstractions.PlayerFactory;
import one.spectra.better_chests.common.Sorter;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BetterChestsMod.MODID)
public class BetterChestsMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "better_chests";
    public static final Logger LOGGER = LogUtils.getLogger();
	public static Injector INJECTOR;

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
        registrar.playToServer(SortRequest.TYPE, SortRequest.STREAM_CODEC, new IPayloadHandler<SortRequest>() {
            public void handle(SortRequest payload, net.neoforged.neoforge.network.handling.IPayloadContext context) {
                var playerFactory = INJECTOR.getInstance(PlayerFactory.class);
                var player = playerFactory.createPlayer(context.player());
                var sorter = INJECTOR.getInstance(Sorter.class);
                var inventoryToSort = payload.sortPlayerInventory() ? player.getInventory() : player.getOpenContainer();
                sorter.sort(inventoryToSort, true, true);
            };
        });
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
