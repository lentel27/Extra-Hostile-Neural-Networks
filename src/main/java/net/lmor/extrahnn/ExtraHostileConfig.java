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

    public static int simulationModelingPowerCap;
    public static int simulationModelingPowerCost;
    public static int simulationModelingPowerDuration;

    public static int upgradeSpeed;
    public static float upgradeSpeedEnergy;
    public static int upgradeModuleStackCost;
    public static int upgradeDataKill;

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

        simulationModelingPowerCap = cfg.getInt("Simulation Modeling Power Cap", "power", 100000000, 1, Integer.MAX_VALUE, "The maximum FE stored in the Simulation Modeling.");
        simulationModelingPowerCost = cfg.getInt("Simulation Modeling Power Cost", "power", 100000, 0, Integer.MAX_VALUE, "The FE/t cost of the Ultimate Simulation Modeling.");
        simulationModelingPowerDuration = cfg.getInt("Simulation Modeling Power Duration", "power", 100, 1, Integer.MAX_VALUE, "Simulation Modeling duration in ticks.");

        upgradeSpeed = cfg.getInt("Upgrade Speed", "upgrade", 50, 1, 100, "Acceleration of mechanisms with a speed map");
        upgradeSpeedEnergy = cfg.getFloat("Upgrade Speed Energy", "upgrade", 2.0f, 0.0f, Float.MAX_VALUE, "How many times will energy consumption increase?");

        upgradeModuleStackCost = cfg.getInt("Upgrade Module Stack Cost", "upgrade", 10000000, 0, Integer.MAX_VALUE, "How much energy is needed to move to the next tier?");

        upgradeDataKill = cfg.getInt("Upgrade Data Kill", "upgrade", 2, 0, Integer.MAX_VALUE, "How many times more data?");

        if (cfg.hasChanged()) {
            cfg.save();
        }
    }
}
