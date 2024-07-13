package one.spectra.better_chests.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.text.Text;
import one.spectra.better_chests.BetterChests;
import one.spectra.better_chests.BetterChestsClient;
import one.spectra.better_chests.ConfigurationButtonWidget;
import one.spectra.better_chests.InventoryType;
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
import net.minecraft.client.gui.screen.Screen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class ScreenMixin extends Screen {
	private SortButtonWidget inventoryButtonWidget;
	private SortButtonWidget containerSortButton;
	private ConfigurationButtonWidget configurationButton;
	@Shadow
	protected int x;
	@Shadow
	protected int y;
	@Shadow
	protected int backgroundWidth;
	@Shadow
	protected int backgroundHeight;

	private GlobalConfiguration globalConfiguration;
	private ContainerConfiguration containerConfiguration;
	private CurrentScreenHelper currentScreenHelper;

	protected ScreenMixin(Text title) {
		super(Text.empty().append("Test"));
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void invsort$init(CallbackInfo callbackinfo) {
		initialize(callbackinfo);
	}

	@Inject(method = "close", at = @At("HEAD"))
	private void invsort$close(CallbackInfo callbackinfo) {
		if (shouldSortOnClose()) {
			ClientPlayNetworking.send(new SortRequest(false, shouldSpread()));
		}
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

	@Inject(method = "render", at = @At("TAIL"))
	private void invsort$render(CallbackInfo callbackInfo) {
		if (inventoryButtonWidget != null) {
			var x = this.x + this.backgroundWidth - 20;
			if (x != inventoryButtonWidget.getX()) {
				inventoryButtonWidget.setX(x);
			}
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

		var containerConfigurationHolder = AutoConfig.getConfigHolder(FabricConfiguration.class);
		var globalConfigurationHolder = AutoConfig.getConfigHolder(FabricGlobalConfiguration.class);
		var globalConfiguration = globalConfigurationHolder.get();
		var futureResponse = messageService.requestFromServer(GetConfigurationRequest.INSTANCE,
				GetContainerConfigurationResponse.class);

		Executors.newCachedThreadPool().submit(() -> {
			try {
				var response = futureResponse.get();
				this.globalConfiguration = configurationMapper.map(globalConfiguration);
				this.containerConfiguration = response.containerConfiguration();
				var configuration = configurationMapper.map(globalConfiguration, this.containerConfiguration);
				containerConfigurationHolder.setConfig(configuration);

				MinecraftClient.getInstance().submit(() -> {
					if (globalConfiguration.showSortButton) {
						addSortButtons();
					}
					if (globalConfiguration.showConfigurationButton) {
						addConfigurationButton();
					}
				});
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			} catch (Exception e) {
			}
		});

	}

	private void addConfigurationButton() {
		var x = this.x + this.backgroundWidth - 20;
		configurationButton = new ConfigurationButtonWidget(x + 20, y + 1, this, client, () -> {
			var configScreen = currentScreenHelper.isGenericContainerScreen()
					? AutoConfig.getConfigScreen(FabricConfiguration.class, this).get()
					: AutoConfig.getConfigScreen(FabricGlobalConfiguration.class, this).get();
			client.setScreen(configScreen);
		});
		addDrawableChild(configurationButton);
	}

	private void addSortButtons() {
		int numSlots = client.player.currentScreenHandler.slots.size();
		var x = this.x + this.backgroundWidth - 20;

		if (numSlots >= 45) {
			var y = this.y + (numSlots > 36 ? (backgroundHeight - 95) : 6);
			inventoryButtonWidget = new SortButtonWidget(x, y, new PressAction() {

				@Override
				public void onPress(ButtonWidget button) {
					ClientPlayNetworking.send(new SortRequest(true, shouldSpread()));
				}

			});
			this.addDrawableChild(inventoryButtonWidget);
		}
		if (numSlots >= 63) {
			containerSortButton = new SortButtonWidget(x, y + 6, new PressAction() {

				@Override
				public void onPress(ButtonWidget button) {
					ClientPlayNetworking.send(new SortRequest(false, shouldSpread()));
				}

			});
			addDrawableChild(containerSortButton);
		}
	}
}
