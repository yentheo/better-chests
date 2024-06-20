package one.spectra.better_chests.abstractions;

import one.spectra.better_chests.common.abstractions.Player;

public interface PlayerFactory {
    Player createPlayer(net.minecraft.entity.player.PlayerEntity player);
}
