package one.spectra.better_chests;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SortButtonWidget extends TexturedButtonWidget {
    private static final Identifier focused = Identifier.of("better_chests", "sort-button-focused");
    private static final Identifier unfocused = Identifier.of("better_chests", "sort-button-unfocused");


    public SortButtonWidget(int x, int y, PressAction pressAction) {
        super(x, y, 13, 9, new ButtonTextures(unfocused, focused), pressAction);
    }
}
