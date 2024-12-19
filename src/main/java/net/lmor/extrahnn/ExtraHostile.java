package net.lmor.extrahnn;

import com.google.common.collect.ImmutableSet;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntityType;
import dev.shadowsoffire.placebo.menu.MenuUtil;
import dev.shadowsoffire.placebo.registry.DeferredHelper;
import net.lmor.extrahnn.block.MergerCameraBlock;
import net.lmor.extrahnn.block.UltimateLootFabBlock;
import net.lmor.extrahnn.block.UltimateSimChamberBlock;
import net.lmor.extrahnn.gui.MergerCameraContainer;
import net.lmor.extrahnn.gui.UltimateLootFabContainer;
import net.lmor.extrahnn.gui.UltimateSimChamberContainer;
import net.lmor.extrahnn.item.BlankExtraDataModelItem;
import net.lmor.extrahnn.item.ExtraDataModelItem;
import net.lmor.extrahnn.tile.MergerCameraTileEntity;
import net.lmor.extrahnn.tile.UltimateLootFabTileEntity;
import net.lmor.extrahnn.tile.UltimateSimChamberTileEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

public class ExtraHostile {
    private static final DeferredHelper R = DeferredHelper.create(ExtraHostileNetworks.MOD_ID);
    public ExtraHostile() {
    }

    static void bootstrap() {
        ExtraHostile.Blocks.bootstrap();
        ExtraHostile.Items.bootstrap();
        ExtraHostile.TileEntities.bootstrap();
        ExtraHostile.Containers.bootstrap();
        ExtraHostile.Tabs.bootstrap();
    }

    public static class Blocks {
        public static final RegistryObject<Block> ULTIMATE_SIM_CHAMBER = ExtraHostile.R.block("ultimate_sim_chamber", () -> { return new UltimateSimChamberBlock(BlockBehaviour.Properties.of().lightLevel((s) -> { return 1; }).strength(4.0F, 3000.0F).noOcclusion()); });
        public static final RegistryObject<Block> ULTIMATE_LOOT_FABRICATOR = ExtraHostile.R.block("ultimate_loot_fabricator", () -> { return new UltimateLootFabBlock(BlockBehaviour.Properties.of().lightLevel((s) -> { return 1; }).strength(4.0F, 3000.0F).noOcclusion()); });
        public static final RegistryObject<Block> MERGER_CAMERA = ExtraHostile.R.block("merger_camera", () -> { return new MergerCameraBlock(BlockBehaviour.Properties.of().lightLevel((s) -> {  return 1; }).strength(4.0F, 3000.0F).noOcclusion()); });

        private static void bootstrap() {
        }
    }


    public static class Items {
        public static final RegistryObject<BlockItem> ULTIMATE_SIM_CHAMBER = ExtraHostile.R.item("ultimate_sim_chamber", () -> { return new BlockItem(Blocks.ULTIMATE_SIM_CHAMBER.get(), new Item.Properties()); });;
        public static final RegistryObject<BlockItem> ULTIMATE_LOOT_FABRICATOR = ExtraHostile.R.item("ultimate_loot_fabricator", () -> { return new BlockItem(Blocks.ULTIMATE_LOOT_FABRICATOR.get(), new Item.Properties()); });;
        public static final RegistryObject<BlockItem> MERGER_CAMERA = ExtraHostile.R.item("merger_camera", () -> { return new BlockItem(Blocks.MERGER_CAMERA.get(), new Item.Properties()); });;
        public static final RegistryObject<ExtraDataModelItem> EXTRA_DATA_MODEL = ExtraHostile.R.item("extra_data_model", () -> { return new ExtraDataModelItem((new Item.Properties()).stacksTo(1)); });;
        public static final RegistryObject<Item> BLANK_EXTRA_DATA_MODEL = ExtraHostile.R.item("blank_extra_data_model", () -> { return new BlankExtraDataModelItem((new Item.Properties())); });;

        private static void bootstrap() {
        }
    }

    public static class TileEntities {
        public static final RegistryObject<BlockEntityType<UltimateSimChamberTileEntity>> ULTIMATE_SIM_CHAMBER = ExtraHostile.R.blockEntity("ultimate_sim_chamber", () -> { return new TickingBlockEntityType(UltimateSimChamberTileEntity::new, ImmutableSet.of(Blocks.ULTIMATE_SIM_CHAMBER.get()), false, true); });
        public static final RegistryObject<BlockEntityType<UltimateLootFabTileEntity>> ULTIMATE_LOOT_FABRICATOR = ExtraHostile.R.blockEntity("ultimate_loot_fabricator", () -> { return new TickingBlockEntityType(UltimateLootFabTileEntity::new, ImmutableSet.of(Blocks.ULTIMATE_LOOT_FABRICATOR.get()), false, true); });
        public static final RegistryObject<BlockEntityType<MergerCameraTileEntity>> MERGER_CAMERA = ExtraHostile.R.blockEntity("merger_camera", () -> { return new TickingBlockEntityType(MergerCameraTileEntity::new, ImmutableSet.of(Blocks.MERGER_CAMERA.get()), false, true); });

        private static void bootstrap() {
        }
    }

    public static class Containers {
        public static final RegistryObject<MenuType<UltimateSimChamberContainer>> ULTIMATE_SIM_CHAMBER = ExtraHostile.R.menu("ultimate_sim_chamber", () -> { return MenuUtil.posType(UltimateSimChamberContainer::new); });
        public static final RegistryObject<MenuType<UltimateLootFabContainer>> ULTIMATE_LOOT_FABRICATOR = ExtraHostile.R.menu("ultimate_loot_fabricator", () -> { return MenuUtil.posType(UltimateLootFabContainer::new); });
        public static final RegistryObject<MenuType<MergerCameraContainer>> MERGER_CAMERA = ExtraHostile.R.menu("merger_camera", () -> { return MenuUtil.posType(MergerCameraContainer::new); });

        private static void bootstrap() {
        }
    }

    public static class Tabs {
        public static final ResourceKey<CreativeModeTab> HNN_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ExtraHostileNetworks.local("tab"));
        public static final RegistryObject<CreativeModeTab> HNN_TAB = ExtraHostile.R.tab("tab", () -> { return CreativeModeTab.builder().title(Component.translatable("itemGroup.extrahnn")).icon(() -> { return (Items.ULTIMATE_SIM_CHAMBER.get()).getDefaultInstance(); }).build(); });

        private static void bootstrap() {
        }
    }
}
