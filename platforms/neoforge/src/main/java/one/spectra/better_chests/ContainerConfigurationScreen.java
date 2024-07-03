package one.spectra.better_chests;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import one.spectra.better_chests.communications.MessageService;
import one.spectra.better_chests.communications.requests.ConfigureChestRequest;
import one.spectra.better_chests.communications.requests.GetConfigurationRequest;
import one.spectra.better_chests.communications.responses.GetConfigurationResponse;

public class ContainerConfigurationScreen extends Screen {

    private Checkbox _spreadCheckbox;
    private Checkbox _sortOnCloseCheckbox;
    private Screen _parentScreen;

    protected ContainerConfigurationScreen(Component p_96550_, Screen parentScreen) {
        super(p_96550_);
        _parentScreen = parentScreen;
        var messageService = BetterChestsMod.NETWORK_INJECTOR.getInstance(MessageService.class);
        var futureResponse = messageService.requestFromServer(new GetConfigurationRequest(),
                GetConfigurationResponse.class);
        Executors.newCachedThreadPool().submit(() -> {
            try {
                var response = futureResponse.get();
                LogUtils.getLogger().info(String.valueOf(response.spread()));
                if (response.spread() && !_spreadCheckbox.selected())
                    _spreadCheckbox.onPress();
                if (response.sortOnClose() && !_sortOnCloseCheckbox.selected())
                    _sortOnCloseCheckbox.onPress();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void init() {
        super.init();
        // Checkbox.builder((Component)Component.literal("Spread"), new Font());
        _spreadCheckbox = Checkbox.builder(Component.literal("Spread"), minecraft.font)
                .selected(true)
                .onValueChange((sender, value) -> {
                })
                .pos(32, 64)
                .build();

        this.addRenderableWidget(_spreadCheckbox);
        _sortOnCloseCheckbox = Checkbox.builder(Component.literal("Sort on close"), minecraft.font)
                .onValueChange((sender, value) -> {
                })
                .pos(32, 88)
                .build();

        this.addRenderableWidget(_sortOnCloseCheckbox);

        var saveChangesButton = Button.builder(Component.literal("Save changes"), e -> {
            var configureChestRequest = new ConfigureChestRequest(_spreadCheckbox.selected(), _sortOnCloseCheckbox.selected());
            PacketDistributor.sendToServer(configureChestRequest);
            Minecraft.getInstance().setScreen(_parentScreen);
        }).pos(32, 112).build();
        this.addRenderableWidget(saveChangesButton);
    }

    @Override
    public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_) {
        if (p_96552_ == 256 && this.shouldCloseOnEsc()) {
            Minecraft.getInstance().setScreen(_parentScreen);
            return true;
        }
        return super.keyPressed(p_96552_, p_96553_, p_96554_);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        // https://docs.minecraftforge.net/en/1.19.x/gui/screens/
    }
}
