package net.lmor.extrahnn;

import net.lmor.extrahnn.gui.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

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
            MenuScreens.register(ExtraHostile.Containers.SIMULATOR_MODELING.get(), SimulationModelingScreen::new);
        });
    }

    @SubscribeEvent
    public static void mrl(ModelEvent.RegisterAdditional e) {
        e.register(ExtraHostileNetworks.local("item/extra_data_model_base"));
    }
}
