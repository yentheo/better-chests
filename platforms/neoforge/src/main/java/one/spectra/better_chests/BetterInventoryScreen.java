package one.spectra.better_chests;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import one.spectra.better_chests.communications.requests.SortRequest;
import one.spectra.better_chests.configuration.FabricGlobalConfiguration;

public class BetterInventoryScreen extends InventoryScreen {

    private ImageButton _sortButton;

    public BetterInventoryScreen(Player player) {
        super(player);
    }

    @Override
    public void init() {
        super.init();
        var globalConfigurationHolder = AutoConfig.getConfigHolder(FabricGlobalConfiguration.class);

        var globalConfiguration = globalConfigurationHolder.get();

        if (globalConfiguration.showSortButton) {

            var positionX = this.leftPos + this.imageWidth - 20;
            var positionY = this.topPos + 72;

            var sortButtonFocusedImage = ResourceLocation.fromNamespaceAndPath("better_chests", "sort-button-focused");
            var sortButtonUnfocusedImage = ResourceLocation.fromNamespaceAndPath("better_chests",
                    "sort-button-unfocused");
            var sortSprite = new WidgetSprites(sortButtonFocusedImage, sortButtonUnfocusedImage);
            _sortButton = new ImageButton(positionX, positionY, 13, 9, sortSprite,
                    e -> {
                        PacketDistributor.sendToServer(new SortRequest(true, globalConfiguration.sorting.spread));
                    });
            this.addRenderableWidget(_sortButton);
        }
    }

    @Override
    public void render(GuiGraphics p_282060_, int p_282533_, int p_281661_, float p_281873_) {
        if (this._sortButton != null) {
            var positionX = this.leftPos + this.imageWidth - 20;
            var positionY = this.topPos + 72;

            if (this._sortButton != null
                    && (this._sortButton.getX() != positionX || this._sortButton.getY() != positionY))
                this._sortButton.setPosition(positionX, positionY);
        }
        super.render(p_282060_, p_282533_, p_281661_, p_281873_);
    }
}
