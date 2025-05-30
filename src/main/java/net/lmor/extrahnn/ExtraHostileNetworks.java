package net.lmor.extrahnn;

import dev.shadowsoffire.placebo.loot.LootSystem;
import dev.shadowsoffire.placebo.network.MessageHelper;
import dev.shadowsoffire.placebo.tabs.TabFillingRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import net.lmor.extrahnn.ExtraHostile.*;

@Mod(ExtraHostileNetworks.MOD_ID)
public class ExtraHostileNetworks {
    public static final String MOD_ID = "extrahnn";
    public static final String VERSION = (ModList.get().getModContainerById(MOD_ID).get()).getModInfo().getVersion().toString();
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(local("extrahnn")).clientAcceptedVersions((s) -> {
        return true;
    }).serverAcceptedVersions((s) -> {
        return true;
    }).networkProtocolVersion(() -> {
        return "1.0.0";
    }).simpleChannel();
    public static final ResourceKey<CreativeModeTab> TAB;

    public ExtraHostileNetworks() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        ExtraHostileConfig.load();
        ExtraHostile.bootstrap();
        MessageHelper.registerMessage(CHANNEL, 0, new ExtraHostileConfig.ConfigMessage.Provider());
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            LootSystem.defaultBlockTable(Blocks.ULTIMATE_LOOT_FABRICATOR.get());
            LootSystem.defaultBlockTable(Blocks.ULTIMATE_SIM_CHAMBER.get());
            LootSystem.defaultBlockTable(Blocks.MERGER_CAMERA.get());
            LootSystem.defaultBlockTable(Blocks.SIMULATOR_MODELING.get());
            TabFillingRegistry.register(Tabs.HNN_TAB_KEY, Items.ULTIMATE_LOOT_FABRICATOR, Items.ULTIMATE_SIM_CHAMBER,
                    Items.MERGER_CAMERA, Items.BLANK_EXTRA_DATA_MODEL, Items.SIMULATOR_MODELING, Items.UPGRADE_SPEED,
                    Items.UPGRADE_MODULE_STACK, Items.UPGRADE_DATA_KILL, Items.SETTING_CARD);
        });
    }

    public static ResourceLocation local(String id){
        return new ResourceLocation(MOD_ID, id);
    }

    static {
        TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, local("tab"));
    }
}
