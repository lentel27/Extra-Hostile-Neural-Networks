package net.lmor.extrahnn.common.container;

import dev.shadowsoffire.hostilenetworks.tile.SimChamberTileEntity.RedstoneState;
import dev.shadowsoffire.placebo.menu.BlockEntityMenu;
import dev.shadowsoffire.placebo.menu.FilteredSlot;
import net.lmor.extrahnn.common.item.ExtraDataModelItem;
import net.lmor.extrahnn.common.tile.UltimateSimChamberTileEntity;
import net.lmor.extrahnn.common.tile.UltimateSimChamberTileEntity.FailureState;
import net.lmor.extrahnn.common.tile.UltimateSimChamberTileEntity.SimItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class UltimateSimChamberContainer extends BlockEntityMenu<UltimateSimChamberTileEntity> {
    private final Block block;

    public UltimateSimChamberContainer(int id, Inventory inv, BlockPos pos, MenuType<?> type, Block block) {
        super(type, id, inv, pos);
        this.block = block;

        SimItemHandler inventory = this.tile.getInventory();
        this.addSlot(new FilteredSlot(inventory, 0, -13, 1, s -> s.getItem() instanceof ExtraDataModelItem));
        this.addSlot(new FilteredSlot(inventory, 1, 200, 48, s -> ExtraDataModelItem.matchesModelInput(this.getSlot(0).getItem(), s)));

        int[][] lockedSlots = {
                {2, 158, 6}, {3, 176, 6},
                {4, 158, 24}, {5, 176, 24},
                {6, 200, 6}, {7, 200, 24},
                {8, 158, 48}, {9, 176, 48}
        };
        for (int[] slot : lockedSlots) {
            addLockedSlot(inventory, slot[0], slot[1], slot[2]);
        }

        this.addPlayerSlots(inv, 36, 175);
        this.mover.registerRule((stack, slot) -> slot < 10, 10, this.slots.size());
        this.mover.registerRule((stack, slot) -> stack.getItem() instanceof ExtraDataModelItem, 0, 1);
        this.mover.registerRule((stack, slot) -> ExtraDataModelItem.matchesModelInput(this.getSlot(0).getItem(), stack), 1, 2);
        this.registerInvShuffleRules();
    }

    private void addLockedSlot(SimItemHandler inv, int index, int x, int y) {
        this.addSlot(new FilteredSlot(inv, index, x, y, s -> false));
    }

    @Override
    public boolean stillValid(Player player) {
        return player.level().getBlockState(this.pos).is(block);
    }

    public int getEnergyStored() {
        return this.tile.getEnergyStored();
    }

    public int getRuntime() {
        return this.tile.getRuntime();
    }

    public boolean isPredictionSucceed() {
        return this.tile.isPredictionSucceed();
    }

    public FailureState getFailState() {
        return this.tile.getFailState();
    }

    public void setRedstoneState(RedstoneState state) {
        this.tile.setRedstoneState(state);
    }

    public RedstoneState getRedstoneState() {
        return this.tile.getRedstoneState();
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int id) {
        if (id >= 0 && id <= 2) {
            RedstoneState state = RedstoneState.values()[id];
            this.setRedstoneState(state);
            return true;
        }
        return super.clickMenuButton(player, id);
    }
}