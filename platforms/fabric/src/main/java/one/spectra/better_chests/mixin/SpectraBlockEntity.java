package one.spectra.better_chests.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import one.spectra.better_chests.ConfigurationBlockEntity;
import one.spectra.better_chests.common.configuration.ContainerConfiguration;
import one.spectra.better_chests.common.configuration.SortingConfiguration;

@Mixin(LockableContainerBlockEntity.class)
public class SpectraBlockEntity extends BlockEntity implements ConfigurationBlockEntity {

    private boolean spread = false;
    private boolean sortOnClose = false;

    public SpectraBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void readData(ReadView view)    {
        this.spread = view.getBoolean("better_chests:spread", false);
        this.sortOnClose = view.getBoolean("better_chests:sortOnClose", false);
    }

    @Override 
    public void writeData(WriteView view)    {
        view.putBoolean("better_chests:spread", spread);
        view.putBoolean("better_chests:sortOnClose", sortOnClose);
    }

    @Override
    public void setConfiguration(ContainerConfiguration configuration) {
        this.spread = configuration.sorting().spread().orElse(false);
        this.sortOnClose = configuration.sorting().sortOnClose().orElse(false);
        this.markDirty();
    }

    @Override
    public ContainerConfiguration getConfiguration() {
        var sortingConfiguration = new SortingConfiguration(Optional.of(this.spread), Optional.of(this.sortOnClose));
        return new ContainerConfiguration(sortingConfiguration);
    }

}
