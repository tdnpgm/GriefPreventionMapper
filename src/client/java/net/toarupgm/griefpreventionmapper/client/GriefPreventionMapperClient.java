package net.toarupgm.griefpreventionmapper.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.toarupgm.griefpreventionmapper.GriefPreventionMapper;
import net.toarupgm.griefpreventionmapper.client.config.ModOptions;

import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.toarupgm.griefpreventionmapper.GriefPreventionMapper.LOGGER;

public class GriefPreventionMapperClient implements ClientModInitializer {
    public PlayerAreaMapper playerAreaMapper;
    public Path areaJsonPath = FabricLoader.getInstance().getConfigDir().resolve("areas.json");

    @Override
    public void onInitializeClient() {
        this.playerAreaMapper = new PlayerAreaMapper();
        ClientReceiveMessageEvents.ALLOW_GAME.register((text, overlay) -> {
            if(overlay) return true;
            Matcher matcher = ModOptions.config().generalConfig.landownerPattern.matcher(text.getString());
            if(matcher.find())
            {
                String playerName = matcher.group(1);
                PlayerAreaMapper.INSTANCE.recordPlayerName(playerName);
            }
            return true;
        });

        try {
            PlayerAreaMapper.INSTANCE.load(areaJsonPath);
        } catch (IOException e) {
            LOGGER.error("Could not load json file");
            throw new RuntimeException(e);
        }
        ClientLifecycleEvents.CLIENT_STOPPING.register(minecraftClient -> {
            try {
                PlayerAreaMapper.INSTANCE.save(areaJsonPath);
            } catch (IOException e) {
                LOGGER.error("Could not save json file");
                throw new RuntimeException(e);
            }
        });
    }
}
