package it.alessandrocalista.customenchants.enchant.schema;

public class EnchantState {
    private boolean enabled;

    public EnchantState(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
