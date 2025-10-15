package net.lmor.extrahnn;

import dev.shadowsoffire.placebo.config.Configuration;
import dev.shadowsoffire.placebo.network.MessageHelper;
import dev.shadowsoffire.placebo.network.MessageProvider;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

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

    public static String minimumUpgradeTierHostile;
    public static String minimumUpgradeTierExtra;

    public ExtraHostileConfig() {
    }

    public static void load() {
        Configuration cfg = new Configuration("extrahnn");
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

        minimumUpgradeTierHostile = cfg.getString("Minimum Upgrade tier - Hostile", "utils", "FAULTY", "What is the minimum entry threshold required for a model to start leveling up?\nSELF_AWARE > SUPERIOR > ADVANCED > BASIC > FAULTY");
        minimumUpgradeTierExtra = cfg.getString("Minimum Upgrade tier - Extra Hostile", "utils", "FAULTY", "What is the minimum entry threshold required for a model to start leveling up?\nOMNIPOTENT > SYNTHETIC > ADAPTIVE > INTELLIGENT > AUTONOMOUS");

        String tierLowerHostile = minimumUpgradeTierHostile.toLowerCase();
        String tierLowerExtra = minimumUpgradeTierExtra.toLowerCase();
        minimumUpgradeTierHostile = !EHNNUtils.validTiersHostile.contains(tierLowerHostile) ? "faulty": tierLowerHostile;
        minimumUpgradeTierExtra = !EHNNUtils.validTiersExtra.contains(tierLowerExtra) ? "autonomous": tierLowerExtra;

        if (cfg.hasChanged()) {
            cfg.save();
        }
    }

    static record ConfigMessage(int ultimateSimPowerCap, int ultimateSimPowerDuration, int ultimateFabPowerCap, int ultimateFabPowerCost, int ultimateFabPowerDuration, int mergerCameraPowerCap,
                                int mergerCameraPowerCost, int mergerCameraPowerDuration, int simulationModelingPowerCap, int simulationModelingPowerCost, int simulationModelingPowerDuration,
                                int upgradeSpeed, float upgradeSpeedEnergy, int upgradeModuleStackCost, int upgradeDataKill, String minimumUpgradeTierHostile, String minimumUpgradeTierExtra) {
        public ConfigMessage() {
            this(ExtraHostileConfig.ultimateSimPowerCap, ExtraHostileConfig.ultimateSimPowerDuration, ExtraHostileConfig.ultimateFabPowerCap, ExtraHostileConfig.ultimateFabPowerCost, ExtraHostileConfig.ultimateFabPowerDuration,
                    ExtraHostileConfig.mergerCameraPowerCap, ExtraHostileConfig.mergerCameraPowerCost, ExtraHostileConfig.mergerCameraPowerDuration, ExtraHostileConfig.simulationModelingPowerCap, ExtraHostileConfig.simulationModelingPowerCost,
                    ExtraHostileConfig.simulationModelingPowerDuration, ExtraHostileConfig.upgradeSpeed, ExtraHostileConfig.upgradeSpeedEnergy, ExtraHostileConfig.upgradeModuleStackCost, ExtraHostileConfig.upgradeDataKill, ExtraHostileConfig.minimumUpgradeTierHostile, ExtraHostileConfig.minimumUpgradeTierExtra);
        }

        ConfigMessage(int ultimateSimPowerCap, int ultimateSimPowerDuration, int ultimateFabPowerCap, int ultimateFabPowerCost, int ultimateFabPowerDuration, int mergerCameraPowerCap, int mergerCameraPowerCost, int mergerCameraPowerDuration,
                      int simulationModelingPowerCap, int simulationModelingPowerCost, int simulationModelingPowerDuration, int upgradeSpeed, float upgradeSpeedEnergy, int upgradeModuleStackCost, int upgradeDataKill, String minimumUpgradeTierHostile, String minimumUpgradeTierExtra) {
            this.ultimateSimPowerCap = ultimateSimPowerCap;
            this.ultimateSimPowerDuration = ultimateSimPowerDuration;
            this.ultimateFabPowerCap = ultimateFabPowerCap;
            this.ultimateFabPowerCost = ultimateFabPowerCost;
            this.ultimateFabPowerDuration = ultimateFabPowerDuration;
            this.mergerCameraPowerCap = mergerCameraPowerCap;
            this.mergerCameraPowerCost = mergerCameraPowerCost;
            this.mergerCameraPowerDuration = mergerCameraPowerDuration;
            this.simulationModelingPowerCap = simulationModelingPowerCap;
            this.simulationModelingPowerCost = simulationModelingPowerCost;
            this.simulationModelingPowerDuration = simulationModelingPowerDuration;
            this.upgradeSpeed = upgradeSpeed;
            this.upgradeSpeedEnergy = upgradeSpeedEnergy;
            this.upgradeModuleStackCost = upgradeModuleStackCost;
            this.upgradeDataKill = upgradeDataKill;
            this.minimumUpgradeTierHostile = minimumUpgradeTierHostile;
            this.minimumUpgradeTierExtra = minimumUpgradeTierExtra;
        }

        public int ultimateSimPowerCap() {
            return this.ultimateSimPowerCap;
        }
        public int ultimateSimPowerDuration() {
            return this.ultimateSimPowerDuration;
        }
        public int ultimateFabPowerCap() {
            return this.ultimateFabPowerCap;
        }
        public int ultimateFabPowerCost() {
            return this.ultimateFabPowerCost;
        }
        public int ultimateFabPowerDuration() {
            return this.ultimateFabPowerDuration;
        }
        public int mergerCameraPowerCap() {
            return this.mergerCameraPowerCap;
        }
        public int mergerCameraPowerCost() {
            return this.mergerCameraPowerCost;
        }
        public int mergerCameraPowerDuration() {
            return this.mergerCameraPowerDuration;
        }
        public int simulationModelingPowerCap() {
            return this.simulationModelingPowerCap;
        }
        public int simulationModelingPowerCost() {
            return this.simulationModelingPowerCost;
        }
        public int simulationModelingPowerDuration() {
            return this.simulationModelingPowerDuration;
        }
        public int upgradeSpeed() {
            return this.upgradeSpeed;
        }
        public float upgradeSpeedEnergy() {
            return this.upgradeSpeedEnergy;
        }
        public int upgradeModuleStackCost() {
            return this.upgradeModuleStackCost;
        }
        public int upgradeDataKill() {
            return this.upgradeDataKill;
        }
        public String minimumUpgradeTierHostile() { return this.minimumUpgradeTierHostile; }
        public String minimumUpgradeTierExtra() { return this.minimumUpgradeTierExtra; }

        public static class Provider implements MessageProvider<ConfigMessage> {
            public Provider() {
            }

            public Class<?> getMsgClass() {
                return ConfigMessage.class;
            }

            public void write(ConfigMessage msg, FriendlyByteBuf buf) {
                buf.writeInt(msg.ultimateSimPowerCap);
                buf.writeInt(msg.ultimateSimPowerDuration);
                buf.writeInt(msg.ultimateFabPowerCap);
                buf.writeInt(msg.ultimateFabPowerCost);
                buf.writeInt(msg.ultimateFabPowerDuration);
                buf.writeInt(msg.mergerCameraPowerCap);
                buf.writeInt(msg.mergerCameraPowerCost);
                buf.writeInt(msg.mergerCameraPowerDuration);
                buf.writeInt(msg.simulationModelingPowerCap);
                buf.writeInt(msg.simulationModelingPowerCost);
                buf.writeInt(msg.simulationModelingPowerDuration);
                buf.writeInt(msg.upgradeSpeed);
                buf.writeFloat(msg.upgradeSpeedEnergy);
                buf.writeInt(msg.upgradeModuleStackCost);
                buf.writeInt(msg.upgradeDataKill);
                buf.writeUtf(msg.minimumUpgradeTierHostile);
                buf.writeUtf(msg.minimumUpgradeTierExtra);
            }

            public ConfigMessage read(FriendlyByteBuf buf) {
                return new ConfigMessage(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(),  buf.readInt(),  buf.readInt(),  buf.readInt(),  buf.readInt(),
                        buf.readInt(),  buf.readInt(), buf.readInt(), buf.readFloat(), buf.readInt(), buf.readInt(), buf.readUtf(), buf.readUtf());
            }

            public void handle(ConfigMessage msg, Supplier<NetworkEvent.Context> ctx) {
                MessageHelper.handlePacket(() -> {
                    ExtraHostileConfig.ultimateSimPowerCap = msg.ultimateSimPowerCap;
                    ExtraHostileConfig.ultimateSimPowerDuration = msg.ultimateSimPowerDuration;
                    ExtraHostileConfig.ultimateFabPowerCap = msg.ultimateFabPowerCap;
                    ExtraHostileConfig.ultimateFabPowerCost = msg.ultimateFabPowerCost;
                    ExtraHostileConfig.ultimateFabPowerDuration = msg.ultimateFabPowerDuration;
                    ExtraHostileConfig.mergerCameraPowerCap = msg.mergerCameraPowerCap;
                    ExtraHostileConfig.mergerCameraPowerCost = msg.mergerCameraPowerCost;
                    ExtraHostileConfig.mergerCameraPowerDuration = msg.mergerCameraPowerDuration;
                    ExtraHostileConfig.simulationModelingPowerCap = msg.simulationModelingPowerCap;
                    ExtraHostileConfig.simulationModelingPowerCost = msg.simulationModelingPowerCost;
                    ExtraHostileConfig.simulationModelingPowerDuration = msg.simulationModelingPowerDuration;
                    ExtraHostileConfig.upgradeSpeed = msg.upgradeSpeed;
                    ExtraHostileConfig.upgradeSpeedEnergy = msg.upgradeSpeedEnergy;
                    ExtraHostileConfig.upgradeModuleStackCost = msg.upgradeModuleStackCost;
                    ExtraHostileConfig.upgradeDataKill = msg.upgradeDataKill;
                    ExtraHostileConfig.minimumUpgradeTierHostile = msg.minimumUpgradeTierHostile;
                    ExtraHostileConfig.minimumUpgradeTierExtra = msg.minimumUpgradeTierExtra;
                }, ctx);
            }

            public Optional<NetworkDirection> getNetworkDirection() {
                return Optional.of(NetworkDirection.PLAY_TO_CLIENT);
            }
        }
    }
}
