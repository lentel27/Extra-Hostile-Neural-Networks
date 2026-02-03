package net.lmor.extrahnn.gui;

import dev.shadowsoffire.placebo.menu.BlockEntityMenu;
import dev.shadowsoffire.placebo.menu.FilteredSlot;
import net.lmor.extrahnn.item.ExtraDataModelItem;
import net.lmor.extrahnn.tile.UltimateSimChamberTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;

public class UltimateSimChamberContainer extends BlockEntityMenu<UltimateSimChamberTileEntity> {

    private final Block block;

    public UltimateSimChamberContainer(int id, Inventory pInv, BlockPos pos, MenuType<?> type, Block block) {
        super(type, id, pInv, pos);

        this.block = block;

        UltimateSimChamberTileEntity.SimItemHandler inventory = this.tile.getInventory();
        this.addSlot(new FilteredSlot(inventory, 0, -13, 1, (s) -> s.getItem() instanceof ExtraDataModelItem));
        this.addSlot(new FilteredSlot(inventory, 1, 200, 48, (s) -> ExtraDataModelItem.matchesInput(this.getSlot(0).getItem(), s)));

        this.addSlot(new FilteredSlot(inventory, 2, 158, 6, (s) -> false));
        this.addSlot(new FilteredSlot(inventory, 3, 176, 6, (s) -> false));
        this.addSlot(new FilteredSlot(inventory, 4, 158, 24, (s) -> false));
        this.addSlot(new FilteredSlot(inventory, 5, 176, 24, (s) -> false));

        this.addSlot(new FilteredSlot(inventory, 6, 200, 6, (s) -> false));
        this.addSlot(new FilteredSlot(inventory, 7, 200, 24, (s) -> false));
        this.addSlot(new FilteredSlot(inventory, 8, 158, 48, (s) -> false));
        this.addSlot(new FilteredSlot(inventory, 9, 176, 48, (s) -> false));


        this.addPlayerSlots(pInv, 36, 175);
        this.mover.registerRule((stack, slot) -> slot < 10, 10, this.slots.size());
        this.mover.registerRule((stack, slot) -> stack.getItem() instanceof ExtraDataModelItem, 0, 1);
        this.mover.registerRule((stack, slot) -> ExtraDataModelItem.matchesInput(this.getSlot(0).getItem(), stack), 1, 2);
        this.registerInvShuffleRules();
    }

    public boolean stillValid(Player pPlayer) {
        return pPlayer.level().getBlockState(this.pos).getBlock() == block;
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

    public int getMaxEnergyStored() {
        return this.tile.CAP;
    }

    public int getDuration() {
        return this.tile.DURATION;
    }

    public UltimateSimChamberTileEntity.FailureState getFailState() {
        return this.tile.getFailState();
    }
}
