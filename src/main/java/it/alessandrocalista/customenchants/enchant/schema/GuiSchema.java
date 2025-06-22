package it.alessandrocalista.customenchants.enchant.schema;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public record GuiSchema(
        Material icon,
        String displayName
) {

    public GuiSchema {
        Preconditions.checkNotNull(icon);
        Preconditions.checkNotNull(displayName);
        Preconditions.checkArgument(!displayName.isEmpty(), "Display name cannot be empty");
    }

    public ItemStack toItem() {
        ItemStack itemStack = new ItemStack(icon);
        itemStack.editMeta(meta -> meta.displayName(MiniMessage.miniMessage().deserialize(displayName)));
        return itemStack;
    }

    public static GuiSchema fromSection(ConfigurationSection section) {
        Material icon = Registry.MATERIAL.get(NamespacedKey.minecraft(section.getString("icon", "stone")));
        String displayName = section.getString("name", "Custom Enchant");
        return new GuiSchema(icon, displayName);
    }

}
