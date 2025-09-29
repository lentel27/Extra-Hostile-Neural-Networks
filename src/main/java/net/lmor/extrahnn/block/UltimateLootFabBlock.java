package net.lmor.extrahnn.block;

import dev.shadowsoffire.placebo.block_entity.TickingEntityBlock;
import dev.shadowsoffire.placebo.menu.MenuUtil;
import dev.shadowsoffire.placebo.menu.SimplerMenuProvider;
import net.lmor.extrahnn.api.Version;
import net.lmor.extrahnn.gui.UltimateLootFabContainer;
import net.lmor.extrahnn.tile.UltimateLootFabTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class UltimateLootFabBlock extends HorizontalDirectionalBlock implements TickingEntityBlock {
    Supplier<BlockEntityType<?>> blockType;
    Supplier<MenuType<?>> type;
    Version version;

    public UltimateLootFabBlock(BlockBehaviour.Properties props, Supplier<BlockEntityType<?>> blockType, Supplier<MenuType<?>> type, Version version) {
        super(props);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
        this.blockType = blockType;
        this.type = type;
        this.version = version;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new UltimateLootFabTileEntity(pPos, pState, blockType.get(), version);
    }

    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        return MenuUtil.openGui(player, pos, (id, pInv, posBlock) -> new UltimateLootFabContainer(id, pInv, posBlock, type.get(), this));
    }

    /** @deprecated */
    @Deprecated
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity te = pLevel.getBlockEntity(pPos);
            if (te instanceof UltimateLootFabTileEntity fab) {
                Containers.dropContents(pLevel, pPos, new RecipeWrapper(fab.getInventory()));
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }

    }

    public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        return new SimplerMenuProvider<>(pLevel, pPos, (id, pInv, posBlock) -> new UltimateLootFabContainer(id, pInv, posBlock, type.get(), this));
    }
}
