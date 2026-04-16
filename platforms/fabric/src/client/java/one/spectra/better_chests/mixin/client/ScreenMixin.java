package one.spectra.better_chests.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import one.spectra.better_chests.BetterChestsClient;
import one.spectra.better_chests.ConfigurationButtonWidget;
import one.spectra.better_chests.SortButtonWidget;
import one.spectra.better_chests.common.configuration.ContainerConfiguration;
import one.spectra.better_chests.common.configuration.GlobalConfiguration;
import one.spectra.better_chests.communications.MessageService;
import one.spectra.better_chests.communications.requests.GetConfigurationRequest;
import one.spectra.better_chests.communications.requests.SortRequest;
import one.spectra.better_chests.communications.responses.GetContainerConfigurationResponse;
import one.spectra.better_chests.configuration.ConfigurationMapper;
import one.spectra.better_chests.configuration.FabricConfiguration;
import one.spectra.better_chests.configuration.FabricGlobalConfiguration;
import one.spectra.better_chests.screens.CurrentScreenHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.concurrent.ExecutionException;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public abstract class ScreenMixin {
    private SortButtonWidget inventoryButtonWidget;
    private SortButtonWidget containerSortButton;
    private ConfigurationButtonWidget configurationButton;

    private GlobalConfiguration globalConfiguration;
    private ContainerConfiguration containerConfiguration;
    private CurrentScreenHelper currentScreenHelper;

    @Shadow
    @Final
    protected MinecraftClient client;

    @Shadow
    protected abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T drawable);

    @Inject(method = "init", at = @At("TAIL"))
    private void invsort$init(CallbackInfo callbackinfo) {
        if (!((Object) this instanceof HandledScreen<?>))
            return;

        initialize(callbackinfo);
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void invsort$close(CallbackInfo callbackinfo) {
        if (!((Object) this instanceof HandledScreen<?>))
            return;

        if (shouldSortOnClose()) {
            ClientPlayNetworking.send(new SortRequest(false, shouldSpread()));
        }
    }

    @Inject(method = "clearAndInit", at = @At("TAIL"), require = 0)
    private void bc$afterClearAndInit(CallbackInfo ci) {
        if (!((Object)this instanceof HandledScreen<?>)) return;
        initializeButtons();
    }

    private boolean shouldSortOnClose() {
        if (currentScreenHelper.isPlayerScreen() || !currentScreenHelper.shouldHandle())
            return false;

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

    private void initialize(CallbackInfo callbackinfo) {
        this.currentScreenHelper = BetterChestsClient.INJECTOR.getInstance(CurrentScreenHelper.class);
        if (client == null || client.player == null)
            return;

        if (!currentScreenHelper.shouldHandle()) {
            return;
        }

        var messageService = BetterChestsClient.INJECTOR.getInstance(MessageService.class);
        var configurationMapper = BetterChestsClient.INJECTOR.getInstance(ConfigurationMapper.class);
        var globalConfigurationHolder = AutoConfig.getConfigHolder(FabricGlobalConfiguration.class);
        var globalConfiguration = globalConfigurationHolder.get();
        var containerConfigurationHolder = AutoConfig.getConfigHolder(FabricConfiguration.class);

        initializeButtons();

        if (currentScreenHelper.isGenericContainerScreen()) {
            var futureResponse = messageService.requestFromServer(GetConfigurationRequest.INSTANCE,
                    GetContainerConfigurationResponse.class);
            Executors.newCachedThreadPool().submit(() -> {
                try {
                    var response = futureResponse.get();
                    this.containerConfiguration = response.containerConfiguration();
                    var configuration = configurationMapper.map(globalConfiguration, this.containerConfiguration);
                    containerConfigurationHolder.setConfig(configuration);

                    MinecraftClient.getInstance().submit(() -> {
                    });
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                }
            });
        }
    }

    private void initializeButtons() {
        var configurationMapper = BetterChestsClient.INJECTOR.getInstance(ConfigurationMapper.class);
        var globalConfigurationHolder = AutoConfig.getConfigHolder(FabricGlobalConfiguration.class);
        var globalConfiguration = globalConfigurationHolder.get();
        this.globalConfiguration = configurationMapper.map(globalConfiguration);
        if (globalConfiguration.showSortButton) {
            addSortButtons();
        }
        if (globalConfiguration.showConfigurationButton) {
            addConfigurationButton();
        }        
    }

    private void addConfigurationButton() {
        HandledScreenAccessor acc = (HandledScreenAccessor) (Object) this;
        Screen screen = (Screen) (Object) this;
        configurationButton = new ConfigurationButtonWidget(acc.bc$getX() + acc.bc$getBackgroundWidth() + 2,
                acc.bc$getY() + 1, button -> {
                    var configScreen = currentScreenHelper.isGenericContainerScreen()
                            ? AutoConfig.getConfigScreen(FabricConfiguration.class, screen).get()
                            : AutoConfig.getConfigScreen(FabricGlobalConfiguration.class, screen).get();
                    client.setScreen(configScreen);
                });
        addDrawableChild(configurationButton);
    }

    @Inject(method = "render", at = @At("HEAD"), require = 0)
    private void bc$onRender(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!((Object) this instanceof HandledScreen<?>))
            return;
        repositionButtons();
    }

    private void addSortButtons() {
        HandledScreenAccessor acc = (HandledScreenAccessor) (Object) this;
        int numSlots = client.player.currentScreenHandler.slots.size();
        var x = acc.bc$getX() + acc.bc$getBackgroundWidth() - 20;

        if (numSlots >= 45) {
            var y = acc.bc$getY() + (numSlots > 36 ? (acc.bc$getBackgroundHeight() - 95) : 6);
            inventoryButtonWidget = new SortButtonWidget(x, y, new PressAction() {

                @Override
                public void onPress(ButtonWidget button) {
                    ClientPlayNetworking.send(new SortRequest(true, shouldSpread()));
                }

            });
            this.addDrawableChild(inventoryButtonWidget);
        }
        if (numSlots >= 63) {
            containerSortButton = new SortButtonWidget(x, acc.bc$getY() + 6, new PressAction() {

                @Override
                public void onPress(ButtonWidget button) {
                    ClientPlayNetworking.send(new SortRequest(false, shouldSpread()));
                }

            });
            addDrawableChild(containerSortButton);
        }
    }

    private void repositionButtons() {
        HandledScreenAccessor acc = (HandledScreenAccessor) (Object) this;

        if (this.inventoryButtonWidget != null) {
            var x = acc.bc$getX() + acc.bc$getBackgroundWidth() - 20;
            if (x != this.inventoryButtonWidget.getX()) {
                this.inventoryButtonWidget.setX(x);
            }
        }
        if (this.configurationButton != null) {
            var x = acc.bc$getX() + acc.bc$getBackgroundWidth() + 2;
            if (x != this.configurationButton.getX()) {
                this.configurationButton.setX(x);
            }
        }
    }
}
