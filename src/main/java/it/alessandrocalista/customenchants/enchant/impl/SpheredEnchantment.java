package it.alessandrocalista.customenchants.enchant.impl;

import it.alessandrocalista.customenchants.enchant.PrisonEnchant;
import it.alessandrocalista.customenchants.listener.EventContext;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SpheredEnchantment extends PrisonEnchant {
    public static final String ENCHANT_ID = "sphered";
    private static final int MAX_RADIUS = 5;

    public SpheredEnchantment() {
        super(ENCHANT_ID);
    }

    @Override
    public List<String> conflictingEnchants() {
        return List.of(ExplosiveEnchantment.ENCHANT_ID);
    }

    @Override
    public List<Block> apply(EventContext context) {
        int radius = calculateRadius(context.level());
        return collectBreakableBlocks(context, radius);
    }

    private int calculateRadius(long level) {
        long maxLevel = enchantSchema.levelSchema().maxLevel();
        return (int) Math.max(1, MAX_RADIUS * level / maxLevel);
    }

    private List<Block> collectBreakableBlocks(EventContext context, int radius) {
        List<Block> toBreak = new ArrayList<>();
        Block origin = context.block();
        ItemStack tool = context.itemStack();
        World world = origin.getWorld();

        int ox = origin.getX(), oy = origin.getY(), oz = origin.getZ();
        double rSquared = radius * radius;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (isCenterBlock(dx, dy, dz)) continue;
                    if (isOutsideSphere(dx, dy, dz, rSquared)) continue;

                    Block target = world.getBlockAt(ox + dx, oy + dy, oz + dz);
                    if (canBreakBlock(tool, target)) {
                        toBreak.add(target);
                    }
                }
            }
        }
        return toBreak;
    }

    private boolean isCenterBlock(int dx, int dy, int dz) {
        return dx == 0 && dy == 0 && dz == 0;
    }

    private boolean isOutsideSphere(int dx, int dy, int dz, double rSquared) {
        return dx * dx + dy * dy + dz * dz > rSquared;
    }
}