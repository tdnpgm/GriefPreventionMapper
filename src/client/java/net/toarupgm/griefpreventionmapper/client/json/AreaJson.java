package net.toarupgm.griefpreventionmapper.client.json;

import com.google.gson.annotations.SerializedName;
import net.toarupgm.griefpreventionmapper.client.PlayerArea;

import java.util.ArrayList;
import java.util.Map;

public class AreaJson {
    public long lastUpdated;
    public Map<String, ArrayList<PlayerArea>> areas;

    public AreaJson(long lastUpdated, Map<String, ArrayList<PlayerArea>> areas) {
        this.lastUpdated = lastUpdated;
        this.areas = areas;
    }
}
