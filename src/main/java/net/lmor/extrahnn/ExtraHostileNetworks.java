package net.lmor.extrahnn;

import dev.shadowsoffire.placebo.config.Configuration;
import dev.shadowsoffire.placebo.network.PayloadHelper;
import dev.shadowsoffire.placebo.tabs.TabFillingRegistry;
import net.lmor.extrahnn.ExtraHostile.Items;
import net.lmor.extrahnn.ExtraHostile.Tabs;
import net.lmor.extrahnn.ExtraHostile.TileEntities;
import net.lmor.extrahnn.api.IRegTile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(ExtraHostileNetworks.MOD_ID)
public class ExtraHostileNetworks {
    public static final String MOD_ID = "extrahnn";
    public static final String VERSION = ModList.get().getModContainerById(MOD_ID).get().getModInfo().getVersion().toString();
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static Configuration cfg;

    public static ResourceLocation local(String id){
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
    }

    public ExtraHostileNetworks(IEventBus bus) {
        bus.register(this);
        cfg = ExtraHostileConfig.load();
        ExtraHostile.bootstrap(bus);
        PayloadHelper.registerPayload(new ExtraHostileConfig.ConfigMessage.Provider());
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            TabFillingRegistry.register(
                    Tabs.EXTRA_HNN_TAB.getKey(),
                    Items.ULTIMATE_LOOT_FABRICATOR_V1, Items.ULTIMATE_LOOT_FABRICATOR_V2, Items.ULTIMATE_LOOT_FABRICATOR_V3, Items.ULTIMATE_LOOT_FABRICATOR_V4,
                    Items.ULTIMATE_SIM_CHAMBER_V1, Items.ULTIMATE_SIM_CHAMBER_V2, Items.ULTIMATE_SIM_CHAMBER_V3, Items.ULTIMATE_SIM_CHAMBER_V4,
                    Items.MERGER_CAMERA,
                    Items.SIMULATOR_MODELING,
                    Items.BLANK_EXTRA_DATA_MODEL,
                    Items.UPGRADE_SPEED,
                    Items.UPGRADE_MODULE_STACK,
                    Items.UPGRADE_DATA_KILL,
                    Items.SETTING_CARD
            );
        });
    }

    @SubscribeEvent
    public void caps(RegisterCapabilitiesEvent e) {
        registerAll(e,
                TileEntities.ULTIMATE_LOOT_FABRICATOR_V1,
                TileEntities.ULTIMATE_LOOT_FABRICATOR_V2,
                TileEntities.ULTIMATE_LOOT_FABRICATOR_V3,
                TileEntities.ULTIMATE_LOOT_FABRICATOR_V4,
                TileEntities.ULTIMATE_SIM_CHAMBER_V1,
                TileEntities.ULTIMATE_SIM_CHAMBER_V2,
                TileEntities.ULTIMATE_SIM_CHAMBER_V3,
                TileEntities.ULTIMATE_SIM_CHAMBER_V4,
                TileEntities.MERGER_CAMERA,
                TileEntities.SIMULATOR_MODELING
        );
    }

    private void registerAll(RegisterCapabilitiesEvent e, BlockEntityType<?>... types) {
        for (BlockEntityType<?> type : types) {
            e.registerBlockEntity(EnergyStorage.BLOCK, type, (be, side) -> ((IRegTile) be).getEnergy());
            e.registerBlockEntity(ItemHandler.BLOCK, type, (be, side) -> ((IRegTile) be).getInventory());
        }
    }
}
