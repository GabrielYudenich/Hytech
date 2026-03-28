package com.gabrielyudenich.hytech;

import com.gabrielyudenich.hytech.interactions.HytechBatteryInteraction;
import com.gabrielyudenich.hytech.interactions.HytechPistonInteraction;
import com.gabrielyudenich.hytech.interactions.HytechSwitchInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;

public class HytechPlugin extends JavaPlugin {
    private static HytechPlugin instance;

    public HytechPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public static HytechPlugin getInstance() {
        return instance;
    }

    @Override
    protected void setup() {
        getCodecRegistry(Interaction.CODEC).register(
            "hytech_switch_toggle",
            HytechSwitchInteraction.class,
            HytechSwitchInteraction.CODEC
        );
        getCodecRegistry(Interaction.CODEC).register(
            "hytech_piston_toggle",
            HytechPistonInteraction.class,
            HytechPistonInteraction.CODEC
        );
        getCodecRegistry(Interaction.CODEC).register(
            "hytech_battery_toggle",
            HytechBatteryInteraction.class,
            HytechBatteryInteraction.CODEC
        );
    }
}
