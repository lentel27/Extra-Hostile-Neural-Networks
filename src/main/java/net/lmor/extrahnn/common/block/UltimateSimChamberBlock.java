package net.lmor.extrahnn.common.block;

import com.mojang.serialization.MapCodec;
import dev.shadowsoffire.placebo.block_entity.TickingEntityBlock;
import dev.shadowsoffire.placebo.menu.MenuUtil;
import dev.shadowsoffire.placebo.menu.SimplerMenuProvider;
import net.lmor.extrahnn.api.Version;
import net.lmor.extrahnn.common.container.UltimateSimChamberContainer;
import net.lmor.extrahnn.common.tile.UltimateSimChamberTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class UltimateSimChamberBlock extends HorizontalDirectionalBlock implements TickingEntityBlock {
    Supplier<BlockEntityType<UltimateSimChamberTileEntity>> blockType;
    MenuType<UltimateSimChamberContainer> type;
    Version version;

    public UltimateSimChamberBlock(Supplier<BlockEntityType<UltimateSimChamberTileEntity>> blockType, MenuType<UltimateSimChamberContainer> type, Version version) {
        super(Properties.of().lightLevel((s) -> 1).strength(4.0F, 3000.0F).noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
        this.blockType = blockType;
        this.type = type;
        this.version = version;
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        return MenuUtil.openGui(player, pos, (id, pInv, posBlock) -> new UltimateSimChamberContainer(id, pInv, posBlock, type, this));
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return new SimplerMenuProvider<>(level, pos, (id, pInv, posBlock) -> new UltimateSimChamberContainer(id, pInv, posBlock, type, this));
    }


    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof UltimateSimChamberTileEntity sim) {
                Containers.dropContents(level, pos, sim.getInventory().getItems());
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new UltimateSimChamberTileEntity(pos, state, blockType.get(), version);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return null;
    }
}
