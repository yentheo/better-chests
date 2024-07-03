package one.spectra.better_chests;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.mojang.logging.LogUtils;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPayloadHandler;
import net.minecraft.network.RegistryByteBuf;
import one.spectra.better_chests.common.Sorter;
import one.spectra.better_chests.common.abstractions.Player;
import one.spectra.better_chests.abstractions.PlayerFactory;
import one.spectra.better_chests.abstractions.SpectraPlayer;
import one.spectra.better_chests.common.inventory.Inventory;
import one.spectra.better_chests.inventory.InventoryCreator;
import one.spectra.better_chests.inventory.InventoryFactory;
import one.spectra.better_chests.inventory.SpectraInventory;
import one.spectra.better_chests.inventory.SpectraInventoryCreator;
import one.spectra.better_chests.common.inventory.Spreader;
import one.spectra.better_chests.common.inventory.fillers.ColumnFiller;
import one.spectra.better_chests.common.inventory.fillers.DefaultFiller;
import one.spectra.better_chests.common.inventory.fillers.Filler;
import one.spectra.better_chests.common.inventory.fillers.InventoryFillerProvider;
import one.spectra.better_chests.common.inventory.fillers.RowFiller;
import one.spectra.better_chests.communications.handlers.ConfigureChestHandler;
import one.spectra.better_chests.communications.handlers.SortRequestHandler;
import one.spectra.better_chests.communications.requests.ConfigureChestRequest;
import one.spectra.better_chests.communications.requests.SortRequest;

public class BetterChestsModule extends AbstractModule {

    private PayloadTypeRegistry<RegistryByteBuf> serverPayloadRegistrar;
    private PayloadTypeRegistry<RegistryByteBuf> clientPayloadRegistrar;

    public BetterChestsModule(PayloadTypeRegistry<RegistryByteBuf> serverPayloadRegistrar,
            PayloadTypeRegistry<RegistryByteBuf> clientPayloadRegistrar) {
        this.serverPayloadRegistrar = serverPayloadRegistrar;
        this.clientPayloadRegistrar = clientPayloadRegistrar;
    }

    @Override
    protected void configure() {
        bind(Logger.class).toInstance(LogUtils.getLogger());
        bind(InventoryCreator.class).to(SpectraInventoryCreator.class);
        bind(one.spectra.better_chests.common.inventory.InventoryCreator.class).to(SpectraInventoryCreator.class);

        bind(new TypeLiteral<PlayPayloadHandler<SortRequest>>() {
        }).to(SortRequestHandler.class);
        bind(new TypeLiteral<PlayPayloadHandler<ConfigureChestRequest>>() {
        }).to(ConfigureChestHandler.class);
        bind(new TypeLiteral<PayloadTypeRegistry<RegistryByteBuf>>() {
        }).annotatedWith(Names.named("server")).toInstance(serverPayloadRegistrar);
        bind(new TypeLiteral<PayloadTypeRegistry<RegistryByteBuf>>() {
        }).annotatedWith(Names.named("client")).toInstance(clientPayloadRegistrar);
        bind(Sorter.class);
        bind(Spreader.class);
        bind(InventoryFillerProvider.class);
        bind(Filler.class).annotatedWith(Names.named("defaultFiller")).to(DefaultFiller.class);
        bind(RowFiller.class);
        bind(ColumnFiller.class);

        install(new FactoryModuleBuilder()
                .implement(Player.class, SpectraPlayer.class)
                .build(PlayerFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Inventory.class, SpectraInventory.class)
                .build(InventoryFactory.class));

        bind(new TypeLiteral<List<Filler>>() {
        }).toProvider(new Provider<List<Filler>>() {

            @Inject
            RowFiller rowFiller;
            @Inject
            ColumnFiller columnFiller;

            @Override
            public List<Filler> get() {
                var fillers = new ArrayList<Filler>();
                fillers.add(rowFiller);
                fillers.add(columnFiller);
                return fillers;
            }
        });
    }
}
