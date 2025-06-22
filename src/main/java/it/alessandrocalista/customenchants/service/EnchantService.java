package it.alessandrocalista.customenchants.service;

import com.destroystokyo.paper.MaterialTags;
import it.alessandrocalista.customenchants.enchant.PrisonEnchant;
import it.alessandrocalista.customenchants.enchant.impl.SpheredEnchantment;
import it.alessandrocalista.customenchants.enchant.impl.ExplosiveEnchantment;
import it.alessandrocalista.customenchants.enchant.schema.EnchantState;
import it.alessandrocalista.customenchants.event.EnchantItemFillEvent;
import it.alessandrocalista.customenchants.listener.EventContext;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EnchantService {
    private final HashMap<String, PrisonEnchant> enchants = new HashMap<>();

    public void reloadEnchants() {
        enchants.clear();
        Arrays.asList(new ExplosiveEnchantment(), new SpheredEnchantment())
                .forEach(enchant -> enchants.put(enchant.getId(), enchant));
    }

    public HashMap<String, PrisonEnchant> getEnchants() {
        return enchants;
    }

    public void handle(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!isValidPlayer(player)) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isValidTool(item)) return;

        PersistentDataContainer dataContainer = getEnchantDataContainer(item);
        if (dataContainer == null) return;

        event.setDropItems(false);
        processEnchantments(player, item, event.getBlock(), dataContainer);
    }

    private boolean isValidPlayer(Player player) {
        return player.getGameMode() != GameMode.CREATIVE;
    }

    private boolean isValidTool(ItemStack item) {
        return MaterialTags.PICKAXES.isTagged(item) && item.hasItemMeta();
    }

    private PersistentDataContainer getEnchantDataContainer(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.get(PrisonEnchant.ROOT_KEY, PersistentDataType.TAG_CONTAINER);
    }

    private void processEnchantments(Player player,
            ItemStack item,
            Block targetBlock,
            PersistentDataContainer dataContainer) {
        boolean inventoryFullWarned = false;

        for (NamespacedKey key : dataContainer.getKeys()) {
            String enchantId = key.getKey();
            PrisonEnchant enchant = enchants.get(enchantId);
            if (enchant == null) continue;

            EnchantState state = enchant.getEnchantSchema().enchantState();
            if (!state.isEnabled()) continue;
            if (!enchant.canBreakBlock(item, targetBlock)) return;
            EventContext context = new EventContext(player, enchant.getEnchantLevel(item), item, targetBlock);
            List<Block> blocks = enchant.apply(context);
            if (blocks.isEmpty()) continue;

            inventoryFullWarned = handleBlockDrops(player, blocks, inventoryFullWarned);
        }
    }

    private boolean handleBlockDrops(Player player, List<Block> blocks, boolean warned) {
        for (Block block : blocks) {
            if (block.getType().isAir()) continue;

            if (!warned && player.getInventory().firstEmpty() == -1) {
                player.sendRichMessage("<red>Your inventory is full! Some blocks may not be collected.");
                warned = true;
            }

            ItemStack[] drops = block.getDrops().toArray(new ItemStack[0]);
            HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(drops);

            for (ItemStack item : leftovers.values()) {
                player.getWorld().dropItemNaturally(block.getLocation(), item);
            }

            Bukkit.getPluginManager().callEvent(new EnchantItemFillEvent(player, drops));
            block.setType(Material.AIR);
        }
        return warned;
    }

}
