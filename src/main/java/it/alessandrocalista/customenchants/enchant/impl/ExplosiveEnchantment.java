package it.alessandrocalista.customenchants.enchant.impl;

import it.alessandrocalista.customenchants.enchant.PrisonEnchant;
import it.alessandrocalista.customenchants.listener.EventContext;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ExplosiveEnchantment extends PrisonEnchant {
    public static final String ENCHANT_ID = "explosive";
    private static final int MAX_RADIUS = 5;
    private static final int MAX_BROKEN_BLOCKS = 40;

    public ExplosiveEnchantment() {
        super(ENCHANT_ID);
    }

    @Override
    public List<String> conflictingEnchants() {
        return List.of();
    }

    @Override
    public List<Block> apply(EventContext context) {
        long level = context.level();
        long maxLevel = enchantSchema.levelSchema().maxLevel();
        Block origin = context.block();
        ItemStack tool = context.itemStack();

        int radius = calculateRadius(level, maxLevel);
        long blocksToBreak = calculateBlockCount(level, maxLevel);

        return getExplodedBlocks(origin, radius, blocksToBreak, tool);
    }

    private int calculateRadius(long level, long maxLevel) {
        return (int) Math.max(1, MAX_RADIUS * level / maxLevel);
    }

    private long calculateBlockCount(long level, long maxLevel) {
        long totalPossible = (2L * MAX_RADIUS + 1) * (2L * MAX_RADIUS + 1) * (2L * MAX_RADIUS + 1) - 1;
        return Math.min(MAX_BROKEN_BLOCKS * level / maxLevel, totalPossible);
    }

    private List<Block> getExplodedBlocks(Block origin, int radius, long needed, ItemStack tool) {
        World world = origin.getWorld();
        int ox = origin.getX(), oy = origin.getY(), oz = origin.getZ();

        List<Block> broken = new ArrayList<>((int) needed + 1);
        Set<Block> visited = new HashSet<>();
        broken.add(origin);
        visited.add(origin);

        long maxAttempts = needed * 10 + 50;
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        long attempts = 0;

        while (broken.size() - 1 < needed && attempts < maxAttempts) {
            attempts++;

            int x = ox + rnd.nextInt(-radius, radius + 1);
            int y = oy + rnd.nextInt(-radius, radius + 1);
            int z = oz + rnd.nextInt(-radius, radius + 1);

            if (x == ox && y == oy && z == oz) continue;

            Block candidate = world.getBlockAt(x, y, z);

            if (!visited.contains(candidate) && canBreakBlock(tool, candidate)) {
                broken.add(candidate);
                visited.add(candidate);
            }
        }

        return broken;
    }
}