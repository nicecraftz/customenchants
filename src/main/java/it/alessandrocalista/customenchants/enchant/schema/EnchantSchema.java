package it.alessandrocalista.customenchants.enchant.schema;

import com.google.common.base.Preconditions;
import org.bukkit.configuration.ConfigurationSection;

public record EnchantSchema(
        EnchantState enchantState,
        String name,
        String description,
        LevelSchema levelSchema,
        GuiSchema guiSchema
) {

    public EnchantSchema {
        Preconditions.checkNotNull(enchantState);
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(description);
        Preconditions.checkNotNull(levelSchema);
        Preconditions.checkArgument(!name.isEmpty(), "Name cannot be empty");
        Preconditions.checkArgument(!description.isEmpty(), "Description cannot be empty");
    }

    public static EnchantSchema fromSection(ConfigurationSection section) {
        boolean enabled = section.getBoolean("enabled", true);
        String name = section.getString("name");
        String description = section.getString("description", "No description provided");
        LevelSchema levelSchema = LevelSchema.fromSection(section.getConfigurationSection("level-schema"));
        GuiSchema guiSchema = GuiSchema.fromSection(section.getConfigurationSection("gui-schema"));
        return new EnchantSchema(new EnchantState(enabled), name, description, levelSchema, guiSchema);
    }

}
