package it.alessandrocalista.customenchants.enchant.schema;

import com.google.common.base.Preconditions;
import org.bukkit.configuration.ConfigurationSection;

public record LevelSchema(
        long initialCost,
        long costIncrease,
        long maxLevel
) {

    public LevelSchema {
        Preconditions.checkArgument(costIncrease > 0, "costIncrease must be greater than 0");
        Preconditions.checkArgument(maxLevel > 0, "maxLevel must be greater than 0");
        Preconditions.checkArgument(initialCost >= 0, "initialCost must be non-negative");
    }

    public long calculateCost(int level) {
        Preconditions.checkArgument(level > 0 || level > maxLevel, "Level must be between 1 and " + maxLevel);
        return initialCost + (costIncrease * (level - 1));
    }

    public static LevelSchema of(long initialCost, long costIncrease, long maxLevel) {
        return new LevelSchema(initialCost, costIncrease, maxLevel);
    }

    public static LevelSchema fromSection(ConfigurationSection section) {
        int initialCost = section.getInt("initial-cost");
        int costIncrease = section.getInt("cost-increase");
        int maxLevel = section.getInt("max-level");
        return new LevelSchema(initialCost, costIncrease, maxLevel);
    }
}
