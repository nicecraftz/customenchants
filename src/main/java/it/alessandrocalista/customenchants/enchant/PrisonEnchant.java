package it.alessandrocalista.customenchants.enchant;

import it.alessandrocalista.customenchants.PrisonCustomEnchants;
import it.alessandrocalista.customenchants.enchant.schema.EnchantSchema;
import it.alessandrocalista.customenchants.hook.Hook;
import it.alessandrocalista.customenchants.hook.worldguard.WorldGuardAPI;
import it.alessandrocalista.customenchants.hook.worldguard.WorldGuardHook;
import it.alessandrocalista.customenchants.listener.EventContext;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.List;

public abstract class PrisonEnchant {
    public static final NamespacedKey ROOT_KEY = new NamespacedKey("customenchants", "data");
    private final NamespacedKey enchantKey;

    protected final String id;
    protected final EnchantSchema enchantSchema;

    protected YamlConfiguration enchantConfig;

    public PrisonEnchant(String id) {
        this.id = id;
        enchantKey = new NamespacedKey("enchant", id);
        this.enchantSchema = loadEnchantSchema(id, PrisonCustomEnchants.ENCHANTS_FOLDER);
    }

    public abstract List<String> conflictingEnchants();

    public abstract List<Block> apply(EventContext context);

    public void setEnchantmentLevel(ItemStack itemStack, long level) throws EnchantException {
        if (itemStack == null) {
            throw new EnchantException("The item stack cannot be null.");
        }
        if (level < 0 || level > enchantSchema.levelSchema().maxLevel()) {
            throw new EnchantException("Enchantment level must be between 0 and " +
                    enchantSchema.levelSchema().maxLevel());
        }

        itemStack.editMeta(meta -> {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            PersistentDataContainer enchantContainer;

            if (container.has(ROOT_KEY, PersistentDataType.TAG_CONTAINER)) {
                enchantContainer = container.get(ROOT_KEY, PersistentDataType.TAG_CONTAINER);
            } else {
                enchantContainer = container.getAdapterContext().newPersistentDataContainer();
            }

            if (level == 0) {
                enchantContainer.remove(enchantKey);
            } else {
                enchantContainer.set(enchantKey, PersistentDataType.LONG, level);
            }

            container.set(ROOT_KEY, PersistentDataType.TAG_CONTAINER, enchantContainer);
        });
    }

    public long getEnchantLevel(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer()
                .get(ROOT_KEY, PersistentDataType.TAG_CONTAINER);
        if (dataContainer == null) return 0;
        return dataContainer.getOrDefault(enchantKey, PersistentDataType.LONG, 0L);
    }

    public boolean canBreakBlock(ItemStack stack, Block block) {
        Hook hook = Hook.hooks().get(WorldGuardHook.NAME);
        if (hook == null || !hook.isHooked()) return !block.getDrops(stack).isEmpty();
        WorldGuardHook worldGuardHook = (WorldGuardHook) hook;
        WorldGuardAPI worldGuardAPI = worldGuardHook.getWorldGuardAPI();
        return worldGuardAPI.canBreakAt(block.getLocation());
    }

    public EnchantSchema getEnchantSchema() {
        return enchantSchema;
    }

    public String getId() {
        return id;
    }

    private EnchantSchema loadEnchantSchema(String id, File enchantsFolder) {
        File enchantFile = new File(enchantsFolder, id + ".yml");
        if (!enchantFile.exists()) {
            throw new IllegalArgumentException("Enchant schema file not found: " + enchantFile.getAbsolutePath());
        }

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(enchantFile);
        this.enchantConfig = yamlConfiguration;
        return EnchantSchema.fromSection(yamlConfiguration);
    }
}
