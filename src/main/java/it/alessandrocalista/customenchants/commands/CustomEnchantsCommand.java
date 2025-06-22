package it.alessandrocalista.customenchants.commands;

import com.destroystokyo.paper.MaterialTags;
import it.alessandrocalista.customenchants.PrisonCustomEnchants;
import it.alessandrocalista.customenchants.enchant.EnchantException;
import it.alessandrocalista.customenchants.enchant.PrisonEnchant;
import it.alessandrocalista.customenchants.service.EnchantService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CustomEnchantsCommand implements CommandExecutor {
    private final PrisonCustomEnchants plugin;
    private final EnchantService enchantService;

    public CustomEnchantsCommand(PrisonCustomEnchants plugin) {
        this.plugin = plugin;
        this.enchantService = plugin.getEnchantService();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>This command can only be executed by a player.");
            return false;
        }

        if (!player.hasPermission("customenchants.admin")) {
            player.sendRichMessage("<red>You do not have permission to use this command.");
            return false;
        }

        if (args.length == 0) {
            player.sendRichMessage("<green>Custom Enchants Plugin is running!");
            plugin.reloadConfig();
            plugin.getEnchantService().reloadEnchants();
            return true;
        }

        if (args[0].toLowerCase().equals("enchant")) {
            ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
            if (!MaterialTags.PICKAXES.isTagged(itemInMainHand)) {
                player.sendRichMessage("<red>You must hold a pickaxe to use this command.");
                return false;
            }

            if (args.length < 3) {
                player.sendRichMessage("<red>Usage: /customenchants enchant <enchant_id> <level>");
                return false;
            }

            String enchantId = args[1];
            long level;
            try {
                level = Long.parseLong(args[2]);
            } catch (NumberFormatException e) {
                player.sendRichMessage("<red>Invalid level. Please enter a valid number.");
                return false;
            }

            HashMap<String, PrisonEnchant> enchants = enchantService.getEnchants();
            PrisonEnchant prisonEnchant = enchants.get(enchantId);
            if (prisonEnchant == null) {
                player.sendRichMessage("<red>Enchantment with ID '" + enchantId + "' does not exist.");
                return false;
            }

            try {
                prisonEnchant.setEnchantmentLevel(itemInMainHand, level);
                player.sendRichMessage("<green>Successfully set enchantment '" + enchantId + "' to level " + level + ".");
            } catch (EnchantException e) {
                player.sendRichMessage("<red>Error: " + e.getMessage());
                return false;
            }
        }

        return true;
    }
}
