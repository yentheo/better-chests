package one.spectra.better_chests.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.math.BlockPos;
import one.spectra.better_chests.BetterChests;
import one.spectra.better_chests.ConfigurationBlockEntity;
import one.spectra.better_chests.common.Configuration;
import net.fabricmc.api.EnvType;


@Environment(EnvType.CLIENT)
@Mixin(LockableContainerBlockEntity.class)
public class SpectraBlockEntity extends BlockEntity implements ConfigurationBlockEntity {

    private boolean spread;
    private boolean sortOnClose;

    public SpectraBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

	@Inject(method = "readNbt", at = @At("HEAD"))
	private void blockentity$read(NbtCompound nbt, WrapperLookup wrapperLookup, CallbackInfo callbackinfo) {
        this.spread = getBoolean(nbt, "better_chests:spread", true);
        this.sortOnClose = getBoolean(nbt, "better_chests:sortOnClose", false);
	}

    private boolean getBoolean(NbtCompound nbt, String key, boolean defaultValue) {
        return nbt.contains(key) ? nbt.getBoolean(key) : defaultValue;
    }

	@Inject(method = "writeNbt", at = @At("HEAD"))
	private void blockentity$write(NbtCompound nbt, WrapperLookup wrapperLookup, CallbackInfo callbackinfo) {
        putBoolean(nbt, "better_chests:spread", spread);
        putBoolean(nbt, "better_chests:sortOnClose", sortOnClose);
	}

    private void putBoolean(NbtCompound nbt, String key, boolean value) {
        if (!nbt.contains(key) || nbt.getBoolean(key) != value) {
            nbt.putBoolean(key, value);
        }
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.spread = configuration.spread();
        this.sortOnClose = configuration.sortOnClose();
        this.markDirty();
    }

    @Override
    public Configuration getConfiguration() {
        return new Configuration(spread, sortOnClose);
    }
    
}
