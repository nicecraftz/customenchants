package it.alessandrocalista.customenchants;

import it.alessandrocalista.customenchants.commands.CustomEnchantsCommand;
import it.alessandrocalista.customenchants.hook.Hook;
import it.alessandrocalista.customenchants.hook.worldguard.WorldGuardHook;
import it.alessandrocalista.customenchants.listener.BlockBreakListener;
import it.alessandrocalista.customenchants.service.EnchantService;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class PrisonCustomEnchants extends JavaPlugin {
    public static File ENCHANTS_FOLDER;
    private EnchantService enchantService;
    private final Set<Hook> hookSet = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ENCHANTS_FOLDER = new File(getDataFolder(), "enchantments");
        saveEnchantsDirectory();
        initializeHooks();

        enchantService = new EnchantService();
        enchantService.reloadEnchants();

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new BlockBreakListener(enchantService), this);
        hookSet.forEach(Hook::tryHook);

        loadCommands();
    }

    private void loadCommands() {
        PluginCommand customenchants = getCommand("customenchants");
        customenchants.setExecutor(new CustomEnchantsCommand(this));
    }

    private void initializeHooks() {
        hookSet.add(new WorldGuardHook(this));
    }

    public EnchantService getEnchantService() {
        return enchantService;
    }

    private void saveEnchantsDirectory() {
        if (!ENCHANTS_FOLDER.exists() && !ENCHANTS_FOLDER.mkdirs()) {
            getLogger().severe("Impossibile creare la cartella enchants.");
            return;
        }

        File[] existing = ENCHANTS_FOLDER.listFiles();
        if (existing != null && existing.length > 0) {
            getLogger().info("La cartella enchants è già popolata. Skip copy.");
            return;
        }

        String prefix = "enchantments/";

        try (JarFile jar = new JarFile(getFile())) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.startsWith(prefix)) continue;

                String relativePath = name.substring(prefix.length());
                if (relativePath.isEmpty()) continue;

                File outFile = new File(ENCHANTS_FOLDER, relativePath);
                if (entry.isDirectory()) {
                    if (!outFile.exists() && !outFile.mkdirs()) {
                        getLogger().warning("Non posso creare directory: " + outFile.getPath());
                    }
                } else {
                    File parent = outFile.getParentFile();
                    if (!parent.exists() && !parent.mkdirs()) {
                        getLogger().warning("Non posso creare la cartella: " + parent.getPath());
                        continue;
                    }
                    try (InputStream in = jar.getInputStream(entry)) {
                        Files.copy(in, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
            getLogger().info("Directory enchants copiata con successo.");
        } catch (IOException e) {
            throw new RuntimeException("Errore durante l'estrazione della cartella enchants", e);
        }
    }
}