package it.alessandrocalista.customenchants.listener;

import it.alessandrocalista.customenchants.service.EnchantService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private final EnchantService enchantService;

    public BlockBreakListener(EnchantService enchantService) {
        this.enchantService = enchantService;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        enchantService.handle(event);
    }
}
