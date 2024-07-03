package one.spectra.better_chests;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.util.Identifier;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ConfigurationButtonWidget extends TexturedButtonWidget {
    private static final Identifier focused = Identifier.of("better_chests", "configuration-button-focused");
    private static final Identifier unfocused = Identifier.of("better_chests", "configuration-button-unfocused");

    private Screen parent;
    private MinecraftClient client;

    public ConfigurationButtonWidget(int x, int y, Screen parent, MinecraftClient client) {
        super(x, y, 16, 16, new ButtonTextures(unfocused, focused), null, Text.literal(""));

        this.client = client;
        this.parent = parent;
    }

    @Override
    public void onPress() {
        client.setScreen(new ConfigurationScreen(parent));
    }
}
