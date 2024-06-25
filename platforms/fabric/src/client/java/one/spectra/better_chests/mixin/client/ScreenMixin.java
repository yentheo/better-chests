package one.spectra.better_chests.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import one.spectra.better_chests.BetterChestsClient;
import one.spectra.better_chests.InventoryType;
import one.spectra.better_chests.SortButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

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

	protected ScreenMixin(Text title) {
		super(Text.empty().append("Test"));
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void invsort$init(CallbackInfo callbackinfo) {
		initialize(callbackinfo);
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

		BetterChestsClient.LOGGER.info(client.player.currentScreenHandler.toString());
		var isGenericContainerScreen = client.player.currentScreenHandler instanceof GenericContainerScreenHandler;
		var isPlayerScreen = client.player.currentScreenHandler instanceof PlayerScreenHandler;

		if (!isGenericContainerScreen && !isPlayerScreen) {
			var screenHandler = client.player.currentScreenHandler;
			BetterChestsClient.LOGGER.info(screenHandler.toString());
			return;
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
			// var y = this.y + (numSlots > 36 ? (backgroundHeight - 95) : 6);
			// this.addDrawableChild(new MoveUpButtonWidget(x - 11, y));
			// this.addDrawableChild(new MoveDownButtonWidget(x - 22, y));
		}
	}
}
