package net.lmor.extrahnn;


import net.lmor.extrahnn.ExtraHostileConfig.ConfigMessage;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ExtraHostileNetworks.MOD_ID)
public class ExtraHostileEvents {

    public ExtraHostileEvents() {}

    @SubscribeEvent
    private static void onServerSetup(ServerAboutToStartEvent event) {
        EHNNUtils.initTiers();
    }

    @SubscribeEvent
    public static void reload(AddReloadListenerEvent e) {
        e.addListener((ResourceManagerReloadListener) resman -> ExtraHostileNetworks.cfg = ExtraHostileConfig.load());
        EHNNUtils.initTiers();
    }

    @SubscribeEvent
    public static void sync(OnDatapackSyncEvent e) {
        ConfigMessage msg = new ConfigMessage();
        e.getRelevantPlayers().forEach(p -> PacketDistributor.sendToPlayer(p, msg));
    }
}
