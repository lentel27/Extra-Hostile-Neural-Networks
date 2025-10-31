package net.lmor.extrahnn;

import com.mojang.serialization.Codec;
import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.data.DataModelRegistry;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntityType.TickSide;
import dev.shadowsoffire.placebo.registry.DeferredHelper;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.lmor.extrahnn.api.Version;
import net.lmor.extrahnn.common.block.MergerCameraBlock;
import net.lmor.extrahnn.common.block.SimulationModelingBlock;
import net.lmor.extrahnn.common.block.UltimateLootFabBlock;
import net.lmor.extrahnn.common.block.UltimateSimChamberBlock;
import net.lmor.extrahnn.common.container.MergerCameraContainer;
import net.lmor.extrahnn.common.container.SimulationModelingContainer;
import net.lmor.extrahnn.common.container.UltimateLootFabContainer;
import net.lmor.extrahnn.common.container.UltimateSimChamberContainer;
import net.lmor.extrahnn.common.item.BlankExtraDataModelItem;
import net.lmor.extrahnn.common.item.ExtraDataModelItem;
import net.lmor.extrahnn.common.item.SettingCard;
import net.lmor.extrahnn.common.item.UpgradeMachine;
import net.lmor.extrahnn.common.tile.MergerCameraTileEntity;
import net.lmor.extrahnn.common.tile.SimulationModelingTileEntity;
import net.lmor.extrahnn.common.tile.UltimateLootFabTileEntity;
import net.lmor.extrahnn.common.tile.UltimateSimChamberTileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExtraHostile {

    private static final DeferredHelper R = DeferredHelper.create(ExtraHostileNetworks.MOD_ID);

    static void bootstrap(IEventBus bus) {
        bus.register(R);
        Blocks.bootstrap();
        Items.bootstrap();
        TileEntities.bootstrap();
        Containers.bootstrap();
        Tabs.bootstrap();
        Components.bootstrap();
    }

    public static class Blocks {
        public static final Holder<Block> ULTIMATE_SIM_CHAMBER_V1 = R.block("ultimate_sim_chamber_v1", () -> new UltimateSimChamberBlock(() -> TileEntities.ULTIMATE_SIM_CHAMBER_V1, Containers.ULTIMATE_SIM_CHAMBER_V1, Version.V1));
        public static final Holder<Block> ULTIMATE_SIM_CHAMBER_V2 = R.block("ultimate_sim_chamber_v2", () -> new UltimateSimChamberBlock(() -> TileEntities.ULTIMATE_SIM_CHAMBER_V2, Containers.ULTIMATE_SIM_CHAMBER_V2, Version.V2));
        public static final Holder<Block> ULTIMATE_SIM_CHAMBER_V3 = R.block("ultimate_sim_chamber_v3", () -> new UltimateSimChamberBlock(() -> TileEntities.ULTIMATE_SIM_CHAMBER_V3, Containers.ULTIMATE_SIM_CHAMBER_V3, Version.V3));
        public static final Holder<Block> ULTIMATE_SIM_CHAMBER_V4 = R.block("ultimate_sim_chamber_v4", () -> new UltimateSimChamberBlock(() -> TileEntities.ULTIMATE_SIM_CHAMBER_V4, Containers.ULTIMATE_SIM_CHAMBER_V4, Version.V4));
        public static final Holder<Block> ULTIMATE_LOOT_FABRICATOR_V1 = R.block("ultimate_loot_fabricator_v1", () -> new UltimateLootFabBlock(() -> TileEntities.ULTIMATE_LOOT_FABRICATOR_V1, Containers.ULTIMATE_LOOT_FABRICATOR_V1, Version.V1));
        public static final Holder<Block> ULTIMATE_LOOT_FABRICATOR_V2 = R.block("ultimate_loot_fabricator_v2", () -> new UltimateLootFabBlock(() -> TileEntities.ULTIMATE_LOOT_FABRICATOR_V2, Containers.ULTIMATE_LOOT_FABRICATOR_V2, Version.V2));
        public static final Holder<Block> ULTIMATE_LOOT_FABRICATOR_V3 = R.block("ultimate_loot_fabricator_v3", () -> new UltimateLootFabBlock(() -> TileEntities.ULTIMATE_LOOT_FABRICATOR_V3, Containers.ULTIMATE_LOOT_FABRICATOR_V3, Version.V3));
        public static final Holder<Block> ULTIMATE_LOOT_FABRICATOR_V4 = R.block("ultimate_loot_fabricator_v4", () -> new UltimateLootFabBlock(() -> TileEntities.ULTIMATE_LOOT_FABRICATOR_V4, Containers.ULTIMATE_LOOT_FABRICATOR_V4, Version.V4));
        public static final Holder<Block> MERGER_CAMERA = R.block("merger_camera", MergerCameraBlock::new);
        public static final Holder<Block> SIMULATOR_MODELING = R.block("simulator_modeling", SimulationModelingBlock::new);

        private static void bootstrap() {}
    }

    public static class Items {
        public static final Holder<Item> ULTIMATE_SIM_CHAMBER_V1 = R.blockItem("ultimate_sim_chamber_v1", Blocks.ULTIMATE_SIM_CHAMBER_V1);
        public static final Holder<Item> ULTIMATE_SIM_CHAMBER_V2 = R.blockItem("ultimate_sim_chamber_v2", Blocks.ULTIMATE_SIM_CHAMBER_V2);
        public static final Holder<Item> ULTIMATE_SIM_CHAMBER_V3 = R.blockItem("ultimate_sim_chamber_v3", Blocks.ULTIMATE_SIM_CHAMBER_V3);
        public static final Holder<Item> ULTIMATE_SIM_CHAMBER_V4 = R.blockItem("ultimate_sim_chamber_v4", Blocks.ULTIMATE_SIM_CHAMBER_V4);
        public static final Holder<Item> ULTIMATE_LOOT_FABRICATOR_V1 = R.blockItem("ultimate_loot_fabricator_v1", Blocks.ULTIMATE_LOOT_FABRICATOR_V1);
        public static final Holder<Item> ULTIMATE_LOOT_FABRICATOR_V2 = R.blockItem("ultimate_loot_fabricator_v2", Blocks.ULTIMATE_LOOT_FABRICATOR_V2);
        public static final Holder<Item> ULTIMATE_LOOT_FABRICATOR_V3 = R.blockItem("ultimate_loot_fabricator_v3", Blocks.ULTIMATE_LOOT_FABRICATOR_V3);
        public static final Holder<Item> ULTIMATE_LOOT_FABRICATOR_V4 = R.blockItem("ultimate_loot_fabricator_v4", Blocks.ULTIMATE_LOOT_FABRICATOR_V4);
        public static final Holder<Item> MERGER_CAMERA = R.blockItem("merger_camera", Blocks.MERGER_CAMERA);
        public static final Holder<Item> SIMULATOR_MODELING = R.blockItem("simulator_modeling", Blocks.SIMULATOR_MODELING);
        public static final Holder<Item> EXTRA_DATA_MODEL = R.item("extra_data_model", ExtraDataModelItem::new, p -> p.stacksTo(1));
        public static final Holder<Item> BLANK_EXTRA_DATA_MODEL = R.item("blank_extra_data_model", BlankExtraDataModelItem::new);
        public static final Holder<Item> SETTING_CARD = R.item("setting_card", SettingCard::new);
        public static final Holder<Item> UPGRADE_SPEED = R.item("upgrade_speed", () -> new UpgradeMachine(Component.translatable("extrahnn.info.item_tooltip.upgrade_speed", ExtraHostileConfig.upgradeSpeed, ExtraHostileConfig.upgradeSpeedEnergy ).withStyle(ChatFormatting.GRAY)));
        public static final Holder<Item> UPGRADE_MODULE_STACK = R.item("upgrade_module_stack", () -> new UpgradeMachine(Component.translatable("extrahnn.info.item_tooltip.upgrade_module_stack", ExtraHostileConfig.upgradeModuleStackCost).withStyle(ChatFormatting.GRAY)));
        public static final Holder<Item> UPGRADE_DATA_KILL = R.item("upgrade_data_kill", () -> new UpgradeMachine(Component.translatable("extrahnn.info.item_tooltip.upgrade_data_kill", ExtraHostileConfig.upgradeDataKill).withStyle(ChatFormatting.GRAY)));

        private static void bootstrap() {}
    }

    public static class TileEntities {
        public static final BlockEntityType<UltimateSimChamberTileEntity> ULTIMATE_SIM_CHAMBER_V1 = R.tickingBlockEntity("ultimate_sim_chamber_v1", (p, s) -> new UltimateSimChamberTileEntity(p, s, TileEntities.ULTIMATE_SIM_CHAMBER_V1, Version.V1), TickSide.SERVER, Blocks.ULTIMATE_SIM_CHAMBER_V1);
        public static final BlockEntityType<UltimateSimChamberTileEntity> ULTIMATE_SIM_CHAMBER_V2 = R.tickingBlockEntity("ultimate_sim_chamber_v2", (p, s) -> new UltimateSimChamberTileEntity(p, s, TileEntities.ULTIMATE_SIM_CHAMBER_V2, Version.V2), TickSide.SERVER, Blocks.ULTIMATE_SIM_CHAMBER_V2);
        public static final BlockEntityType<UltimateSimChamberTileEntity> ULTIMATE_SIM_CHAMBER_V3 = R.tickingBlockEntity("ultimate_sim_chamber_v3", (p, s) -> new UltimateSimChamberTileEntity(p, s, TileEntities.ULTIMATE_SIM_CHAMBER_V3, Version.V3), TickSide.SERVER, Blocks.ULTIMATE_SIM_CHAMBER_V3);
        public static final BlockEntityType<UltimateSimChamberTileEntity> ULTIMATE_SIM_CHAMBER_V4 = R.tickingBlockEntity("ultimate_sim_chamber_v4", (p, s) -> new UltimateSimChamberTileEntity(p, s, TileEntities.ULTIMATE_SIM_CHAMBER_V4, Version.V4), TickSide.SERVER, Blocks.ULTIMATE_SIM_CHAMBER_V4);
        public static final BlockEntityType<UltimateLootFabTileEntity> ULTIMATE_LOOT_FABRICATOR_V1 = R.tickingBlockEntity("ultimate_loot_fabricator_v1", (p, s) -> new UltimateLootFabTileEntity(p, s, TileEntities.ULTIMATE_LOOT_FABRICATOR_V1, Version.V1), TickSide.SERVER, Blocks.ULTIMATE_LOOT_FABRICATOR_V1);
        public static final BlockEntityType<UltimateLootFabTileEntity> ULTIMATE_LOOT_FABRICATOR_V2 = R.tickingBlockEntity("ultimate_loot_fabricator_v2", (p, s) -> new UltimateLootFabTileEntity(p, s, TileEntities.ULTIMATE_LOOT_FABRICATOR_V2, Version.V2), TickSide.SERVER, Blocks.ULTIMATE_LOOT_FABRICATOR_V2);
        public static final BlockEntityType<UltimateLootFabTileEntity> ULTIMATE_LOOT_FABRICATOR_V3 = R.tickingBlockEntity("ultimate_loot_fabricator_v3", (p, s) -> new UltimateLootFabTileEntity(p, s, TileEntities.ULTIMATE_LOOT_FABRICATOR_V3, Version.V3), TickSide.SERVER, Blocks.ULTIMATE_LOOT_FABRICATOR_V3);
        public static final BlockEntityType<UltimateLootFabTileEntity> ULTIMATE_LOOT_FABRICATOR_V4 = R.tickingBlockEntity("ultimate_loot_fabricator_v4", (p, s) -> new UltimateLootFabTileEntity(p, s, TileEntities.ULTIMATE_LOOT_FABRICATOR_V4, Version.V4), TickSide.SERVER, Blocks.ULTIMATE_LOOT_FABRICATOR_V4);
        public static final BlockEntityType<MergerCameraTileEntity> MERGER_CAMERA = R.tickingBlockEntity("merger_camera", MergerCameraTileEntity::new, TickSide.SERVER, Blocks.MERGER_CAMERA);
        public static final BlockEntityType<SimulationModelingTileEntity> SIMULATOR_MODELING = R.tickingBlockEntity("simulator_modeling", SimulationModelingTileEntity::new, TickSide.SERVER, Blocks.SIMULATOR_MODELING);


        private static void bootstrap() {}
    }

    public static class Containers {
        public static final MenuType<UltimateSimChamberContainer> ULTIMATE_SIM_CHAMBER_V1 = R.menuWithPos("ultimate_sim_chamber_v1", (id, pInv, pos) -> new UltimateSimChamberContainer(id, pInv, pos, Containers.ULTIMATE_SIM_CHAMBER_V1, Blocks.ULTIMATE_SIM_CHAMBER_V1.value()) );
        public static final MenuType<UltimateSimChamberContainer> ULTIMATE_SIM_CHAMBER_V2 = R.menuWithPos("ultimate_sim_chamber_v2", (id, pInv, pos) -> new UltimateSimChamberContainer(id, pInv, pos, Containers.ULTIMATE_SIM_CHAMBER_V2, Blocks.ULTIMATE_SIM_CHAMBER_V2.value()) );
        public static final MenuType<UltimateSimChamberContainer> ULTIMATE_SIM_CHAMBER_V3 = R.menuWithPos("ultimate_sim_chamber_v3", (id, pInv, pos) -> new UltimateSimChamberContainer(id, pInv, pos, Containers.ULTIMATE_SIM_CHAMBER_V3, Blocks.ULTIMATE_SIM_CHAMBER_V3.value()) );
        public static final MenuType<UltimateSimChamberContainer> ULTIMATE_SIM_CHAMBER_V4 = R.menuWithPos("ultimate_sim_chamber_v4", (id, pInv, pos) -> new UltimateSimChamberContainer(id, pInv, pos, Containers.ULTIMATE_SIM_CHAMBER_V4, Blocks.ULTIMATE_SIM_CHAMBER_V4.value()) );
        public static final MenuType<UltimateLootFabContainer> ULTIMATE_LOOT_FABRICATOR_V1 = R.menuWithPos("ultimate_loot_fabricator_v1", (id, pInv, pos) -> new UltimateLootFabContainer(id, pInv, pos, Containers.ULTIMATE_LOOT_FABRICATOR_V1, Blocks.ULTIMATE_LOOT_FABRICATOR_V1.value()) );
        public static final MenuType<UltimateLootFabContainer> ULTIMATE_LOOT_FABRICATOR_V2 = R.menuWithPos("ultimate_loot_fabricator_v2", (id, pInv, pos) -> new UltimateLootFabContainer(id, pInv, pos, Containers.ULTIMATE_LOOT_FABRICATOR_V2, Blocks.ULTIMATE_LOOT_FABRICATOR_V2.value()) );
        public static final MenuType<UltimateLootFabContainer> ULTIMATE_LOOT_FABRICATOR_V3 = R.menuWithPos("ultimate_loot_fabricator_v3", (id, pInv, pos) -> new UltimateLootFabContainer(id, pInv, pos, Containers.ULTIMATE_LOOT_FABRICATOR_V3, Blocks.ULTIMATE_LOOT_FABRICATOR_V3.value()) );
        public static final MenuType<UltimateLootFabContainer> ULTIMATE_LOOT_FABRICATOR_V4 = R.menuWithPos("ultimate_loot_fabricator_v4", (id, pInv, pos) -> new UltimateLootFabContainer(id, pInv, pos, Containers.ULTIMATE_LOOT_FABRICATOR_V4, Blocks.ULTIMATE_LOOT_FABRICATOR_V4.value()) );
        public static final MenuType<MergerCameraContainer> MERGER_CAMERA = R.menuWithPos("merger_camera", MergerCameraContainer::new);
        public static final MenuType<SimulationModelingContainer> SIMULATOR_MODELING = R.menuWithPos("simulator_modeling", SimulationModelingContainer::new);

        private static void bootstrap() {}
    }

    public static class Tabs {
        public static final Holder<CreativeModeTab> EXTRA_HNN_TAB = R.creativeTab("tab", b -> b.title(Component.translatable("itemGroup.extrahnn")).icon(() -> Items.ULTIMATE_SIM_CHAMBER_V1.value().getDefaultInstance()));

        private static void bootstrap() {}
    }

    public static class Components {
        public static final DataComponentType<List<DynamicHolder<DataModel>>> EXTRA_DATA_MODEL =
                R.component("extra_data_model", b -> b
                        .persistent(DataModelRegistry.INSTANCE.holderCodec().listOf())
                        .networkSynchronized(new StreamCodec<>() {
                            @Override
                            public @NotNull List<DynamicHolder<DataModel>> decode(@NotNull RegistryFriendlyByteBuf buf) {
                                int size = buf.readVarInt();
                                List<DynamicHolder<DataModel>> list = new ArrayList<>(size);
                                for (int i = 0; i < size; i++) {
                                    list.add(DataModelRegistry.INSTANCE.holderStreamCodec().decode(buf));
                                }
                                return list;
                            }

                            @Override
                            public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull List<DynamicHolder<DataModel>> list) {
                                buf.writeVarInt(list.size());
                                for (DynamicHolder<DataModel> holder : list) {
                                    DataModelRegistry.INSTANCE.holderStreamCodec().encode(buf, holder);
                                }
                            }
                        })
                );

        public static final DataComponentType<CompoundTag> SETTING_CARD = R.component("setting_card", b -> b
                .persistent(CompoundTag.CODEC)
                .networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
        );

        private static void bootstrap() {}
    }
}
