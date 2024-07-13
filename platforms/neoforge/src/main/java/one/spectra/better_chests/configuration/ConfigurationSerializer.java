package one.spectra.better_chests.configuration;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import net.neoforged.neoforge.network.PacketDistributor;
import one.spectra.better_chests.BetterChestsMod;
import one.spectra.better_chests.common.configuration.ContainerConfiguration;
import one.spectra.better_chests.common.configuration.SortingConfiguration;
import one.spectra.better_chests.communications.requests.ConfigureChestRequest;

public class ConfigurationSerializer implements ConfigSerializer<FabricConfiguration> {

    public ConfigurationSerializer(Config definition, Class<FabricConfiguration> configClass) {
    }

    @Override
    public FabricConfiguration createDefault() {
        return new FabricConfiguration();
    }

    @Override
    public FabricConfiguration deserialize() throws SerializationException {
        try {
            var containerConfigurationHolder = AutoConfig.getConfigHolder(FabricConfiguration.class);
            return containerConfigurationHolder.get();
        } catch (Exception e) {
            return createDefault();
        }
    }

    @Override
    public void serialize(FabricConfiguration configuration) {
        if (configuration.containerSorting != null) {
            var mapper = BetterChestsMod.INJECTOR.getInstance(ConfigurationMapper.class);
            var sortingConfiguration = new SortingConfiguration(mapper.map(configuration.containerSorting.spread),
                    mapper.map(configuration.containerSorting.sortOnClose));
            var containerConfiguration = new ContainerConfiguration(sortingConfiguration);
            PacketDistributor.sendToServer(new ConfigureChestRequest(containerConfiguration));
        }
    }

}
