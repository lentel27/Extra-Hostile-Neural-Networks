package net.lmor.extrahnn;

import dev.shadowsoffire.placebo.config.Configuration;
import dev.shadowsoffire.placebo.network.PayloadProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.Utf8String;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

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

    public static Configuration load() {
        Configuration cfg = new Configuration(ExtraHostileNetworks.MOD_ID);
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

        if (cfg.hasChanged()) cfg.save();
        return cfg;
    }

    public record ConfigMessage(int ultimateSimV1PowerCap, int ultimateSimV1PowerDuration,
                                int ultimateSimV2PowerCap, int ultimateSimV2PowerDuration,
                                int ultimateSimV3PowerCap, int ultimateSimV3PowerDuration,
                                int ultimateSimV4PowerCap, int ultimateSimV4PowerDuration,
                                int ultimateFabV1PowerCap, int ultimateFabV1PowerCost, int ultimateFabV1PowerDuration,
                                int ultimateFabV2PowerCap, int ultimateFabV2PowerCost, int ultimateFabV2PowerDuration,
                                int ultimateFabV3PowerCap, int ultimateFabV3PowerCost, int ultimateFabV3PowerDuration,
                                int ultimateFabV4PowerCap, int ultimateFabV4PowerCost, int ultimateFabV4PowerDuration,

                                int mergerCameraPowerCap, int mergerCameraPowerCost, int mergerCameraPowerDuration,
                                int simulationModelingPowerCap, int simulationModelingPowerCost, int simulationModelingPowerDuration,

                                int upgradeSpeed, float upgradeSpeedEnergy, int upgradeModuleStackCost, int upgradeDataKill,
                                String minimumTierHostile, String minimumTierExtra, String[] blackList) implements CustomPacketPayload {

        static StreamCodec<ByteBuf, String[]> STRING_LIST = new StreamCodec<>() {
            private static final int maxLength = 32767;

            @Override
            public String @NotNull [] decode(@NotNull ByteBuf buffer) {
                int size = VarInt.read(buffer);
                String[] list = new String[size];
                for (int i = 0; i < size; i++) {
                    list[i] = Utf8String.read(buffer, maxLength);
                }
                return list;
            }

            @Override
            public void encode(@NotNull ByteBuf buffer, String[] value) {
                VarInt.write(buffer, value.length);
                for (String s : value) {
                    Utf8String.write(buffer, s, maxLength);
                }
            }
        };

        public static final Type<ConfigMessage> TYPE = new Type<>(ExtraHostileNetworks.local("config"));

        public static final StreamCodec<RegistryFriendlyByteBuf, ConfigMessage> CODEC =
                new StreamCodec<>() {
                    @Override
                    public @NotNull ConfigMessage decode(RegistryFriendlyByteBuf buf) {
                        return ConfigMessageSync(
                                buf.readVarInt(), buf.readVarInt(),
                                buf.readVarInt(), buf.readVarInt(),
                                buf.readVarInt(), buf.readVarInt(),
                                buf.readVarInt(), buf.readVarInt(),

                                buf.readVarInt(), buf.readVarInt(), buf.readVarInt(),
                                buf.readVarInt(), buf.readVarInt(), buf.readVarInt(),
                                buf.readVarInt(), buf.readVarInt(), buf.readVarInt(),
                                buf.readVarInt(), buf.readVarInt(), buf.readVarInt(),

                                buf.readVarInt(), buf.readVarInt(), buf.readVarInt(),
                                buf.readVarInt(), buf.readVarInt(), buf.readVarInt(),
                                buf.readVarInt(), buf.readFloat(), buf.readVarInt(), buf.readVarInt(), buf.readUtf(),
                                buf.readUtf(), STRING_LIST.decode(buf)
                        );
                    }

                    @Override
                    public void encode(RegistryFriendlyByteBuf buf, ConfigMessage msg) {
                        buf.writeVarInt(msg.ultimateSimV1PowerCap());
                        buf.writeVarInt(msg.ultimateSimV1PowerDuration());
                        buf.writeVarInt(msg.ultimateSimV2PowerCap());
                        buf.writeVarInt(msg.ultimateSimV2PowerDuration());
                        buf.writeVarInt(msg.ultimateSimV3PowerCap());
                        buf.writeVarInt(msg.ultimateSimV3PowerDuration());
                        buf.writeVarInt(msg.ultimateSimV4PowerCap());
                        buf.writeVarInt(msg.ultimateSimV4PowerDuration());
                        buf.writeVarInt(msg.ultimateFabV1PowerCap());
                        buf.writeVarInt(msg.ultimateFabV1PowerCost());
                        buf.writeVarInt(msg.ultimateFabV1PowerDuration());
                        buf.writeVarInt(msg.ultimateFabV2PowerCap());
                        buf.writeVarInt(msg.ultimateFabV2PowerCost());
                        buf.writeVarInt(msg.ultimateFabV2PowerDuration());
                        buf.writeVarInt(msg.ultimateFabV3PowerCap());
                        buf.writeVarInt(msg.ultimateFabV3PowerCost());
                        buf.writeVarInt(msg.ultimateFabV3PowerDuration());
                        buf.writeVarInt(msg.ultimateFabV4PowerCap());
                        buf.writeVarInt(msg.ultimateFabV4PowerCost());
                        buf.writeVarInt(msg.ultimateFabV4PowerDuration());
                        buf.writeVarInt(msg.mergerCameraPowerCap());
                        buf.writeVarInt(msg.mergerCameraPowerCost());
                        buf.writeVarInt(msg.mergerCameraPowerDuration());
                        buf.writeVarInt(msg.simulationModelingPowerCap());
                        buf.writeVarInt(msg.simulationModelingPowerCost());
                        buf.writeVarInt(msg.simulationModelingPowerDuration());
                        buf.writeVarInt(msg.upgradeSpeed());
                        buf.writeFloat(msg.upgradeSpeedEnergy());
                        buf.writeVarInt(msg.upgradeModuleStackCost());
                        buf.writeVarInt(msg.upgradeDataKill());
                        buf.writeUtf(msg.minimumTierHostile());
                        buf.writeUtf(msg.minimumTierExtra());
                        STRING_LIST.encode(buf, msg.blackList());
                    }
                };

        public ConfigMessage() {
            this(ExtraHostileConfig.ultimateSimV1PowerCap, ExtraHostileConfig.ultimateSimV1PowerDuration,
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

        public static ConfigMessage ConfigMessageSync(
                int ultimateSimV1PowerCap, int ultimateSimV1PowerDuration, int ultimateSimV2PowerCap, int ultimateSimV2PowerDuration,
                int ultimateSimV3PowerCap, int ultimateSimV3PowerDuration, int ultimateSimV4PowerCap, int ultimateSimV4PowerDuration,
                int ultimateFabV1PowerCap, int ultimateFabV1PowerCost, int ultimateFabV1PowerDuration,
                int ultimateFabV2PowerCap, int ultimateFabV2PowerCost, int ultimateFabV2PowerDuration,
                int ultimateFabV3PowerCap, int ultimateFabV3PowerCost, int ultimateFabV3PowerDuration,
                int ultimateFabV4PowerCap, int ultimateFabV4PowerCost, int ultimateFabV4PowerDuration,
                int mergerCameraPowerCap, int mergerCameraPowerCost,
                int mergerCameraPowerDuration, int simulationModelingPowerCap, int simulationModelingPowerCost, int simulationModelingPowerDuration,
                int upgradeSpeed, float upgradeSpeedEnergy, int upgradeModuleStackCost, int upgradeDataKill, String minimumTierHostile,
                String minimumTierExtra, String[] blackList) {

            return new ConfigMessage(
                    ultimateSimV1PowerCap, ultimateSimV1PowerDuration,
                    ultimateSimV2PowerCap, ultimateSimV2PowerDuration,
                    ultimateSimV3PowerCap, ultimateSimV3PowerDuration,
                    ultimateSimV4PowerCap, ultimateSimV4PowerDuration,
                    ultimateFabV1PowerCap, ultimateFabV1PowerCost, ultimateFabV1PowerDuration,
                    ultimateFabV2PowerCap, ultimateFabV2PowerCost, ultimateFabV2PowerDuration,
                    ultimateFabV3PowerCap, ultimateFabV3PowerCost, ultimateFabV3PowerDuration,
                    ultimateFabV4PowerCap, ultimateFabV4PowerCost, ultimateFabV4PowerDuration,
                    mergerCameraPowerCap, mergerCameraPowerCost, mergerCameraPowerDuration,
                    simulationModelingPowerCap, simulationModelingPowerCost, simulationModelingPowerDuration,
                    upgradeSpeed, upgradeSpeedEnergy, upgradeModuleStackCost, upgradeDataKill, minimumTierHostile, minimumTierExtra, blackList);
        }

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static class Provider implements PayloadProvider<ConfigMessage> {

            @Override
            public Type<ConfigMessage> getType() {
                return TYPE;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, ConfigMessage> getCodec() {
                return CODEC;
            }

            @Override
            public void handle(ConfigMessage msg, IPayloadContext ctx) {
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
                ExtraHostileConfig.minimumTierHostile = msg.minimumTierHostile;
                ExtraHostileConfig.minimumTierExtra = msg.minimumTierExtra;
                ExtraHostileConfig.blackList = msg.blackList;
            }

            @Override
            public List<ConnectionProtocol> getSupportedProtocols() {
                return List.of(ConnectionProtocol.PLAY);
            }

            @Override
            public Optional<PacketFlow> getFlow() {
                return Optional.of(PacketFlow.CLIENTBOUND);
            }

            @Override
            public String getVersion() {
                return "1";
            }
        }
    }
}
