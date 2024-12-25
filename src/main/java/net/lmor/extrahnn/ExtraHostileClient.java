package net.lmor.extrahnn;

import net.lmor.extrahnn.gui.MergerCameraScreen;
import net.lmor.extrahnn.gui.UltimateLootFabScreen;
import net.lmor.extrahnn.gui.UltimateSimChamberScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import shadows.hostilenetworks.HostileClient;

@Mod.EventBusSubscriber(
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = {Dist.CLIENT},
        modid = ExtraHostileNetworks.MOD_ID
)
public class ExtraHostileClient {
    public ExtraHostileClient() {
    }

    @SubscribeEvent
    public static void init(FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            MenuScreens.register(ExtraHostile.Containers.ULTIMATE_SIM_CHAMBER.get(), UltimateSimChamberScreen::new);
            MenuScreens.register(ExtraHostile.Containers.ULTIMATE_LOOT_FABRICATOR.get(), UltimateLootFabScreen::new);
            MenuScreens.register(ExtraHostile.Containers.MERGER_CAMERA.get(), MergerCameraScreen::new);
        });
        MinecraftForge.EVENT_BUS.addListener(HostileClient::tick);
    }

    @SubscribeEvent
    public static void mrl(ModelEvent.RegisterAdditional e) {
        e.register(ExtraHostileNetworks.local("item/extra_data_model_base"));
    }
}
