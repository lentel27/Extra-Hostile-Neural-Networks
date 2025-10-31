package net.lmor.extrahnn;

import net.lmor.extrahnn.client.ExtraDataModelItemStackRenderer;
import net.lmor.extrahnn.client.screen.MergerCameraScreen;
import net.lmor.extrahnn.client.screen.SimulationModelingScreen;
import net.lmor.extrahnn.client.screen.UltimateLootFabScreen;
import net.lmor.extrahnn.client.screen.UltimateSimChamberScreen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(value = Dist.CLIENT, modid = ExtraHostileNetworks.MOD_ID)
public class ExtraHostileClient {
    public ExtraHostileClient() {
    }

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            final ExtraDataModelItemStackRenderer renderer = new ExtraDataModelItemStackRenderer();

            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        }, ExtraHostile.Items.EXTRA_DATA_MODEL);
    }

    @SubscribeEvent
    public static void mrl(ModelEvent.RegisterAdditional e) {
        e.register(ModelResourceLocation.standalone(ExtraHostileNetworks.local("item/extra_data_model_base")));
    }

    @SubscribeEvent
    public static void screens(RegisterMenuScreensEvent e) {
        e.register(ExtraHostile.Containers.ULTIMATE_SIM_CHAMBER_V1, UltimateSimChamberScreen::new);
        e.register(ExtraHostile.Containers.ULTIMATE_SIM_CHAMBER_V2, UltimateSimChamberScreen::new);
        e.register(ExtraHostile.Containers.ULTIMATE_SIM_CHAMBER_V3, UltimateSimChamberScreen::new);
        e.register(ExtraHostile.Containers.ULTIMATE_SIM_CHAMBER_V4, UltimateSimChamberScreen::new);
        e.register(ExtraHostile.Containers.ULTIMATE_LOOT_FABRICATOR_V1, UltimateLootFabScreen::new);
        e.register(ExtraHostile.Containers.ULTIMATE_LOOT_FABRICATOR_V2, UltimateLootFabScreen::new);
        e.register(ExtraHostile.Containers.ULTIMATE_LOOT_FABRICATOR_V3, UltimateLootFabScreen::new);
        e.register(ExtraHostile.Containers.ULTIMATE_LOOT_FABRICATOR_V4, UltimateLootFabScreen::new);
        e.register(ExtraHostile.Containers.MERGER_CAMERA, MergerCameraScreen::new);
        e.register(ExtraHostile.Containers.SIMULATOR_MODELING, SimulationModelingScreen::new);
    }
}
