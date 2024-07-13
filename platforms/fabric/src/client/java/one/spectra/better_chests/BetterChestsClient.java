package one.spectra.better_chests;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.autoconfig.gui.registry.api.GuiTransformer;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.text.Text;
import one.spectra.better_chests.communications.ClientMessageRegistrar;
import one.spectra.better_chests.communications.responses.GetContainerConfigurationResponse;
import one.spectra.better_chests.configuration.ConfigurationSerializer;
import one.spectra.better_chests.configuration.FabricConfiguration;
import one.spectra.better_chests.configuration.FabricGlobalConfiguration;
import one.spectra.better_chests.configuration.OptionalBoolean;

public class BetterChestsClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("better-chests");

	public static Injector INJECTOR;

	@Override
	public void onInitializeClient() {
		INJECTOR = BetterChests.INJECTOR.createChildInjector(new BetterChestsClientModule());
		var messageRegistrar = INJECTOR.getInstance(ClientMessageRegistrar.class);
		messageRegistrar.registerResponseToClient(GetContainerConfigurationResponse.class,
				GetContainerConfigurationResponse.ID, GetContainerConfigurationResponse.CODEC);

		AutoConfig.register(FabricGlobalConfiguration.class, Toml4jConfigSerializer::new);
		AutoConfig.register(FabricConfiguration.class, ConfigurationSerializer::new);

		var fabricConfigurationGuiRegistry = AutoConfig.getGuiRegistry(FabricConfiguration.class);
		fabricConfigurationGuiRegistry.registerPredicateTransformer(new GuiTransformer() {

			@Override
			public List<AbstractConfigListEntry> transform(List<AbstractConfigListEntry> arg0, String arg1, Field arg2,
					Object arg3, Object arg4, GuiRegistryAccess arg5) {
				var builder = ConfigEntryBuilder.create();
				var buttons = new ArrayList<AbstractConfigListEntry>();
				try {
					buttons.add(builder
							.startEnumSelector(Text.translatable(arg1), OptionalBoolean.class, (OptionalBoolean)arg2.get(arg3))
							.setDefaultValue(OptionalBoolean.Global)
							.setSaveConsumer(item -> {
								try {
									arg2.set(arg3, item);
								} catch (IllegalArgumentException | IllegalAccessException e) {
								}
							})
							.build());
				} catch (IllegalArgumentException | IllegalAccessException e) {
				}
				return buttons;
			}

		}, x -> x.getType() == OptionalBoolean.class);
	}
}