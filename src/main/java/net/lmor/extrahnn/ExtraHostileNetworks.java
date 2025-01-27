package net.lmor.extrahnn;

import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import net.lmor.extrahnn.ExtraHostile.*;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import shadows.placebo.loot.LootSystem;

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

    static final CreativeModeTab TAB = new CreativeModeTab(ExtraHostileNetworks.MOD_ID) {
        public ItemStack makeIcon() {
            return new ItemStack(Blocks.ULTIMATE_SIM_CHAMBER.get());
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> p_40778_) {
            for(Item item : Registry.ITEM) {
                if (item != Items.EXTRA_DATA_MODEL.get()) item.fillItemCategory(this, p_40778_);
            }
        }
    };

    public ExtraHostileNetworks() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        ExtraHostileConfig.load();
    }

    @SubscribeEvent
    public void register(RegisterEvent e) {
        if (e.getForgeRegistry() == (Object) ForgeRegistries.BLOCKS) {
            ExtraHostile.Blocks.bootstrap();
        }

        if (e.getForgeRegistry() == (Object) ForgeRegistries.BLOCK_ENTITY_TYPES) {
            ExtraHostile.TileEntities.bootstrap();
        }

        if (e.getForgeRegistry() == (Object) ForgeRegistries.ITEMS) {
            ExtraHostile.Items.bootstrap();
        }

        if (e.getForgeRegistry() == (Object) ForgeRegistries.MENU_TYPES) {
            ExtraHostile.Containers.bootstrap();
        }

    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            LootSystem.defaultBlockTable(Blocks.ULTIMATE_LOOT_FABRICATOR.get());
            LootSystem.defaultBlockTable(Blocks.ULTIMATE_SIM_CHAMBER.get());
            LootSystem.defaultBlockTable(Blocks.MERGER_CAMERA.get());
            LootSystem.defaultBlockTable(Blocks.SIMULATOR_MODELING.get());
        });
    }

    public static ResourceLocation local(String id){
        return new ResourceLocation(MOD_ID, id);
    }

}
