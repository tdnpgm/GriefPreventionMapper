package net.toarupgm.griefpreventionmapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GriefPreventionMapper implements ModInitializer {
    public static final String NAMESPACE = "griefprevention";
    public static final String MOD_ID = "GriefPrevention";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final boolean DEBUGGING = false;

    @Override
    public void onInitialize() {
    }
}
