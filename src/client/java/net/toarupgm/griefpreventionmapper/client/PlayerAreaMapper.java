package net.toarupgm.griefpreventionmapper.client;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.math.BlockPos;
import net.toarupgm.griefpreventionmapper.client.config.ModOptions;
import net.toarupgm.griefpreventionmapper.client.json.AreaJson;
import net.toarupgm.griefpreventionmapper.client.xaero.WaypointManager;
import org.jetbrains.annotations.NotNull;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorld;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.*;

import static net.toarupgm.griefpreventionmapper.GriefPreventionMapper.LOGGER;

public class PlayerAreaMapper {
    private String lastPlayerName;
    private Map<String, ArrayList<PlayerArea>> areas;
    private ArrayList<BlockPos> corners;
    public static PlayerAreaMapper INSTANCE;
    public Gson gson;
    Type AREA_JSON_TYPE = new TypeToken<AreaJson>(){}.getType();


    public PlayerAreaMapper() {
        INSTANCE = this;

        this.corners = new ArrayList<>();
        this.areas = new HashMap<>();
        this.lastPlayerName = "";
        this.gson = (new GsonBuilder()).setPrettyPrinting().create();
    }

    public void addXaeroWaypoint(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
    {
        int waypointX = (minX + maxX) / 2;
        int waypointY = (minY + maxY) / 2;
        int waypointZ = (minZ + maxZ) / 2;
        LOGGER.info("Adding Xaero Waypoint ({}, {})",waypointX,waypointZ);

        String waypointName = this.lastPlayerName;
        String waypointInitial = waypointName.substring(0,2);
        Random random = new Random(lastPlayerName.hashCode());
        WaypointColor waypointColor = WaypointColor.fromIndex(random.nextInt(WaypointColor.values().length));

        String waypointSetName = ModOptions.config().generalConfig.waypointSetName;
        MinimapWorld currentWorld = WaypointManager.getCurrentWaypointManager().getWorldManager().getCurrentWorld();
        WaypointSet waypointSet = WaypointManager.getOrCreateWaypointSet(currentWorld, waypointSetName);

        for(Waypoint waypoint : waypointSet.getWaypoints())
        {
            if( waypoint.getX()==waypointX &&
                    waypoint.getY()==waypointY &&
                    waypoint.getZ()==waypointZ)
            {
                waypoint.setName(waypointName);
                waypoint.setWaypointColor(waypointColor);
                return;
            }
        }

        WaypointManager.registerWaypoint(waypointSet, waypointX, waypointY, waypointZ, waypointName, waypointInitial, waypointColor);
    }

    public void createArea(String playerName, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        if(playerName.isEmpty()) return;
        if(!this.areas.containsKey(playerName))
        {
            this.areas.put(playerName, new ArrayList<>());
        }
        ArrayList<PlayerArea> playerAreas = this.areas.get(playerName);

        PlayerArea playerArea = new PlayerArea(playerName, minX, minY, minZ, maxX, maxY, maxZ);
        playerAreas.add(playerArea);


        for(String ownerName : areas.keySet())
        {
            if(Objects.equals(ownerName, playerName)) continue;

            Iterator<PlayerArea> ownerAreasIterator = areas.get(ownerName).iterator();

            while (ownerAreasIterator.hasNext()) {
                PlayerArea ownerArea = ownerAreasIterator.next();
                boolean collision = ownerArea.checkCollision(playerArea);
                if (collision){
                    int waypointX = (ownerArea.getMinX()+ownerArea.getMaxX())/2;
                    int waypointY = (ownerArea.getMinY()+ownerArea.getMaxY())/2;
                    int waypointZ = (ownerArea.getMinZ()+ownerArea.getMaxZ())/2;
                    String waypointSetName = ModOptions.config().generalConfig.waypointSetName;
                    MinimapWorld currentWorld = WaypointManager.getCurrentWaypointManager().getWorldManager().getCurrentWorld();
                    WaypointSet waypointSet = WaypointManager.getOrCreateWaypointSet(currentWorld, waypointSetName);


                    Iterator<Waypoint> waypointIterator = waypointSet.getWaypoints().iterator();
                    while (waypointIterator.hasNext()) {
                        Waypoint waypoint = waypointIterator.next();
                        if(     waypoint.getX()==waypointX &&
                                waypoint.getY()==waypointY &&
                                waypoint.getZ()==waypointZ) {
                            waypointIterator.remove();
                        }
                    }
                    ownerAreasIterator.remove();
                }
            }
        }


        try {
            Class.forName("xaero.common.core.XaeroMinimapCore");

            addXaeroWaypoint(minX, minY, minZ, maxX, maxY, maxZ);
        } catch (ClassNotFoundException e) {
        }
    }

    public void pushArea()
    {
        int minX = Integer.MAX_VALUE, maxX = 0;
        int minY = Integer.MAX_VALUE, maxY = 0;
        int minZ = Integer.MAX_VALUE, maxZ = 0;

        for(BlockPos corner : this.corners)
        {
            if(minX > corner.getX()) {
                minX = corner.getX();
            }
            if(minY > corner.getY()) {
                minY = corner.getY();
            }
            if(minZ > corner.getZ()) {
                minZ = corner.getZ();
            }
            if(maxX < corner.getX()) {
                maxX = corner.getX();
            }
            if(maxY < corner.getY()) {
                maxY = corner.getY();
            }
            if(maxZ < corner.getZ()) {
                maxZ = corner.getZ();
            }
        }

        LOGGER.info("Area Found: [{}] {}:{} {}:{}", this.lastPlayerName, minX, maxX, minZ, maxZ);

        this.createArea(this.lastPlayerName, minX, minY, minZ, maxX, maxY, maxZ);

        this.corners.clear();
        this.lastPlayerName = "";
    }

    public void recordPlayerName(String playerName)
    {
        this.lastPlayerName = playerName;
        if(this.corners.size()>=8)
        {
            this.pushArea();
        }
    }

    public void recordCorner(BlockPos corner)
    {
        corners.add(corner);
        if(corners.size()>=8)
        {
            this.pushArea();
        }
    }

    public void load(Path areaPath) throws IOException {
        File file = areaPath.toFile();

        if (!file.exists()) {
            LOGGER.error("Area file does not exist (PATH: {})", areaPath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            long lastUpdated = root.get("lastUpdated").getAsLong();

            JsonObject areasJson = root.getAsJsonObject("areas");

            for (String playerName : areasJson.keySet()) {
                JsonArray areaArray = areasJson.getAsJsonArray(playerName);

                for (JsonElement areaElement : areaArray) {
                    PlayerArea area = parsePlayerArea(playerName, areaElement.getAsJsonObject());

                    this.createArea(playerName, area.getMinX(), area.getMinY(), area.getMinZ(), area.getMaxX(), area.getMaxY(), area.getMaxZ());
                }
            }
        }
    }

    private PlayerArea parsePlayerArea(String playerName, JsonObject areaJson) {
        int minX = areaJson.get("minX").getAsInt();
        int minY = areaJson.get("minY").getAsInt();
        int minZ = areaJson.get("minZ").getAsInt();
        int maxX = areaJson.get("maxX").getAsInt();
        int maxY = areaJson.get("maxY").getAsInt();
        int maxZ = areaJson.get("maxZ").getAsInt();

        return new PlayerArea(playerName, minX, minY, minZ, maxX, maxY, maxZ);
    }


    public void save(Path areaPath) throws IOException {
        File file = areaPath.toFile();
        if (!file.exists()) {
            if (!file.createNewFile()) {
                LOGGER.error("Could not create area file (PATH: {})", areaPath);
                return;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));
             JsonWriter gsonWriter = new JsonWriter(writer)) {

            AreaJson areaJson = new AreaJson(System.currentTimeMillis(), areas);
            JsonObject root = new JsonObject();
            root.addProperty("lastUpdated", areaJson.lastUpdated);
            JsonObject inner = new JsonObject();

            for (Map.Entry<String, ArrayList<PlayerArea>> entry : areaJson.areas.entrySet()) {
                String playerName = entry.getKey();
                JsonArray innerArray = getAreaJsonElements(entry);
                inner.add(playerName, innerArray);
            }

            root.add("areas", inner);
            gson.toJson(root, gsonWriter);
        }
    }

    private static @NotNull JsonArray getAreaJsonElements(Map.Entry<String, ArrayList<PlayerArea>> entry) {
        ArrayList<PlayerArea> value = entry.getValue();
        JsonArray innerArray = new JsonArray();
        value.forEach(playerArea -> {
            JsonObject playerAreaJson = new JsonObject();
            playerAreaJson.addProperty("minX",playerArea.getMinX());
            playerAreaJson.addProperty("minY",playerArea.getMinX());
            playerAreaJson.addProperty("minZ",playerArea.getMinZ());
            playerAreaJson.addProperty("maxX",playerArea.getMaxX());
            playerAreaJson.addProperty("maxY",playerArea.getMaxX());
            playerAreaJson.addProperty("maxZ",playerArea.getMaxZ());
            innerArray.add(playerAreaJson);
        });
        return innerArray;
    }
}
