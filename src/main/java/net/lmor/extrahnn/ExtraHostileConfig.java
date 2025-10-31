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

    public static String minimumTierHostile;
    public static String minimumTierExtra;

    public static String[] blackList;

    public ExtraHostileConfig() {
    }

    public static Configuration load() {
        Configuration cfg = new Configuration(ExtraHostileNetworks.MOD_ID);
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

        minimumTierHostile = cfg.getString("Minimum Upgrade tier - Hostile", "utils", "FAULTY", "What is the minimum entry threshold required for a model to start leveling up?\nSELF_AWARE > SUPERIOR > ADVANCED > BASIC > FAULTY");
        minimumTierExtra = cfg.getString("Minimum Upgrade tier - Extra Hostile", "utils", "AUTONOMOUS", "What is the minimum entry threshold required for a model to start leveling up?\nOMNIPOTENT > SYNTHETIC > ADAPTIVE > INTELLIGENT > AUTONOMOUS");

        blackList = cfg.getStringList("Black list for Simulation Modeling", "utils", new String[0], "Blacklist of models that cannot be improved during simulation modeling. Please note that you need to write the Hostile Network model identifier, for example: hostilenetworks:blaze etc.");

        if (cfg.hasChanged()) cfg.save();
        return cfg;
    }

    public record ConfigMessage(int ultimateSimPowerCap, int ultimateSimPowerDuration, int ultimateFabPowerCap, int ultimateFabPowerCost, int ultimateFabPowerDuration, int mergerCameraPowerCap,
                                int mergerCameraPowerCost, int mergerCameraPowerDuration, int simulationModelingPowerCap, int simulationModelingPowerCost, int simulationModelingPowerDuration,
                                int upgradeSpeed, float upgradeSpeedEnergy, int upgradeModuleStackCost, int upgradeDataKill, String minimumTierHostile, String minimumTierExtra, String[] blackList) implements CustomPacketPayload {

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
                                buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(),
                                buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(),
                                buf.readVarInt(), buf.readFloat(), buf.readVarInt(), buf.readVarInt(), buf.readUtf(), buf.readUtf(), STRING_LIST.decode(buf)
                        );
                    }

                    @Override
                    public void encode(RegistryFriendlyByteBuf buf, ConfigMessage msg) {
                        buf.writeVarInt(msg.ultimateSimPowerCap());
                        buf.writeVarInt(msg.ultimateSimPowerDuration());
                        buf.writeVarInt(msg.ultimateFabPowerCap());
                        buf.writeVarInt(msg.ultimateFabPowerCost());
                        buf.writeVarInt(msg.ultimateFabPowerDuration());
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
            this(ExtraHostileConfig.ultimateSimPowerCap, ExtraHostileConfig.ultimateSimPowerDuration, ExtraHostileConfig.ultimateFabPowerCap, ExtraHostileConfig.ultimateFabPowerCost, ExtraHostileConfig.ultimateFabPowerDuration,
                    ExtraHostileConfig.mergerCameraPowerCap, ExtraHostileConfig.mergerCameraPowerCost, ExtraHostileConfig.mergerCameraPowerDuration, ExtraHostileConfig.simulationModelingPowerCap, ExtraHostileConfig.simulationModelingPowerCost,
                    ExtraHostileConfig.simulationModelingPowerDuration, ExtraHostileConfig.upgradeSpeed, ExtraHostileConfig.upgradeSpeedEnergy, ExtraHostileConfig.upgradeModuleStackCost, ExtraHostileConfig.upgradeDataKill, ExtraHostileConfig.minimumTierHostile, ExtraHostileConfig.minimumTierExtra, ExtraHostileConfig.blackList);
        }

        public static ConfigMessage ConfigMessageSync(int ultimateSimPowerCap, int ultimateSimPowerDuration, int ultimateFabPowerCap, int ultimateFabPowerCost, int ultimateFabPowerDuration, int mergerCameraPowerCap,
                                                      int mergerCameraPowerCost, int mergerCameraPowerDuration, int simulationModelingPowerCap, int simulationModelingPowerCost, int simulationModelingPowerDuration,
                                                      int upgradeSpeed, float upgradeSpeedEnergy, int upgradeModuleStackCost, int upgradeDataKill, String minimumTierHostile, String minimumTierExtra, String[] blackList) {
            return new ConfigMessage(ultimateSimPowerCap, ultimateSimPowerDuration, ultimateFabPowerCap, ultimateFabPowerCost, ultimateFabPowerDuration, mergerCameraPowerCap,
                    mergerCameraPowerCost, mergerCameraPowerDuration, simulationModelingPowerCap, simulationModelingPowerCost, simulationModelingPowerDuration,
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
