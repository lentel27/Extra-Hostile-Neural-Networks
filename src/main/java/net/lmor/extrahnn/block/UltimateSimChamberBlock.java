package net.lmor.extrahnn.block;

import dev.shadowsoffire.placebo.block_entity.TickingEntityBlock;
import dev.shadowsoffire.placebo.menu.MenuUtil;
import net.lmor.extrahnn.api.SettingCardMessage;
import net.lmor.extrahnn.gui.UltimateSimChamberContainer;
import net.lmor.extrahnn.item.SettingCard;
import net.lmor.extrahnn.tile.UltimateSimChamberTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

public class UltimateSimChamberBlock extends HorizontalDirectionalBlock implements TickingEntityBlock {
    public UltimateSimChamberBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        return MenuUtil.openGui(player, pos, UltimateSimChamberContainer::new);
    }

    /** @deprecated */
    @Deprecated
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity te = pLevel.getBlockEntity(pPos);
            if (te instanceof UltimateSimChamberTileEntity sim) {
                Containers.dropContents(pLevel, pPos, new RecipeWrapper(sim.getInventory()));
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }

    }

    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new UltimateSimChamberTileEntity(pPos, pState);
    }
}
