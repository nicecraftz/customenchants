package it.alessandrocalista.customenchants.hook;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class Hook {
    private static final Map<String, Hook> HOOKS = new HashMap<>();
    private final JavaPlugin plugin;
    private final Logger logger;

    private final String name;
    private boolean hooked = false;

    protected Hook(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.name = name;
    }

    public boolean canHook() {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        return pluginManager.isPluginEnabled(name);
    }

    public void tryHook() {
        if (!canHook()) return;
        try {
            logger.info("Hooking into plugin " + name);
            hook();
            HOOKS.put(name, this);
            hooked = true;
            logger.info("Successfully hooked into " + name);
        } catch (HookException e) {
            logger.severe("Failed to hook into " + name + ": " + e.getMessage());
        }
    }


    public boolean isHooked() {
        return hooked;
    }

    public abstract void hook() throws HookException;


    public static Map<String, Hook> hooks() {
        return HOOKS;
    }
}
