package one.spectra.better_chests;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.neoforged.neoforge.network.PacketDistributor;
import one.spectra.better_chests.common.configuration.GlobalConfiguration;
import one.spectra.better_chests.communications.MessageService;
import one.spectra.better_chests.communications.requests.GetConfigurationRequest;
import one.spectra.better_chests.communications.requests.SortRequest;
import one.spectra.better_chests.communications.responses.GetConfigurationResponse;
import one.spectra.better_chests.configuration.ConfigurationMapper;
import one.spectra.better_chests.configuration.FabricGlobalConfiguration;

public class BetterShulkerContainerScreen extends ShulkerBoxScreen {

    private int _rowCount = 0;
    private boolean sortOnClose;

    private GlobalConfiguration globalConfiguration;

    public BetterShulkerContainerScreen(ShulkerBoxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this._rowCount = 3;
        var configurationMapper = BetterChestsMod.NETWORK_INJECTOR.getInstance(ConfigurationMapper.class);

        var globalConfigurationHolder = AutoConfig.getConfigHolder(FabricGlobalConfiguration.class);

        globalConfiguration = configurationMapper.map(globalConfigurationHolder.get());

        var messageService = BetterChestsMod.NETWORK_INJECTOR.getInstance(MessageService.class);
        var futureResponse = messageService.requestFromServer(new GetConfigurationRequest(),
                GetConfigurationResponse.class);
        Executors.newCachedThreadPool().submit(() -> {
            try {
                var response = futureResponse.get();
                sortOnClose = globalConfiguration.sorting().sortOnClose().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void init() {
        super.init();
        if (globalConfiguration.showSortButton()) {
            var sortButtonFocusedImage = ResourceLocation.fromNamespaceAndPath("better_chests", "sort-button-focused");
            var sortButtonUnfocusedImage = ResourceLocation.fromNamespaceAndPath("better_chests",
                    "sort-button-unfocused");
            var sortSprite = new WidgetSprites(sortButtonFocusedImage, sortButtonUnfocusedImage);
            var sortContainerButton = new ImageButton(this.leftPos + this.imageWidth - 20, this.topPos + 5, 13, 9,
                    sortSprite,
                    e -> {
                        PacketDistributor
                                .sendToServer(new SortRequest(false, globalConfiguration.sorting().spread().get()));
                    });
            this.addRenderableWidget(sortContainerButton);

            var containerHeight = _rowCount * 18;

            var sortInventoryButton = new ImageButton(this.leftPos + this.imageWidth - 20,
                    this.topPos + 5 + containerHeight + 14, 13, 9, sortSprite, e -> {
                        PacketDistributor
                                .sendToServer(new SortRequest(true, globalConfiguration.sorting().spread().get()));
                    });
            this.addRenderableWidget(sortInventoryButton);
        }
    }

    @Override
    public void render(GuiGraphics p_282060_, int p_282533_, int p_281661_, float p_281873_) {
        super.render(p_282060_, p_282533_, p_281661_, p_281873_);
    }

    @Override
    public void onClose() {
        if (sortOnClose)
            PacketDistributor.sendToServer(new SortRequest(false, globalConfiguration.sorting().spread().get()));
        super.onClose();
    }
}
