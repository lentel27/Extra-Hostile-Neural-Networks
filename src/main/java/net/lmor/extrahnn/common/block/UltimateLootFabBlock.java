package net.lmor.extrahnn.common.block;

import com.mojang.serialization.MapCodec;
import dev.shadowsoffire.placebo.block_entity.TickingEntityBlock;
import dev.shadowsoffire.placebo.menu.MenuUtil;
import dev.shadowsoffire.placebo.menu.SimplerMenuProvider;
import net.lmor.extrahnn.api.Version;
import net.lmor.extrahnn.common.container.UltimateLootFabContainer;
import net.lmor.extrahnn.common.tile.UltimateLootFabTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
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

public class UltimateLootFabBlock extends HorizontalDirectionalBlock implements TickingEntityBlock {
    Supplier<BlockEntityType<UltimateLootFabTileEntity>> blockType;
    MenuType<UltimateLootFabContainer> type;
    Version version;

    public UltimateLootFabBlock(Supplier<BlockEntityType<UltimateLootFabTileEntity>> blockType, MenuType<UltimateLootFabContainer> type, Version version) {
        super(Properties.of().lightLevel((s) -> 1).strength(4.0F, 3000.0F).noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
        this.blockType = blockType;
        this.type = type;
        this.version = version;
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new UltimateLootFabTileEntity(pos, state, blockType.get(), version);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        return MenuUtil.openGui(player, pos, (id, pInv, posBlock) -> new UltimateLootFabContainer(id, pInv, posBlock, type, this));
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return new SimplerMenuProvider<>(level, pos, (id, pInv, posBlock) -> new UltimateLootFabContainer(id, pInv, posBlock, type, this));
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
//        if (stack.getItem() instanceof FabDirectiveItem) {
//            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
//        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof UltimateLootFabTileEntity fab) {
                Containers.dropContents(level, pos, fab.getInventory().getItems());
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {return null;}

}