package net.lmor.extrahnn.block;

import net.lmor.extrahnn.gui.MergerCameraContainer;
import net.lmor.extrahnn.tile.MergerCameraTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.network.NetworkHooks;
import shadows.placebo.block_entity.TickingEntityBlock;

public class MergerCameraBlock extends HorizontalDirectionalBlock implements TickingEntityBlock {
    public MergerCameraBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new MergerCameraTileEntity(pPos, pState);
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            NetworkHooks.openScreen((ServerPlayer)pPlayer, this.getMenuProvider(pState, pLevel, pPos), pPos);
            return InteractionResult.CONSUME;
        }
    }

    @Deprecated
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity te = pLevel.getBlockEntity(pPos);
            if (te instanceof MergerCameraTileEntity fab) {
                Containers.dropContents(pLevel, pPos, new RecipeWrapper(fab.getInventory()));
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }

    }

    public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        return new SimpleMenuProvider((id, inv, player) -> {
            return new MergerCameraContainer(id, inv, pPos);
        }, Component.translatable(this.getDescriptionId()));
    }
}
