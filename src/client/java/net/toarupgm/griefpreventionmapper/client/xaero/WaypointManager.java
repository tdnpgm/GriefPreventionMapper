package net.toarupgm.griefpreventionmapper.client.xaero;

import xaero.common.core.XaeroMinimapCore;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointWorld;
import xaero.common.minimap.waypoints.WaypointsManager;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointPurpose;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorld;

import java.util.Objects;

public class WaypointManager {
    @SuppressWarnings("deprecation")
    public static WaypointsManager getCurrentWaypointManager()
    {
        return XaeroMinimapCore.currentSession.getWaypointsManager();
    }

    public static WaypointSet getOrCreateWaypointSet(MinimapWorld currentWorld, String waypointSetName)
    {
        WaypointSet waypointSet = currentWorld.getWaypointSet(waypointSetName);
        if(Objects.isNull(waypointSet))
        {
            currentWorld.addWaypointSet(waypointSetName);
            return currentWorld.getWaypointSet(waypointSetName);
        }
        return waypointSet;
    }

    public static void addWaypoint(WaypointSet waypointSet, MinimapWorld minimapWorld, Waypoint waypoint)
    {
        waypointSet.add(waypoint);
    }

    public static void addWaypoint(WaypointSet waypointSet, Waypoint waypoint)
    {
        MinimapWorld minimapWorld = getCurrentWaypointManager().getWorldManager().getCurrentWorld();
        addWaypoint(waypointSet, minimapWorld, waypoint);
    }

    public static void addWaypoint(Waypoint waypoint)
    {
        MinimapWorld minimapWorld = getCurrentWaypointManager().getWorldManager().getCurrentWorld();
        addWaypoint(minimapWorld.getCurrentWaypointSet(), waypoint);
    }

    public static Waypoint registerWaypoint(WaypointSet waypointSet, int x, int y, int z, String name, String initials, WaypointColor waypointColor, WaypointPurpose waypointPurpose)
    {
        Waypoint waypoint = new Waypoint(x, y, z, name, initials, waypointColor, waypointPurpose, false, false);
        addWaypoint(waypointSet, waypoint);
        return waypoint;
    }

    public static Waypoint registerWaypoint(WaypointSet waypointSet, int x, int y, int z, String name, String initials, WaypointColor waypointColor)
    {
        return registerWaypoint(waypointSet, x, y, z, name, initials, waypointColor, WaypointPurpose.NORMAL);
    }

    public static Waypoint registerWaypoint(int x, int y, int z, String name, String initials, WaypointColor waypointColor, WaypointPurpose waypointPurpose)
    {
        Waypoint waypoint = new Waypoint(x, y, z, name, initials, waypointColor, waypointPurpose, false, false);
        addWaypoint(waypoint);
        return waypoint;
    }

    public static Waypoint registerWaypoint(int x, int y, int z, String name, String initials, WaypointColor waypointColor)
    {
        return registerWaypoint(x, y, z, name, initials, waypointColor, WaypointPurpose.NORMAL);
    }
}
