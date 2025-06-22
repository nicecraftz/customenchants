package it.alessandrocalista.customenchants.hook.worldguard;

import it.alessandrocalista.customenchants.hook.Hook;
import it.alessandrocalista.customenchants.hook.HookException;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldGuardHook extends Hook {
    public static final String NAME = "WorldGuard";
    private WorldGuardAPI worldGuardAPI;

    public WorldGuardHook(JavaPlugin plugin) {
        super(plugin, NAME);
    }

    @Override
    public void hook() throws HookException {
        worldGuardAPI = new WorldGuardAPI();
    }

    public WorldGuardAPI getWorldGuardAPI() {
        return worldGuardAPI;
    }
}
