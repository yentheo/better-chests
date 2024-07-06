package one.spectra.better_chests;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

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

    private MessageService messageService;

    private Checkbox spreadCheckbox;
    private Checkbox sortOnCloseCheckbox;
    private Button saveChangesButton;
    private Screen parentScreen;

    protected ContainerConfigurationScreen(Component p_96550_, Screen parentScreen) {
        super(p_96550_);
        this.parentScreen = parentScreen;
        this.messageService = BetterChestsMod.NETWORK_INJECTOR.getInstance(MessageService.class);
    }

    @Override
    public void init() {
        super.init();
        // Checkbox.builder((Component)Component.literal("Spread"), new Font());
        spreadCheckbox = Checkbox.builder(Component.literal("Spread"), minecraft.font)
                .selected(true)
                .onValueChange((sender, value) -> {
                })
                .pos(32, 64)
                .build();

        this.addRenderableWidget(spreadCheckbox);
        sortOnCloseCheckbox = Checkbox.builder(Component.literal("Sort on close"), minecraft.font)
                .onValueChange((sender, value) -> {
                })
                .pos(32, 88)
                .build();

        this.addRenderableWidget(sortOnCloseCheckbox);

        saveChangesButton = Button.builder(Component.literal("Save changes"), e -> {
            var configureChestRequest = new ConfigureChestRequest(spreadCheckbox.selected(), sortOnCloseCheckbox.selected());
            PacketDistributor.sendToServer(configureChestRequest);
            Minecraft.getInstance().setScreen(parentScreen);
        }).pos(32, 112).build();
        this.addRenderableWidget(saveChangesButton);
        var futureResponse = messageService.requestFromServer(new GetConfigurationRequest(),
                GetConfigurationResponse.class);
        Executors.newCachedThreadPool().submit(() -> {
            try {
                var response = futureResponse.get();
                // BetterChestsMod.LOGGER.info("Received chest configuration.");
                // BetterChestsMod.LOGGER.info("spread: {}, sortOnClose: {}", response.spread(), response.sortOnClose());
                if (response.spread() != spreadCheckbox.selected())
                    spreadCheckbox.onPress();
                if (response.sortOnClose() != sortOnCloseCheckbox.selected())
                    sortOnCloseCheckbox.onPress();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_) {
        if (p_96552_ == 256 && this.shouldCloseOnEsc()) {
            Minecraft.getInstance().setScreen(parentScreen);
            return true;
        }
        return super.keyPressed(p_96552_, p_96553_, p_96554_);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        var screenCenterX = width / 2;
        var screenCenterY = height / 2;
        var saveChangesButtonCenterX = saveChangesButton.getWidth() / 2;
        spreadCheckbox.setX(screenCenterX - saveChangesButtonCenterX);
        spreadCheckbox.setY(screenCenterY - sortOnCloseCheckbox.getHeight() - 8);
        sortOnCloseCheckbox.setX(screenCenterX - saveChangesButtonCenterX);
        sortOnCloseCheckbox.setY(screenCenterY);
        saveChangesButton.setX(screenCenterX - saveChangesButtonCenterX);
        saveChangesButton.setY(screenCenterY + sortOnCloseCheckbox.getHeight() + 8);

        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        // https://docs.minecraftforge.net/en/1.19.x/gui/screens/
    }
}
