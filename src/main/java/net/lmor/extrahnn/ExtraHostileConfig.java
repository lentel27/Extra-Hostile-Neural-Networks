package net.lmor.extrahnn;

import dev.shadowsoffire.placebo.network.PayloadProvider;
import io.netty.buffer.ByteBuf;
import net.lmor.extrahnn.config.ExtendedConfig;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
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

    public static List<String> blackList;
    public static List<String> blackListClick;

    public static List<Integer> requiredDataModel;
    public static List<Integer> dataPerKillModel;

    public ExtraHostileConfig() {
    }

    public static ExtendedConfig load() {
        ExtendedConfig cfg = new ExtendedConfig(ExtraHostileNetworks.MOD_ID);
        cfg.setTitle("Extra Hostile Network Config");
        cfg.setComment("All entries in this config file are synced from server to client.");

        cfg.setCategoryComment("machine.version_machines.sim_chamber_v1", "Setting Ultimate Sim Chamber V1");
        ultimateSimV1PowerCap = cfg.getInt("Capacity", "machine.version_machines.sim_chamber_v1", 10000000, 1, Integer.MAX_VALUE);
        ultimateSimV1PowerDuration = cfg.getInt("Duration", "machine.version_machines.sim_chamber_v1", 250, 1, Integer.MAX_VALUE);
        
        cfg.setCategoryComment("machine.version_machines.sim_chamber_v2", "Setting Ultimate Sim Chamber V2");
        ultimateSimV2PowerCap = cfg.getInt("Capacity", "machine.version_machines.sim_chamber_v2", 50000000, 1, Integer.MAX_VALUE);
        ultimateSimV2PowerDuration = cfg.getInt("Duration", "machine.version_machines.sim_chamber_v2", 200, 1, Integer.MAX_VALUE);
        
        cfg.setCategoryComment("machine.version_machines.sim_chamber_v3", "Setting Ultimate Sim Chamber V3");
        ultimateSimV3PowerCap = cfg.getInt("Capacity", "machine.version_machines.sim_chamber_v3", 100000000, 1, Integer.MAX_VALUE);
        ultimateSimV3PowerDuration = cfg.getInt("Duration", "machine.version_machines.sim_chamber_v3", 150, 1, Integer.MAX_VALUE);
        
        cfg.setCategoryComment("machine.version_machines.sim_chamber_v4", "Setting Ultimate Sim Chamber V4");
        ultimateSimV4PowerCap = cfg.getInt("Capacity", "machine.version_machines.sim_chamber_v4", 200000000, 1, Integer.MAX_VALUE);
        ultimateSimV4PowerDuration = cfg.getInt("Duration", "machine.version_machines.sim_chamber_v4", 100, 1, Integer.MAX_VALUE);

        cfg.setCategoryComment("machine.version_machines.loot_fab_v1", "Setting Ultimate Loot Fab V1");
        ultimateFabV1PowerCap = cfg.getInt("Capacity", "machine.version_machines.loot_fab_v1", 10000000, 1, Integer.MAX_VALUE);
        ultimateFabV1PowerCost = cfg.getInt("Power Cost", "machine.version_machines.loot_fab_v1", 4096, 0, Integer.MAX_VALUE);
        ultimateFabV1PowerDuration = cfg.getInt("Duration", "machine.version_machines.loot_fab_v1", 50, 1, Integer.MAX_VALUE);

        cfg.setCategoryComment("machine.version_machines.loot_fab_v2", "Setting Ultimate Loot Fab V2");
        ultimateFabV2PowerCap = cfg.getInt("Capacity", "machine.version_machines.loot_fab_v2", 50000000, 1, Integer.MAX_VALUE);
        ultimateFabV2PowerCost = cfg.getInt("Power Cost", "machine.version_machines.loot_fab_v2", 16384, 0, Integer.MAX_VALUE);
        ultimateFabV2PowerDuration = cfg.getInt("Duration", "machine.version_machines.loot_fab_v2", 40, 1, Integer.MAX_VALUE);

        cfg.setCategoryComment("machine.version_machines.loot_fab_v3", "Setting Ultimate Loot Fab V3");
        ultimateFabV3PowerCap = cfg.getInt("Capacity", "machine.version_machines.loot_fab_v3", 100000000, 1, Integer.MAX_VALUE);
        ultimateFabV3PowerCost = cfg.getInt("Power Cost", "machine.version_machines.loot_fab_v3", 65536, 0, Integer.MAX_VALUE);
        ultimateFabV3PowerDuration = cfg.getInt("Duration", "machine.version_machines.loot_fab_v3", 30, 1, Integer.MAX_VALUE);

        cfg.setCategoryComment("machine.version_machines.loot_fab_v4", "Setting Ultimate Loot Fab V4");
        ultimateFabV4PowerCap = cfg.getInt("Capacity", "machine.version_machines.loot_fab_v4", 200000000, 1, Integer.MAX_VALUE);
        ultimateFabV4PowerCost = cfg.getInt("Power Cost", "machine.version_machines.loot_fab_v4", 262144, 0, Integer.MAX_VALUE);
        ultimateFabV4PowerDuration = cfg.getInt("Duration", "machine.version_machines.loot_fab_v4", 20, 1, Integer.MAX_VALUE);

        cfg.setCategoryComment("machine.merger_camera", "Setting Merger Camera");
        mergerCameraPowerCap = cfg.getInt("Capacity", "machine.merger_camera", 100000000, 1, Integer.MAX_VALUE);
        mergerCameraPowerCost = cfg.getInt("Power Cost", "machine.merger_camera", 25000, 0, Integer.MAX_VALUE);
        mergerCameraPowerDuration = cfg.getInt("Duration", "machine.merger_camera", 500, 1, Integer.MAX_VALUE);

        cfg.setCategoryComment("machine.sim_model", "Setting Simulation Modeling");
        simulationModelingPowerCap = cfg.getInt("Capacity", "machine.sim_model", 100000000, 1, Integer.MAX_VALUE);
        simulationModelingPowerCost = cfg.getInt("Power Cost", "machine.sim_model", 100000, 0, Integer.MAX_VALUE);
        simulationModelingPowerDuration = cfg.getInt("Duration", "machine.sim_model", 100, 1, Integer.MAX_VALUE);

        upgradeSpeed = cfg.getInt("Upgrade Speed", "upgrade", 50, 1, 100, "Acceleration of mechanisms with a speed map");
        upgradeSpeedEnergy = cfg.getFloat("Upgrade Speed Energy", "upgrade", 2.0f, 0.0f, Float.MAX_VALUE, "How many times will energy consumption increase?");

        upgradeModuleStackCost = cfg.getInt("Upgrade Module Stack Cost", "upgrade", 10000000, 0, Integer.MAX_VALUE, "How much energy is needed to move to the next tier?");

        upgradeDataKill = cfg.getInt("Upgrade Data Kill", "upgrade", 2, 0, Integer.MAX_VALUE, "How many times more data?");

        minimumTierHostile = cfg.getString("Minimum Upgrade tier - Hostile", "utils", "FAULTY", "What is the minimum entry threshold required for a model to start leveling up?\nSELF_AWARE > SUPERIOR > ADVANCED > BASIC > FAULTY");
        minimumTierExtra = cfg.getString("Minimum Upgrade tier - Extra Hostile", "utils", "AUTONOMOUS", "What is the minimum entry threshold required for a model to start leveling up?\nOMNIPOTENT > SYNTHETIC > ADAPTIVE > INTELLIGENT > AUTONOMOUS");

        blackList = cfg.getStringList("Black list for Simulation Modeling", "utils", List.of(), "Blacklist of models that cannot be improved during simulation modeling. Please note that you need to write the Hostile Network model identifier\nExample: hostilenetworks:blaze");
        blackListClick = cfg.getStringList("Blacklist for creating a model when clicking on an entity", "utils", List.of(), "Blacklist, which prevents creation of models via right-click on an entity.\nExample: entity.minecraft.blaze");

        cfg.setCategoryRequiresMcRestart("data", true);
        requiredDataModel = cfg.getIntList("Required data for extra model", "data", List.of(1500, 4500, 10750, 22375), "Required data for the extra model. The first dash is always 0, the remaining 4 are available for input. Minecraft needs to be restarted.");
        dataPerKillModel = cfg.getIntList("Data per kill for extra model", "data", List.of(20, 30, 50, 75), "Data per kill for an extra model. The last dash is always 0, the remaining 4 are available for input. Minecraft needs to be restarted.");


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
                                String minimumTierHostile, String minimumTierExtra,
                                List<String> blackList, List<String> blackListClick,
                                List<Integer> mulRequiredDataModel, List<Integer> mulDataPerKillModel
    ) implements CustomPacketPayload {

        static StreamCodec<ByteBuf, List<String>> STRING_LIST = ByteBufCodecs.stringUtf8(32767).apply(ByteBufCodecs.list());
        static StreamCodec<ByteBuf, List<Integer>> INT_LIST = ByteBufCodecs.INT.apply(ByteBufCodecs.list());

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
                                buf.readUtf(), STRING_LIST.decode(buf), STRING_LIST.decode(buf), INT_LIST.decode(buf), INT_LIST.decode(buf)
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
                        STRING_LIST.encode(buf, msg.blackListClick());
                        INT_LIST.encode(buf, msg.mulRequiredDataModel());
                        INT_LIST.encode(buf, msg.mulDataPerKillModel());

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
                    ExtraHostileConfig.simulationModelingPowerDuration, ExtraHostileConfig.upgradeSpeed, ExtraHostileConfig.upgradeSpeedEnergy, ExtraHostileConfig.upgradeModuleStackCost, ExtraHostileConfig.upgradeDataKill, ExtraHostileConfig.minimumTierHostile, ExtraHostileConfig.minimumTierExtra,
                    ExtraHostileConfig.blackList, ExtraHostileConfig.blackListClick, ExtraHostileConfig.requiredDataModel, ExtraHostileConfig.dataPerKillModel);
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
                String minimumTierExtra, List<String> blackList, List<String> blackListClick, List<Integer> mulRequiredDataModel, List<Integer> mulDataPerKillModel) {

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
                    upgradeSpeed, upgradeSpeedEnergy, upgradeModuleStackCost, upgradeDataKill, minimumTierHostile, minimumTierExtra,
                    blackList, blackListClick, mulRequiredDataModel, mulDataPerKillModel);
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
                ExtraHostileConfig.blackListClick = msg.blackListClick;

                ExtraHostileConfig.requiredDataModel = msg.mulRequiredDataModel;
                ExtraHostileConfig.dataPerKillModel = msg.mulDataPerKillModel;
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
