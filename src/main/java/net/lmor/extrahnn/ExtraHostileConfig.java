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
    public static int ultimateSimV1PowerCap;
    public static int ultimateSimV1PowerDuration;
    public static int ultimateSimV2PowerCap;
    public static int ultimateSimV2PowerDuration;
    public static int ultimateSimV3PowerCap;
    public static int ultimateSimV3PowerDuration;
    public static int ultimateSimV4PowerCap;
    public static int ultimateSimV4PowerDuration;

    public static int ultimateFabV1PowerCap;
    public static int ultimateFabV1PowerCost;
    public static int ultimateFabV1PowerDuration;
    public static int ultimateFabV2PowerCap;
    public static int ultimateFabV2PowerCost;
    public static int ultimateFabV2PowerDuration;
    public static int ultimateFabV3PowerCap;
    public static int ultimateFabV3PowerCost;
    public static int ultimateFabV3PowerDuration;
    public static int ultimateFabV4PowerCap;
    public static int ultimateFabV4PowerCost;
    public static int ultimateFabV4PowerDuration;

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

    public static String minimumTierHostile;
    public static String minimumTierExtra;

    public static String[] blackList;

    public ExtraHostileConfig() {
    }

    public static void load() {
        Configuration cfg = new Configuration("extrahnn");
        cfg.setTitle("Extra Hostile Network Config");
        cfg.setComment("All entries in this config file are synced from server to client.");
        ultimateSimV1PowerCap = cfg.getInt("Ultimate Sim Chamber V1 Power Cap", "version_machines", 10000000, 1, Integer.MAX_VALUE, "The maximum FE stored in the Ultimate Simulation Chamber V1.");
        ultimateSimV1PowerDuration = cfg.getInt("Ultimate Sim Chamber V1 Power Duration", "version_machines", 250, 1, Integer.MAX_VALUE, "Ultimate Sim Chamber V1 duration in ticks.");

        ultimateSimV2PowerCap = cfg.getInt("Ultimate Sim Chamber V2 Power Cap", "version_machines", 50000000, 1, Integer.MAX_VALUE, "The maximum FE stored in the Ultimate Simulation Chamber V2.");
        ultimateSimV2PowerDuration = cfg.getInt("Ultimate Sim Chamber V2 Power Duration", "version_machines", 200, 1, Integer.MAX_VALUE, "Ultimate Sim Chamber V2 duration in ticks.");

        ultimateSimV3PowerCap = cfg.getInt("Ultimate Sim Chamber V3 Power Cap", "version_machines", 100000000, 1, Integer.MAX_VALUE, "The maximum FE stored in the Ultimate Simulation Chamber V3.");
        ultimateSimV3PowerDuration = cfg.getInt("Ultimate Sim Chamber V3 Power Duration", "version_machines", 150, 1, Integer.MAX_VALUE, "Ultimate Sim Chamber V3 duration in ticks.");

        ultimateSimV4PowerCap = cfg.getInt("Ultimate Sim Chamber V4 Power Cap", "version_machines", 200000000, 1, Integer.MAX_VALUE, "The maximum FE stored in the Ultimate Simulation Chamber V4.");
        ultimateSimV4PowerDuration = cfg.getInt("Ultimate Sim Chamber V4 Power Duration", "version_machines", 100, 1, Integer.MAX_VALUE, "Ultimate Sim Chamber V4 duration in ticks.");

        ultimateFabV1PowerCap = cfg.getInt("Ultimate Loot Fab V1 Power Cap", "version_machines", 10000000, 1, Integer.MAX_VALUE, "The maximum FE stored in the Ultimate Loot Fabricator V1.");
        ultimateFabV1PowerCost = cfg.getInt("Ultimate Loot Fab V1 Power Cost", "version_machines", 4096, 0, Integer.MAX_VALUE, "The FE/t cost of the Ultimate Loot Fabricator V1.");
        ultimateFabV1PowerDuration = cfg.getInt("Ultimate Loot Fab V1 Power Duration", "version_machines", 50, 1, Integer.MAX_VALUE, "Ultimate Loot Fab V1 duration in ticks.");

        ultimateFabV2PowerCap = cfg.getInt("Ultimate Loot Fab V2 Power Cap", "version_machines", 50000000, 1, Integer.MAX_VALUE, "The maximum FE stored in the Ultimate Loot Fabricator V2.");
        ultimateFabV2PowerCost = cfg.getInt("Ultimate Loot Fab V2 Power Cost", "version_machines", 16384, 0, Integer.MAX_VALUE, "The FE/t cost of the Ultimate Loot Fabricator V2.");
        ultimateFabV2PowerDuration = cfg.getInt("Ultimate Loot Fab V2 Power Duration", "version_machines", 40, 1, Integer.MAX_VALUE, "Ultimate Loot Fab V2 duration in ticks.");

        ultimateFabV3PowerCap = cfg.getInt("Ultimate Loot Fab V3 Power Cap", "version_machines", 100000000, 1, Integer.MAX_VALUE, "The maximum FE stored in the Ultimate Loot Fabricator V3.");
        ultimateFabV3PowerCost = cfg.getInt("Ultimate Loot Fab V3 Power Cost", "version_machines", 65536, 0, Integer.MAX_VALUE, "The FE/t cost of the Ultimate Loot Fabricator V3.");
        ultimateFabV3PowerDuration = cfg.getInt("Ultimate Loot Fab V3 Power Duration", "version_machines", 30, 1, Integer.MAX_VALUE, "Ultimate Loot Fab V3 duration in ticks.");

        ultimateFabV4PowerCap = cfg.getInt("Ultimate Loot Fab V4 Power Cap", "version_machines", 200000000, 1, Integer.MAX_VALUE, "The maximum FE stored in the Ultimate Loot Fabricator V4.");
        ultimateFabV4PowerCost = cfg.getInt("Ultimate Loot Fab V4 Power Cost", "version_machines", 262144, 0, Integer.MAX_VALUE, "The FE/t cost of the Ultimate Loot Fabricator V4.");
        ultimateFabV4PowerDuration = cfg.getInt("Ultimate Loot Fab V4 Power Duration", "version_machines", 20, 1, Integer.MAX_VALUE, "Ultimate Loot Fab V4 duration in ticks.");

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

        minimumTierHostile = cfg.getString("Minimum Upgrade tier - Hostile", "utils", "FAULTY", "What is the minimum entry threshold required for a model to start leveling up?\nSELF_AWARE > SUPERIOR > ADVANCED > BASIC > FAULTY");
        minimumTierExtra = cfg.getString("Minimum Upgrade tier - Extra Hostile", "utils", "AUTONOMOUS", "What is the minimum entry threshold required for a model to start leveling up?\nOMNIPOTENT > SYNTHETIC > ADAPTIVE > INTELLIGENT > AUTONOMOUS");

        blackList = cfg.getStringList("Black list for Simulation Modeling", "utils", new String[0], "Blacklist of models that cannot be improved during simulation modeling. Please note that you need to write the Hostile Network model identifier, for example: hostilenetworks:blaze etc.");

        String tierLowerHostile = minimumTierHostile.toLowerCase();
        String tierLowerExtra = minimumTierExtra.toLowerCase();
        minimumTierHostile = !EHNNUtils.validTiersHostile.contains(tierLowerHostile) ? "faulty": tierLowerHostile;
        minimumTierExtra = !EHNNUtils.validTiersExtra.contains(tierLowerExtra) ? "autonomous": tierLowerExtra;

        if (cfg.hasChanged()) {
            cfg.save();
        }
    }

    static record ConfigMessage(
            int ultimateSimV1PowerCap, int ultimateSimV1PowerDuration,
            int ultimateSimV2PowerCap, int ultimateSimV2PowerDuration,
            int ultimateSimV3PowerCap, int ultimateSimV3PowerDuration,
            int ultimateSimV4PowerCap, int ultimateSimV4PowerDuration,

            int ultimateFabV1PowerCap, int ultimateFabV1PowerCost, int ultimateFabV1PowerDuration,
            int ultimateFabV2PowerCap, int ultimateFabV2PowerCost, int ultimateFabV2PowerDuration,
            int ultimateFabV3PowerCap, int ultimateFabV3PowerCost, int ultimateFabV3PowerDuration,
            int ultimateFabV4PowerCap, int ultimateFabV4PowerCost, int ultimateFabV4PowerDuration,

            int mergerCameraPowerCap, int mergerCameraPowerCost, int mergerCameraPowerDuration,
            int simulationModelingPowerCap, int simulationModelingPowerCost, int simulationModelingPowerDuration,
            int upgradeSpeed, float upgradeSpeedEnergy, int upgradeModuleStackCost, int upgradeDataKill, String minimumUpgradeTierHostile, String minimumUpgradeTierExtra, String[] blackList) {
        public ConfigMessage() {
            this(
                    ExtraHostileConfig.ultimateSimV1PowerCap, ExtraHostileConfig.ultimateSimV1PowerDuration,
                    ExtraHostileConfig.ultimateSimV2PowerCap, ExtraHostileConfig.ultimateSimV2PowerDuration,
                    ExtraHostileConfig.ultimateSimV3PowerCap, ExtraHostileConfig.ultimateSimV3PowerDuration,
                    ExtraHostileConfig.ultimateSimV4PowerCap, ExtraHostileConfig.ultimateSimV4PowerDuration,

                    ExtraHostileConfig.ultimateFabV1PowerCap, ExtraHostileConfig.ultimateFabV1PowerCost, ExtraHostileConfig.ultimateFabV1PowerDuration,
                    ExtraHostileConfig.ultimateFabV2PowerCap, ExtraHostileConfig.ultimateFabV2PowerCost, ExtraHostileConfig.ultimateFabV2PowerDuration,
                    ExtraHostileConfig.ultimateFabV3PowerCap, ExtraHostileConfig.ultimateFabV3PowerCost, ExtraHostileConfig.ultimateFabV3PowerDuration,
                    ExtraHostileConfig.ultimateFabV4PowerCap, ExtraHostileConfig.ultimateFabV4PowerCost, ExtraHostileConfig.ultimateFabV4PowerDuration,

                    ExtraHostileConfig.mergerCameraPowerCap, ExtraHostileConfig.mergerCameraPowerCost, ExtraHostileConfig.mergerCameraPowerDuration, ExtraHostileConfig.simulationModelingPowerCap, ExtraHostileConfig.simulationModelingPowerCost,
                    ExtraHostileConfig.simulationModelingPowerDuration, ExtraHostileConfig.upgradeSpeed, ExtraHostileConfig.upgradeSpeedEnergy, ExtraHostileConfig.upgradeModuleStackCost, ExtraHostileConfig.upgradeDataKill, ExtraHostileConfig.minimumTierHostile, ExtraHostileConfig.minimumTierExtra, ExtraHostileConfig.blackList);
        }

        ConfigMessage(
                int ultimateSimV1PowerCap, int ultimateSimV1PowerDuration,
                int ultimateSimV2PowerCap, int ultimateSimV2PowerDuration,
                int ultimateSimV3PowerCap, int ultimateSimV3PowerDuration,
                int ultimateSimV4PowerCap, int ultimateSimV4PowerDuration,
                int ultimateFabV1PowerCap, int ultimateFabV1PowerCost, int ultimateFabV1PowerDuration,
                int ultimateFabV2PowerCap, int ultimateFabV2PowerCost, int ultimateFabV2PowerDuration,
                int ultimateFabV3PowerCap, int ultimateFabV3PowerCost, int ultimateFabV3PowerDuration,
                int ultimateFabV4PowerCap, int ultimateFabV4PowerCost, int ultimateFabV4PowerDuration,
                int mergerCameraPowerCap, int mergerCameraPowerCost, int mergerCameraPowerDuration,
                int simulationModelingPowerCap, int simulationModelingPowerCost, int simulationModelingPowerDuration,
                int upgradeSpeed, float upgradeSpeedEnergy, int upgradeModuleStackCost, int upgradeDataKill, String minimumUpgradeTierHostile, String minimumUpgradeTierExtra, String[] blackList) {
            this.ultimateSimV1PowerCap = ultimateSimV1PowerCap;
            this.ultimateSimV1PowerDuration = ultimateSimV1PowerDuration;
            this.ultimateSimV2PowerCap = ultimateSimV2PowerCap;
            this.ultimateSimV2PowerDuration = ultimateSimV2PowerDuration;
            this.ultimateSimV3PowerCap = ultimateSimV3PowerCap;
            this.ultimateSimV3PowerDuration = ultimateSimV3PowerDuration;
            this.ultimateSimV4PowerCap = ultimateSimV4PowerCap;
            this.ultimateSimV4PowerDuration = ultimateSimV4PowerDuration;

            this.ultimateFabV1PowerCap = ultimateFabV1PowerCap;
            this.ultimateFabV1PowerCost = ultimateFabV1PowerCost;
            this.ultimateFabV1PowerDuration = ultimateFabV1PowerDuration;
            this.ultimateFabV2PowerCap = ultimateFabV2PowerCap;
            this.ultimateFabV2PowerCost = ultimateFabV2PowerCost;
            this.ultimateFabV2PowerDuration = ultimateFabV2PowerDuration;
            this.ultimateFabV3PowerCap = ultimateFabV3PowerCap;
            this.ultimateFabV3PowerCost = ultimateFabV3PowerCost;
            this.ultimateFabV3PowerDuration = ultimateFabV3PowerDuration;
            this.ultimateFabV4PowerCap = ultimateFabV4PowerCap;
            this.ultimateFabV4PowerCost = ultimateFabV4PowerCost;
            this.ultimateFabV4PowerDuration = ultimateFabV4PowerDuration;

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
            this.blackList = blackList;
        }

        public static class Provider implements MessageProvider<ConfigMessage> {
            public Provider() {
            }

            public Class<?> getMsgClass() {
                return ConfigMessage.class;
            }

            public void write(ConfigMessage msg, FriendlyByteBuf buf) {
                buf.writeInt(msg.blackList.length);
                for (String s : msg.blackList) {
                    buf.writeUtf(s);
                }
                buf.writeInt(msg.ultimateSimV1PowerCap);
                buf.writeInt(msg.ultimateSimV1PowerDuration);
                buf.writeInt(msg.ultimateSimV2PowerCap);
                buf.writeInt(msg.ultimateSimV2PowerDuration);
                buf.writeInt(msg.ultimateSimV3PowerCap);
                buf.writeInt(msg.ultimateSimV3PowerDuration);
                buf.writeInt(msg.ultimateSimV4PowerCap);
                buf.writeInt(msg.ultimateSimV4PowerDuration);
                buf.writeInt(msg.ultimateFabV1PowerCap);
                buf.writeInt(msg.ultimateFabV1PowerCost);
                buf.writeInt(msg.ultimateFabV1PowerDuration);
                buf.writeInt(msg.ultimateFabV2PowerCap);
                buf.writeInt(msg.ultimateFabV2PowerCost);
                buf.writeInt(msg.ultimateFabV2PowerDuration);
                buf.writeInt(msg.ultimateFabV3PowerCap);
                buf.writeInt(msg.ultimateFabV3PowerCost);
                buf.writeInt(msg.ultimateFabV3PowerDuration);
                buf.writeInt(msg.ultimateFabV4PowerCap);
                buf.writeInt(msg.ultimateFabV4PowerCost);
                buf.writeInt(msg.ultimateFabV4PowerDuration);
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
                int blackListLength = buf.readInt();
                String[] blackList = new String[blackListLength];
                for (int i = 0; i < blackListLength; i++) {
                    blackList[i] = buf.readUtf();
                }

                return new ConfigMessage(
                        buf.readInt(), buf.readInt(),
                        buf.readInt(), buf.readInt(),
                        buf.readInt(), buf.readInt(),
                        buf.readInt(), buf.readInt(),

                        buf.readInt(), buf.readInt(), buf.readInt(),
                        buf.readInt(), buf.readInt(), buf.readInt(),
                        buf.readInt(), buf.readInt(), buf.readInt(),
                        buf.readInt(), buf.readInt(), buf.readInt(),

                        buf.readInt(),  buf.readInt(),  buf.readInt(),
                        buf.readInt(), buf.readInt(),  buf.readInt(),
                        buf.readInt(), buf.readFloat(), buf.readInt(), buf.readInt(), buf.readUtf(), buf.readUtf(), blackList);
            }

            public void handle(ConfigMessage msg, Supplier<NetworkEvent.Context> ctx) {
                MessageHelper.handlePacket(() -> {
                    ExtraHostileConfig.ultimateSimV1PowerCap = msg.ultimateSimV1PowerCap;
                    ExtraHostileConfig.ultimateSimV1PowerDuration = msg.ultimateSimV1PowerDuration;
                    ExtraHostileConfig.ultimateSimV2PowerCap = msg.ultimateSimV2PowerCap;
                    ExtraHostileConfig.ultimateSimV2PowerDuration = msg.ultimateSimV2PowerDuration;
                    ExtraHostileConfig.ultimateSimV3PowerCap = msg.ultimateSimV3PowerCap;
                    ExtraHostileConfig.ultimateSimV3PowerDuration = msg.ultimateSimV3PowerDuration;
                    ExtraHostileConfig.ultimateSimV4PowerCap = msg.ultimateSimV4PowerCap;
                    ExtraHostileConfig.ultimateSimV4PowerDuration = msg.ultimateSimV4PowerDuration;
                    ExtraHostileConfig.ultimateFabV1PowerCap = msg.ultimateFabV1PowerCap;
                    ExtraHostileConfig.ultimateFabV1PowerCost = msg.ultimateFabV1PowerCost;
                    ExtraHostileConfig.ultimateFabV1PowerDuration = msg.ultimateFabV1PowerDuration;
                    ExtraHostileConfig.ultimateFabV2PowerCap = msg.ultimateFabV2PowerCap;
                    ExtraHostileConfig.ultimateFabV2PowerCost = msg.ultimateFabV2PowerCost;
                    ExtraHostileConfig.ultimateFabV2PowerDuration = msg.ultimateFabV2PowerDuration;
                    ExtraHostileConfig.ultimateFabV3PowerCap = msg.ultimateFabV3PowerCap;
                    ExtraHostileConfig.ultimateFabV3PowerCost = msg.ultimateFabV3PowerCost;
                    ExtraHostileConfig.ultimateFabV3PowerDuration = msg.ultimateFabV3PowerDuration;
                    ExtraHostileConfig.ultimateFabV4PowerCap = msg.ultimateFabV4PowerCap;
                    ExtraHostileConfig.ultimateFabV4PowerCost = msg.ultimateFabV4PowerCost;
                    ExtraHostileConfig.ultimateFabV4PowerDuration = msg.ultimateFabV4PowerDuration;
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
                    ExtraHostileConfig.minimumTierHostile = msg.minimumUpgradeTierHostile;
                    ExtraHostileConfig.minimumTierExtra = msg.minimumUpgradeTierExtra;
                    ExtraHostileConfig.blackList = msg.blackList;
                }, ctx);
            }

            public Optional<NetworkDirection> getNetworkDirection() {
                return Optional.of(NetworkDirection.PLAY_TO_CLIENT);
            }
        }
    }
}
