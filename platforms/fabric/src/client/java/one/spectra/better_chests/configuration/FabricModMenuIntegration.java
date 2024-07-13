package one.spectra.better_chests.configuration;

import net.fabricmc.api.EnvType;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FabricModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(FabricGlobalConfiguration.class, parent).get();
    }
}
