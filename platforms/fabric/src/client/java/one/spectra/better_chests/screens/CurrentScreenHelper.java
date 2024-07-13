package one.spectra.better_chests.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;

public class CurrentScreenHelper {
    private MinecraftClient client;

    public CurrentScreenHelper() {
        this.client = MinecraftClient.getInstance();
    }

    public boolean isShulkerScreen() {
        return client.player.currentScreenHandler instanceof ShulkerBoxScreenHandler;
    }

    public boolean isPlayerScreen() {
        return client.player.currentScreenHandler instanceof PlayerScreenHandler;
    }

    public boolean isGenericContainerScreen() {
        return client.player.currentScreenHandler instanceof GenericContainerScreenHandler;
    }

    public boolean shouldHandle() {
        if (client == null || client.player == null || client.player.currentScreenHandler == null)
            return false;
        return isShulkerScreen() || isPlayerScreen() || isGenericContainerScreen();
    }
}
