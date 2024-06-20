package one.spectra.better_chests;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.NetworkPayloadSetup;

public class BetterContainerScreen extends ContainerScreen {

    private int _rowCount = 0;

    public BetterContainerScreen(ChestMenu chestMenu, Inventory playerInventory, Component chestTitle) {
        super(chestMenu, playerInventory, chestTitle);
        this._rowCount = chestMenu.getRowCount();
    }

    @Override
    public void init() {
        super.init();
        BetterChestsMod.LOGGER.info("Adding sort button");
        var sortButtonFocusedImage = ResourceLocation.fromNamespaceAndPath("better_chests", "sort-button-focused");
        var sortButtonUnfocusedImage = ResourceLocation.fromNamespaceAndPath("better_chests", "sort-button-unfocused");
        var sortSprite = new WidgetSprites(sortButtonFocusedImage, sortButtonUnfocusedImage);
        var sortContainerButton = new ImageButton(this.leftPos + this.imageWidth - 20, this.topPos + 5, 13, 9, sortSprite,
                e -> {
                    PacketDistributor.sendToServer(new SortRequest(false));
                });
        this.addRenderableWidget(sortContainerButton);
        BetterChestsMod.LOGGER.info("Adding settings button");
        // var gearIconImage = new ResourceLocation("better_chests:gear-icon.png");
        // var configurationButton = new ImageButton(this.leftPos + this.imageWidth,
        // this.topPos + 5, 9, 9, 0, 0, 9,
        // gearIconImage, 9, 18, e -> {
        // Minecraft.getInstance().setScreen(new
        // ContainerConfigurationScreen(Component.literal("Configuration Screen"),
        // this));
        // });
        // this.addRenderableWidget(configurationButton);

        var containerHeight = _rowCount * 18;

        var sortInventoryButton = new ImageButton(this.leftPos + this.imageWidth - 20,
                this.topPos + 5 + containerHeight + 14, 13, 9, sortSprite, e -> {
                    PacketDistributor.sendToServer(new SortRequest(true));
                });
        this.addRenderableWidget(sortInventoryButton);

    }

    @Override
    public void render(GuiGraphics p_282060_, int p_282533_, int p_281661_, float p_281873_) {
        super.render(p_282060_, p_282533_, p_281661_, p_281873_);
    }
}
