package com.nx.oldcombat;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OldCombatMod implements ModInitializer {
    public static final String MOD_ID = "nxoldcombat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("NX Old Combat initialized: 1.8-style PvP is active (no attack cooldown, no sweep attack)");
    }
}
