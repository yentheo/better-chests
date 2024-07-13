package one.spectra.better_chests.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.math.BlockPos;
import one.spectra.better_chests.ConfigurationBlockEntity;
import one.spectra.better_chests.common.configuration.ContainerConfiguration;
import one.spectra.better_chests.common.configuration.GlobalConfiguration;
import one.spectra.better_chests.common.configuration.SortingConfiguration;

@Mixin(LockableContainerBlockEntity.class)
public class SpectraBlockEntity extends BlockEntity implements ConfigurationBlockEntity {

    private Optional<Boolean> spread;
    private Optional<Boolean> sortOnClose;

    public SpectraBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    private void blockentity$read(NbtCompound nbt, WrapperLookup wrapperLookup, CallbackInfo callbackinfo) {
        this.spread = getBoolean(nbt, "better_chests:spread");
        this.sortOnClose = getBoolean(nbt, "better_chests:sortOnClose");
    }

    private Optional<Boolean> getBoolean(NbtCompound nbt, String key) {
        return nbt.contains(key) ? Optional.of(nbt.getBoolean(key)) : Optional.empty();
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    private void blockentity$write(NbtCompound nbt, WrapperLookup wrapperLookup, CallbackInfo callbackinfo) {
        putBoolean(nbt, "better_chests:spread", spread);
        putBoolean(nbt, "better_chests:sortOnClose", sortOnClose);
    }

    private void putBoolean(NbtCompound nbt, String key, Optional<Boolean> value) {
        var valuePresent = value != null && value.isPresent();
        if (!valuePresent && nbt.contains(key)) {
            nbt.remove(key);
        }

        if (valuePresent && (!nbt.contains(key) || nbt.getBoolean(key) != value.get())) {
            nbt.putBoolean(key, value.get());
        }
    }

    @Override
    public void setConfiguration(ContainerConfiguration configuration) {
        this.spread = configuration.sorting().spread();
        this.sortOnClose = configuration.sorting().sortOnClose();
        this.markDirty();
    }

    @Override
    public ContainerConfiguration getConfiguration() {
        var sortingConfiguration = new SortingConfiguration(this.spread, this.sortOnClose);
        return new ContainerConfiguration(sortingConfiguration);
    }

}
