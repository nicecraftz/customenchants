package it.alessandrocalista.customenchants.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EnchantItemFillEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private final ItemStack[] itemStacks;

    public EnchantItemFillEvent(@NotNull Player player, ItemStack[] itemStacks) {
        super(player);
        this.itemStacks = itemStacks;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public ItemStack[] getItemStacks() {
        return itemStacks;
    }
}
