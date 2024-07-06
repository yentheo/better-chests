package one.spectra.better_chests.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.text.Text;
import one.spectra.better_chests.BetterChests;
import one.spectra.better_chests.BetterChestsClient;
import one.spectra.better_chests.ConfigurationButtonWidget;
import one.spectra.better_chests.InventoryType;
import one.spectra.better_chests.SortButtonWidget;
import one.spectra.better_chests.communications.MessageService;
import one.spectra.better_chests.communications.requests.GetConfigurationRequest;
import one.spectra.better_chests.communications.requests.SortRequest;
import one.spectra.better_chests.communications.responses.GetConfigurationResponse;
import net.minecraft.client.gui.screen.Screen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class ScreenMixin extends Screen {
	private SortButtonWidget _inventoryButtonWidget;
	@Shadow
	protected int x;
	@Shadow
	protected int y;
	@Shadow
	protected int backgroundWidth;
	@Shadow
	protected int backgroundHeight;

	private boolean sortOnClose = false;

	protected ScreenMixin(Text title) {
		super(Text.empty().append("Test"));
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void invsort$init(CallbackInfo callbackinfo) {
		initialize(callbackinfo);
	}

	@Inject(method = "close", at = @At("HEAD"))
	private void invsort$close(CallbackInfo callbackinfo) {
		if (sortOnClose)
			ClientPlayNetworking.send(new SortRequest(false));
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void invsort$render(CallbackInfo callbackInfo) {
		if (_inventoryButtonWidget != null) {
			var x = this.x + this.backgroundWidth - 20;
			if (x != _inventoryButtonWidget.getX()) {
				_inventoryButtonWidget.setX(x);
			}
		}
	}

	private void initialize(CallbackInfo callbackinfo) {
		if (client == null || client.player == null)
			return;

		var isGenericContainerScreen = client.player.currentScreenHandler instanceof GenericContainerScreenHandler;
		var isPlayerScreen = client.player.currentScreenHandler instanceof PlayerScreenHandler;
		var isShulkerScreen = client.player.currentScreenHandler instanceof ShulkerBoxScreenHandler;

		if (!isGenericContainerScreen && !isPlayerScreen && !isShulkerScreen) {
			return;
		}

		if (isGenericContainerScreen) {
			var messageService = BetterChestsClient.INJECTOR.getInstance(MessageService.class);

			var futureResponse = messageService.requestFromServer(GetConfigurationRequest.INSTANCE,
					GetConfigurationResponse.class);

			Executors.newCachedThreadPool().submit(() -> {
				try {
					var response = futureResponse.get();
					BetterChests.LOGGER.debug("Received chest configuration.");
					BetterChests.LOGGER.debug("spread: {}, sortOnClose: {}", response.spread(), response.sortOnClose());
					sortOnClose = response.sortOnClose();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			});
		}

		int numSlots = client.player.currentScreenHandler.slots.size();
		var x = this.x + this.backgroundWidth - 20;

		if (numSlots >= 45) {
			var y = this.y + (numSlots > 36 ? (backgroundHeight - 95) : 6);
			_inventoryButtonWidget = new SortButtonWidget(x, y, InventoryType.PLAYER);
			this.addDrawableChild(_inventoryButtonWidget);
		}
		if (numSlots >= 63) {
			var widget2 = new SortButtonWidget(x, this.y + 6, InventoryType.CHEST);
			this.addDrawableChild(widget2);
			if (!isShulkerScreen) {
				var configurationButtonWidget = new ConfigurationButtonWidget(x + 20, this.y + 1, this, client);
				this.addDrawableChild(configurationButtonWidget);
			}
		}

	}
}
