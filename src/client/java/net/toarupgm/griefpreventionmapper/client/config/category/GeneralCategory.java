package net.toarupgm.griefpreventionmapper.client.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.text.Text;
import net.toarupgm.griefpreventionmapper.client.config.ModConfig;

import java.util.regex.Pattern;

public class GeneralCategory {
    public static ConfigCategory create(ModConfig defaults, ModConfig config) {
        return ConfigCategory.createBuilder()
                .name(Text.of("General"))

                .option(Option.<String>createBuilder()
                        .name(Text.of("Landowner Pattern"))
                        .binding(defaults.generalConfig.landownerRegex,
                                () -> config.generalConfig.landownerRegex,
                                newValue -> {
                                    config.generalConfig.landownerRegex = newValue;
                                    config.generalConfig.landownerPattern = Pattern.compile(newValue);
                                })
                        .controller(StringControllerBuilder::create)
                        .build())
                .option(Option.<String>createBuilder()
                        .name(Text.of("WaypointSet Name"))
                        .binding(defaults.generalConfig.waypointSetName,
                                () -> config.generalConfig.waypointSetName,
                                newValue -> {
                                    config.generalConfig.waypointSetName = newValue;
                                })
                        .controller(StringControllerBuilder::create)
                        .build())
                .build();

    }
}
