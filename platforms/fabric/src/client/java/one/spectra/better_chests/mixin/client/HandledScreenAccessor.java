package one.spectra.better_chests.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screen.ingame.HandledScreen;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {
    @Accessor("x") int bc$getX();
    @Accessor("y") int bc$getY();
    @Accessor("backgroundWidth") int bc$getBackgroundWidth();
    @Accessor("backgroundHeight") int bc$getBackgroundHeight();
}
