package net.lmor.extrahnn;

import com.google.common.collect.ImmutableSet;
import net.lmor.extrahnn.block.MergerCameraBlock;
import net.lmor.extrahnn.block.SimulatorModelingBlock;
import net.lmor.extrahnn.block.UltimateLootFabBlock;
import net.lmor.extrahnn.block.UltimateSimChamberBlock;
import net.lmor.extrahnn.gui.MergerCameraContainer;
import net.lmor.extrahnn.gui.SimulatorModelingContainer;
import net.lmor.extrahnn.gui.UltimateLootFabContainer;
import net.lmor.extrahnn.gui.UltimateSimChamberContainer;
import net.lmor.extrahnn.item.BlankExtraDataModelItem;
import net.lmor.extrahnn.item.ExtraDataModelItem;
import net.lmor.extrahnn.item.UpgradeMachine;
import net.lmor.extrahnn.tile.MergerCameraTileEntity;
import net.lmor.extrahnn.tile.SimulatorModelingTileEntity;
import net.lmor.extrahnn.tile.UltimateLootFabTileEntity;
import net.lmor.extrahnn.tile.UltimateSimChamberTileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import shadows.placebo.block_entity.TickingBlockEntityType;
import shadows.placebo.container.ContainerUtil;
import shadows.placebo.util.RegObjHelper;

public class ExtraHostile {
    private static final RegObjHelper R = new RegObjHelper(ExtraHostileNetworks.MOD_ID);
    public ExtraHostile() {
    }

    public static class Blocks {
        public static final RegistryObject<Block> ULTIMATE_SIM_CHAMBER = ExtraHostile.R.block("ULTIMATE_SIM_CHAMBER");
        public static final RegistryObject<Block> ULTIMATE_LOOT_FABRICATOR = ExtraHostile.R.block("ultimate_loot_fabricator");
        public static final RegistryObject<Block> MERGER_CAMERA = ExtraHostile.R.block("merger_camera");
        public static final RegistryObject<Block> SIMULATOR_MODELING = ExtraHostile.R.block("simulator_modeling");

        static void bootstrap() {
            IForgeRegistry<Block> reg = ForgeRegistries.BLOCKS;
            reg.register("ultimate_sim_chamber", new UltimateSimChamberBlock(BlockBehaviour.Properties.of(Material.STONE).lightLevel((s) -> {return 1;}).strength(4.0F, 3000.0F).noOcclusion()));
            reg.register("ultimate_loot_fabricator", new UltimateLootFabBlock(BlockBehaviour.Properties.of(Material.STONE).lightLevel((s) -> {return 1;}).strength(4.0F, 3000.0F).noOcclusion()));
            reg.register("merger_camera", new MergerCameraBlock(BlockBehaviour.Properties.of(Material.STONE).lightLevel((s) -> {return 1;}).strength(4.0F, 3000.0F).noOcclusion()));
            reg.register("simulator_modeling", new SimulatorModelingBlock(BlockBehaviour.Properties.of(Material.STONE).lightLevel((s) -> {return 1;}).strength(4.0F, 3000.0F).noOcclusion()));
        }
    }


    public static class Items {
        public static final RegistryObject<BlockItem> ULTIMATE_SIM_CHAMBER = ExtraHostile.R.item("ultimate_sim_chamber");
        public static final RegistryObject<BlockItem> ULTIMATE_LOOT_FABRICATOR = ExtraHostile.R.item("ultimate_loot_fabricator");
        public static final RegistryObject<BlockItem> MERGER_CAMERA = ExtraHostile.R.item("merger_camera");;
        public static final RegistryObject<BlockItem> SIMULATOR_MODELING = ExtraHostile.R.item("simulator_modeling");;
        public static final RegistryObject<ExtraDataModelItem> EXTRA_DATA_MODEL = ExtraHostile.R.item("extra_data_model");
        public static final RegistryObject<Item> BLANK_EXTRA_DATA_MODEL = ExtraHostile.R.item("blank_extra_data_model");
        public static final RegistryObject<UpgradeMachine> UPGRADE_SPEED = ExtraHostile.R.item("upgrade_speed");
        public static final RegistryObject<UpgradeMachine> UPGRADE_MODULE_STACK = ExtraHostile.R.item("upgrade_module_stack");
        public static final RegistryObject<UpgradeMachine> UPGRADE_DATA_KILL = ExtraHostile.R.item("upgrade_data_kill");

