package it.alessandrocalista.customenchants.hook.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;

public class WorldGuardAPI {
    public boolean canBreakAt(Location location) {
        WorldGuard worldGuard = WorldGuard.getInstance();
        WorldGuardPlatform platform = worldGuard.getPlatform();
        RegionContainer regionContainer = platform.getRegionContainer();
        RegionQuery query = regionContainer.createQuery();
        com.sk89q.worldedit.util.Location adaptedLocation = BukkitAdapter.adapt(location);
        ApplicableRegionSet applicableRegions = query.getApplicableRegions(adaptedLocation);
        boolean canBreak = true;


        for (ProtectedRegion region : applicableRegions) {
            if (region.getFlag(Flags.BLOCK_BREAK) == StateFlag.State.DENY) {
                canBreak = false;
                break;
            }
        }

        return canBreak;
    }
}
