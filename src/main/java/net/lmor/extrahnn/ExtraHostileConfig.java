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


        if (cfg.hasChanged()) {
            cfg.save();
        }
    }

    static record ConfigMessage(int ultimateSimPowerCap, int ultimateSimPowerDuration, int ultimateFabPowerCap, int ultimateFabPowerCost, int ultimateFabPowerDuration, int mergerCameraPowerCap, int mergerCameraPowerCost, int mergerCameraPowerDuration) {
        public ConfigMessage() {
            this(ExtraHostileConfig.ultimateSimPowerCap, ExtraHostileConfig.ultimateSimPowerDuration, ExtraHostileConfig.ultimateFabPowerCap, ExtraHostileConfig.ultimateFabPowerCost, ExtraHostileConfig.ultimateFabPowerDuration, ExtraHostileConfig.mergerCameraPowerCap, ExtraHostileConfig.mergerCameraPowerCost, ExtraHostileConfig.mergerCameraPowerDuration);
        }

        ConfigMessage(int ultimateSimPowerCap, int ultimateSimPowerDuration, int ultimateFabPowerCap, int ultimateFabPowerCost, int ultimateFabPowerDuration, int mergerCameraPowerCap, int mergerCameraPowerCost, int mergerCameraPowerDuration ) {
            this.ultimateSimPowerCap = ultimateSimPowerCap;
            this.ultimateSimPowerDuration = ultimateSimPowerDuration;
            this.ultimateFabPowerCap = ultimateFabPowerCap;
            this.ultimateFabPowerCost = ultimateFabPowerCost;
            this.ultimateFabPowerDuration = ultimateFabPowerDuration;
            this.mergerCameraPowerCap = mergerCameraPowerCap;
            this.mergerCameraPowerCost = mergerCameraPowerCost;
            this.mergerCameraPowerDuration = mergerCameraPowerDuration;
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
            }

            public ConfigMessage read(FriendlyByteBuf buf) {
                return new ConfigMessage(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(),  buf.readInt(),  buf.readInt(),  buf.readInt());
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
                }, ctx);
            }

            public Optional<NetworkDirection> getNetworkDirection() {
                return Optional.of(NetworkDirection.PLAY_TO_CLIENT);
            }
        }
    }
}