        static void bootstrap() {
            IForgeRegistry<Item> reg = ForgeRegistries.ITEMS;

            reg.register("ultimate_sim_chamber", new BlockItem(Blocks.ULTIMATE_SIM_CHAMBER.get(), (new Item.Properties()).tab(ExtraHostileNetworks.TAB)));
            reg.register("ultimate_loot_fabricator", new BlockItem(Blocks.ULTIMATE_LOOT_FABRICATOR.get(), (new Item.Properties()).tab(ExtraHostileNetworks.TAB)));
            reg.register("merger_camera", new BlockItem(Blocks.MERGER_CAMERA.get(), (new Item.Properties()).tab(ExtraHostileNetworks.TAB)));
            reg.register("simulator_modeling", new BlockItem(Blocks.SIMULATOR_MODELING.get(), (new Item.Properties()).tab(ExtraHostileNetworks.TAB)));
            reg.register("blank_extra_data_model", new BlankExtraDataModelItem((new Item.Properties()).stacksTo(1).tab(ExtraHostileNetworks.TAB).stacksTo(64)));
            reg.register("extra_data_model", new ExtraDataModelItem((new Item.Properties()).stacksTo(1)) {
                @Override
                public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> output) {
                    return;
                }
            });

            reg.register("upgrade_speed", new UpgradeMachine(new Item.Properties().tab(ExtraHostileNetworks.TAB), Component.translatable("extrahnn.info.item_tooltip.upgrade_speed", ExtraHostileConfig.upgradeSpeed, ExtraHostileConfig.upgradeSpeedEnergy ).withStyle(ChatFormatting.GRAY)));
            reg.register("upgrade_module_stack", new UpgradeMachine(new Item.Properties().tab(ExtraHostileNetworks.TAB), Component.translatable("extrahnn.info.item_tooltip.upgrade_module_stack", ExtraHostileConfig.upgradeModuleStackCost).withStyle(ChatFormatting.GRAY)));
            reg.register("upgrade_data_kill", new UpgradeMachine(new Item.Properties().tab(ExtraHostileNetworks.TAB), Component.translatable("extrahnn.info.item_tooltip.upgrade_data_kill", ExtraHostileConfig.upgradeDataKill).withStyle(ChatFormatting.GRAY)));
        }
    }

    public static class TileEntities {
        public static final RegistryObject<BlockEntityType<UltimateSimChamberTileEntity>> ULTIMATE_SIM_CHAMBER = ExtraHostile.R.blockEntity("ultimate_sim_chamber");
        public static final RegistryObject<BlockEntityType<UltimateLootFabTileEntity>> ULTIMATE_LOOT_FABRICATOR = ExtraHostile.R.blockEntity("ultimate_loot_fabricator");
        public static final RegistryObject<BlockEntityType<MergerCameraTileEntity>> MERGER_CAMERA = ExtraHostile.R.blockEntity("merger_camera");
        public static final RegistryObject<BlockEntityType<SimulatorModelingTileEntity>> SIMULATOR_MODELING = ExtraHostile.R.blockEntity("simulator_modeling");

        static void bootstrap() {
            IForgeRegistry<BlockEntityType<?>> reg = ForgeRegistries.BLOCK_ENTITY_TYPES;
            reg.register("ultimate_sim_chamber", new TickingBlockEntityType(UltimateSimChamberTileEntity::new, ImmutableSet.of(Blocks.ULTIMATE_SIM_CHAMBER.get()), false, true));
            reg.register("ultimate_loot_fabricator", new TickingBlockEntityType(UltimateLootFabTileEntity::new, ImmutableSet.of(Blocks.ULTIMATE_LOOT_FABRICATOR.get()), false, true));
            reg.register("merger_camera", new TickingBlockEntityType(MergerCameraTileEntity::new, ImmutableSet.of(Blocks.MERGER_CAMERA.get()), false, true));
            reg.register("simulator_modeling", new TickingBlockEntityType(SimulatorModelingTileEntity::new, ImmutableSet.of(Blocks.SIMULATOR_MODELING.get()), false, true));
        }
    }

    public static class Containers {
        public static final RegistryObject<MenuType<UltimateSimChamberContainer>> ULTIMATE_SIM_CHAMBER = ExtraHostile.R.menu("ultimate_sim_chamber");
        public static final RegistryObject<MenuType<UltimateLootFabContainer>> ULTIMATE_LOOT_FABRICATOR = ExtraHostile.R.menu("ultimate_loot_fabricator");
        public static final RegistryObject<MenuType<MergerCameraContainer>> MERGER_CAMERA = ExtraHostile.R.menu("merger_camera");
        public static final RegistryObject<MenuType<SimulatorModelingContainer>> SIMULATOR_MODELING = ExtraHostile.R.menu("simulator_modeling");

        static void bootstrap() {
            IForgeRegistry<MenuType<?>> reg = ForgeRegistries.MENU_TYPES;
            reg.register("ultimate_sim_chamber", ContainerUtil.makeType(UltimateSimChamberContainer::new));
            reg.register("ultimate_loot_fabricator", ContainerUtil.makeType(UltimateLootFabContainer::new));
            reg.register("merger_camera", ContainerUtil.makeType(MergerCameraContainer::new));
            reg.register("simulator_modeling", ContainerUtil.makeType(SimulatorModelingContainer::new));
        }
    }
}
