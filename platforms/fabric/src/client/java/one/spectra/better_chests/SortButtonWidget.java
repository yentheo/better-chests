package one.spectra.better_chests;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.util.Identifier;
import one.spectra.better_chests.communication.messages.SortRequest;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SortButtonWidget extends TexturedButtonWidget {
    private InventoryType inventory;
    private static final Identifier focused = Identifier.of("better_chests", "sort-button-focused");
    private static final Identifier unfocused = Identifier.of("better_chests", "sort-button-unfocused");

    public SortButtonWidget(int x, int y, InventoryType inventory) {
        super(x, y, 13, 9, new ButtonTextures(unfocused, focused), null, Text.literal(""));
        this.inventory = inventory;
    }

    @Override
    public void onPress() {
        ClientPlayNetworking.send(new SortRequest(this.inventory == InventoryType.PLAYER));
    }
}
