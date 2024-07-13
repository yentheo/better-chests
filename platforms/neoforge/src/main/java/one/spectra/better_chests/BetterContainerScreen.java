package one.spectra.better_chests;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.neoforged.neoforge.network.PacketDistributor;
import one.spectra.better_chests.common.configuration.ContainerConfiguration;
import one.spectra.better_chests.common.configuration.GlobalConfiguration;
import one.spectra.better_chests.communications.MessageService;
import one.spectra.better_chests.communications.requests.GetConfigurationRequest;
import one.spectra.better_chests.communications.requests.SortRequest;
import one.spectra.better_chests.communications.responses.GetConfigurationResponse;
import one.spectra.better_chests.configuration.ConfigurationMapper;
import one.spectra.better_chests.configuration.FabricConfiguration;
import one.spectra.better_chests.configuration.FabricGlobalConfiguration;

public class BetterContainerScreen extends ContainerScreen {

    private int _rowCount = 0;

    private GlobalConfiguration globalConfiguration;
    private ContainerConfiguration containerConfiguration;

    public BetterContainerScreen(ChestMenu chestMenu, Inventory playerInventory, Component chestTitle) {
        super(chestMenu, playerInventory, chestTitle);
        this._rowCount = chestMenu.getRowCount();
        var messageService = BetterChestsMod.NETWORK_INJECTOR.getInstance(MessageService.class);
        var configurationMapper = BetterChestsMod.NETWORK_INJECTOR.getInstance(ConfigurationMapper.class);
        var futureResponse = messageService.requestFromServer(new GetConfigurationRequest(),
                GetConfigurationResponse.class);

        var containerConfigurationHolder = AutoConfig.getConfigHolder(FabricConfiguration.class);
        var globalConfigurationHolder = AutoConfig.getConfigHolder(FabricGlobalConfiguration.class);
        globalConfiguration = configurationMapper.map(globalConfigurationHolder.get());
        Executors.newCachedThreadPool().submit(() -> {
            try {
                var response = futureResponse.get();
                containerConfiguration = response.containerConfiguration();
                var fabricConfiguration = configurationMapper.map(globalConfigurationHolder.get(),
                        containerConfiguration);
                containerConfigurationHolder.setConfig(fabricConfiguration);

                Minecraft.getInstance().submit(() -> {
                    if (globalConfiguration.showConfigurationButton()) {
                        addConfigurationButton();
                    }
                    if (globalConfiguration.showSortButton()) {
                        addSortButtons();
                    }
                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

	private boolean shouldSortOnClose() {
		if (containerConfiguration != null && containerConfiguration.sorting().sortOnClose().isPresent()) {
			return containerConfiguration.sorting().sortOnClose().get();
		} else {
			return globalConfiguration.sorting().sortOnClose().get();
		}
	}

    private boolean shouldSpread() {
        if (containerConfiguration != null && containerConfiguration.sorting().spread().isPresent()) {
            return containerConfiguration.sorting().spread().get();
        } else {
            return globalConfiguration.sorting().spread().get();
        }
    }

    @Override
    public void init() {
        super.init();

    }

    private void addSortButtons() {
        var sortButtonFocusedImage = ResourceLocation.fromNamespaceAndPath("better_chests", "sort-button-focused");
        var sortButtonUnfocusedImage = ResourceLocation.fromNamespaceAndPath("better_chests", "sort-button-unfocused");
        var sortSprite = new WidgetSprites(sortButtonFocusedImage, sortButtonUnfocusedImage);
        var sortContainerButton = new ImageButton(this.leftPos + this.imageWidth - 20, this.topPos + 5, 13, 9,
                sortSprite,
                e -> {
                    PacketDistributor.sendToServer(new SortRequest(false, shouldSpread()));
                });
        this.addRenderableWidget(sortContainerButton);

        var containerHeight = _rowCount * 18;

        var sortInventoryButton = new ImageButton(this.leftPos + this.imageWidth - 20,
                this.topPos + 5 + containerHeight + 14, 13, 9, sortSprite, e -> {
                    PacketDistributor.sendToServer(new SortRequest(true, shouldSpread()));
                });
        this.addRenderableWidget(sortInventoryButton);
    }

    private void addConfigurationButton() {
        var configurationButtonFocusedImage = ResourceLocation.fromNamespaceAndPath("better_chests",
                "configuration-button-unfocused");
        var configurationButtonUnfocusedImage = ResourceLocation.fromNamespaceAndPath("better_chests",
                "configuration-button-focused");
        var configurationSprite = new WidgetSprites(configurationButtonFocusedImage, configurationButtonUnfocusedImage);
        var configurationButton = new ImageButton(this.leftPos + this.imageWidth + 1, this.topPos + 1, 16, 16,
                configurationSprite, e -> {
                    Minecraft.getInstance()
                            .setScreen(AutoConfig.getConfigScreen(FabricConfiguration.class, this).get());
                });
        this.addRenderableWidget(configurationButton);
    }

    @Override
    public void render(GuiGraphics p_282060_, int p_282533_, int p_281661_, float p_281873_) {
        super.render(p_282060_, p_282533_, p_281661_, p_281873_);
    }

    @Override
    public void onClose() {
        if (shouldSortOnClose())
            PacketDistributor.sendToServer(new SortRequest(false, shouldSpread()));
        super.onClose();
    }
}
