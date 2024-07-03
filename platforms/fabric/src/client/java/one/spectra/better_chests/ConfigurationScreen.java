package one.spectra.better_chests;

import java.util.concurrent.ExecutionException;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;
import one.spectra.better_chests.communications.MessageService;
import one.spectra.better_chests.communications.requests.ConfigureChestRequest;
import one.spectra.better_chests.communications.requests.GetConfigurationRequest;
import one.spectra.better_chests.communications.responses.GetConfigurationResponse;
import java.util.concurrent.Executors;

@Environment(EnvType.CLIENT)
public class ConfigurationScreen extends Screen {

    private Screen parent;

    private MessageService messageService;

    private CheckboxWidget spreadCheckboxWidget;
    private CheckboxWidget sortOnCloseCheckboxWidget;

    protected ConfigurationScreen(Screen parent) {
        super(Text.literal("Configuration"));
        messageService = BetterChestsClient.INJECTOR.getInstance(MessageService.class);

        var futureResponse = messageService.requestFromServer(GetConfigurationRequest.INSTANCE,
                GetConfigurationResponse.class);

        Executors.newCachedThreadPool().submit(() -> {
            try {
                var response = futureResponse.get();
                if (response.spread() && !spreadCheckboxWidget.isSelected())
                    spreadCheckboxWidget.onPress();
                if (response.sortOnClose() && !sortOnCloseCheckboxWidget.isSelected())
                    sortOnCloseCheckboxWidget.onPress();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        this.parent = parent;
    }

    @Override
    public void init() {
        spreadCheckboxWidget = CheckboxWidget.builder(Text.literal("Spread"), textRenderer)
                .pos(32, 64)
                .build();
        addDrawableChild(spreadCheckboxWidget);
        sortOnCloseCheckboxWidget = CheckboxWidget.builder(Text.literal("Sort on close"), textRenderer)
                .pos(32, 88)
                .build();
        addDrawableChild(sortOnCloseCheckboxWidget);

        var buttonSaveChanges = ButtonWidget.builder(Text.literal("Save changes"), button -> {
            ClientPlayNetworking.send(
                    new ConfigureChestRequest(spreadCheckboxWidget.isChecked(), sortOnCloseCheckboxWidget.isChecked()));
            this.close();
        })
                .position(32, 114)
                .build();
        addDrawableChild(buttonSaveChanges);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
