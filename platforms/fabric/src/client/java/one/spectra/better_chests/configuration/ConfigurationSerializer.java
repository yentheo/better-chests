package one.spectra.better_chests.configuration;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import one.spectra.better_chests.BetterChestsClient;
import one.spectra.better_chests.common.configuration.ContainerConfiguration;
import one.spectra.better_chests.common.configuration.SortingConfiguration;
import one.spectra.better_chests.communications.requests.ConfigureChestRequest;
import one.spectra.better_chests.screens.CurrentScreenHelper;

public class ConfigurationSerializer implements ConfigSerializer<FabricConfiguration> {

    private CurrentScreenHelper currentScreenHelper;

    public ConfigurationSerializer(Config definition, Class<FabricConfiguration> configClass) {
        currentScreenHelper = BetterChestsClient.INJECTOR.getInstance(CurrentScreenHelper.class);
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
        if (currentScreenHelper.shouldHandle()) {
            var mapper = BetterChestsClient.INJECTOR.getInstance(ConfigurationMapper.class);
            var sortingConfiguration = new SortingConfiguration(mapper.map(configuration.containerSorting.spread),
                    mapper.map(configuration.containerSorting.sortOnClose));
            var containerConfiguration = new ContainerConfiguration(sortingConfiguration);
            ClientPlayNetworking.send(new ConfigureChestRequest(containerConfiguration));
        }
    }

}
