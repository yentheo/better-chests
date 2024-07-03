package one.spectra.better_chests;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

public class ScreenEvents {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void mainScreenEvent(ScreenEvent.Opening event) {
        var screen = event.getNewScreen();
        var minecraft = Minecraft.getInstance();
        if (screen instanceof ContainerScreen) {
            var containerScreen = (ContainerScreen) screen;
            event.setNewScreen(new BetterContainerScreen(containerScreen.getMenu(), new Inventory(minecraft.player), containerScreen.getTitle()));
        } else if (screen instanceof ShulkerBoxScreen) {
            // var containerScreen = (ShulkerBoxScreen) screen;
            // event.setNewScreen(new BetterShulkerBoxScreen(containerScreen.getMenu(), new Inventory(minecraft.player), containerScreen.getTitle()));
        } else if (screen instanceof InventoryScreen) {
            event.setNewScreen(new BetterInventoryScreen(minecraft.player));
        }
    }
}
