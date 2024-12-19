package net.lmor.extrahnn;


import dev.shadowsoffire.placebo.network.PacketDistro;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
        modid = "extrahnn"
)
public class ExtraHostileEvents {

    public ExtraHostileEvents() {
    }

    @SubscribeEvent
    public static void sync(OnDatapackSyncEvent e) {
        if (e.getPlayer() != null) {
            PacketDistro.sendTo(ExtraHostileNetworks.CHANNEL, new ExtraHostileConfig.ConfigMessage(), e.getPlayer());
        } else {
            PacketDistro.sendToAll(ExtraHostileNetworks.CHANNEL, new ExtraHostileConfig.ConfigMessage());
        }

    }
}
