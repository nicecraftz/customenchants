package it.alessandrocalista.customenchants.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record EventContext(
        Player player,
        long level,
        ItemStack itemStack,
        Block block
) {
}
