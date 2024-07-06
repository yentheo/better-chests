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

import net.neoforged.neoforge.network.handling.IPayloadHandler;
import one.spectra.better_chests.abstractions.PlayerFactory;
import one.spectra.better_chests.abstractions.SpectraPlayer;
import one.spectra.better_chests.common.Sorter;
import one.spectra.better_chests.common.abstractions.Player;
import one.spectra.better_chests.common.inventory.InMemoryInventory;
import one.spectra.better_chests.common.inventory.Inventory;
import one.spectra.better_chests.common.inventory.Spreader;
import one.spectra.better_chests.common.inventory.fillers.ColumnFiller;
import one.spectra.better_chests.common.inventory.fillers.DefaultFiller;
import one.spectra.better_chests.common.inventory.fillers.Filler;
import one.spectra.better_chests.common.inventory.fillers.InventoryFillerProvider;
import one.spectra.better_chests.common.inventory.fillers.RowFiller;
import one.spectra.better_chests.communications.handlers.GetConfigurationHandler;
import one.spectra.better_chests.communications.handlers.SortRequestHandler;
import one.spectra.better_chests.communications.requests.GetConfigurationRequest;
import one.spectra.better_chests.communications.requests.SortRequest;
import one.spectra.better_chests.inventory.InventoryCreator;
import one.spectra.better_chests.inventory.InventoryFactory;
import one.spectra.better_chests.inventory.SpectraInMemoryInventory;
import one.spectra.better_chests.inventory.SpectraInventory;
import one.spectra.better_chests.inventory.SpectraInventoryCreator;

public class BetterChestsModule extends AbstractModule {

    public BetterChestsModule() {
    }

    @Override
    protected void configure() {
        bind(Logger.class).toInstance(LogUtils.getLogger());
        bind(InventoryCreator.class).to(SpectraInventoryCreator.class);
        bind(one.spectra.better_chests.common.inventory.InventoryCreator.class).to(SpectraInventoryCreator.class);

        bind(Sorter.class);
        bind(Spreader.class);
        bind(InventoryFillerProvider.class);
        bind(Filler.class).annotatedWith(Names.named("defaultFiller")).to(DefaultFiller.class);
        bind(RowFiller.class);
        bind(ColumnFiller.class);

        bind(new TypeLiteral<IPayloadHandler<SortRequest>>() {
        }).to(SortRequestHandler.class);
        bind(new TypeLiteral<IPayloadHandler<GetConfigurationRequest>>() {
        }).to(GetConfigurationHandler.class);

        install(new FactoryModuleBuilder()
                .implement(Player.class, SpectraPlayer.class)
                .build(PlayerFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Inventory.class, SpectraInventory.class)
                .implement(InMemoryInventory.class, SpectraInMemoryInventory.class)
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
