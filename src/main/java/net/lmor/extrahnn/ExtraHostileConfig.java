package net.lmor.extrahnn;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import shadows.placebo.config.Configuration;

import java.util.Optional;
import java.util.function.Supplier;

public class ExtraHostileConfig {
    public static int ultimateSimPowerCap;
    public static int ultimateSimPowerDuration;
    public static int ultimateFabPowerCap;
    public static int ultimateFabPowerCost;
    public static int ultimateFabPowerDuration;
    public static int mergerCameraPowerCap;
    public static int mergerCameraPowerCost;
    public static int mergerCameraPowerDuration;

    public ExtraHostileConfig() {
    }

    public static void load() {
        Configuration cfg = new Configuration("hostilenetworks");
        cfg.setTitle("Extra Hostile Network Config");
        cfg.setComment("All entries in this config file are synced from server to client.");
        ultimateSimPowerCap = cfg.getInt("Ultimate Sim Chamber Power Cap", "power", 200000000, 1, Integer.MAX_VALUE, "The maximum FE stored in the Ultimate Simulation Chamber.");
        ultimateSimPowerDuration = cfg.getInt("Ultimate Sim Chamber Power Duration", "power", 150, 1, Integer.MAX_VALUE, "Ultimate Sim Chamber duration in ticks.");

        ultimateFabPowerCap = cfg.getInt("Ultimate Loot Fab Power Cap", "power", 100000000, 1, Integer.MAX_VALUE, "The maximum FE stored in the Ultimate Loot Fabricator.");
        ultimateFabPowerCost = cfg.getInt("Ultimate Loot Fab Power Cost", "power", 4096, 0, Integer.MAX_VALUE, "The FE/t cost of the Ultimate Loot Fabricator.");
        ultimateFabPowerDuration = cfg.getInt("Ultimate Loot Fab Power Duration", "power", 30, 1, Integer.MAX_VALUE, "Ultimate Loot Fab duration in ticks.");

        mergerCameraPowerCap = cfg.getInt("Merger Camera Power Cap", "power", 100000000, 1, Integer.MAX_VALUE, "The maximum FE stored in the Merger Camera.");
        mergerCameraPowerCost = cfg.getInt("Merger Camera Power Cost", "power", 25000, 0, Integer.MAX_VALUE, "Total RF required to fuse data models in a Merger Camera.");
        mergerCameraPowerDuration = cfg.getInt("Merger Camera Power Duration", "power", 500, 1, Integer.MAX_VALUE, "Merger Camera duration in ticks.");


        if (cfg.hasChanged()) {
            cfg.save();
        }
    }
}
