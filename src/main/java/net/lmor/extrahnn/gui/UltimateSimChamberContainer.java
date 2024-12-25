package net.lmor.extrahnn.gui;

import net.lmor.extrahnn.ExtraHostile;
import net.lmor.extrahnn.item.ExtraDataModelItem;
import net.lmor.extrahnn.tile.UltimateSimChamberTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import shadows.placebo.container.BlockEntityContainer;
import shadows.placebo.container.FilteredSlot;

public class UltimateSimChamberContainer extends BlockEntityContainer<UltimateSimChamberTileEntity> {
    public UltimateSimChamberContainer(int id, Inventory pInv, BlockPos pos) {
        super(ExtraHostile.Containers.ULTIMATE_SIM_CHAMBER.get(), id, pInv, pos);
        UltimateSimChamberTileEntity.SimItemHandler inventory = this.tile.getInventory();
        this.addSlot(new FilteredSlot(inventory, 0, -13, 1, (s) -> {
            return s.getItem() instanceof ExtraDataModelItem;
        }));
        this.addSlot(new FilteredSlot(inventory, 1, 200, 48, (s) -> {
            return ExtraDataModelItem.matchesInput(this.getSlot(0).getItem(), s);
        }));

        this.addSlot(new FilteredSlot(inventory, 2, 158, 6, (s) -> {
            return false;
        }));
        this.addSlot(new FilteredSlot(inventory, 3, 176, 6, (s) -> {
            return false;
        }));
        this.addSlot(new FilteredSlot(inventory, 4, 158, 24, (s) -> {
            return false;
        }));
        this.addSlot(new FilteredSlot(inventory, 5, 176, 24, (s) -> {
            return false;
        }));

        this.addSlot(new FilteredSlot(inventory, 6, 200, 6, (s) -> {
            return false;
        }));
        this.addSlot(new FilteredSlot(inventory, 7, 200, 24, (s) -> {
            return false;
        }));
        this.addSlot(new FilteredSlot(inventory, 8, 158, 48, (s) -> {
            return false;
        }));
        this.addSlot(new FilteredSlot(inventory, 9, 176, 48, (s) -> {
            return false;
        }));


        this.addPlayerSlots(pInv, 36, 175);
        this.mover.registerRule((stack, slot) -> {
            return slot < 10;
        }, 10, this.slots.size());
        this.mover.registerRule((stack, slot) -> {
            return stack.getItem() instanceof ExtraDataModelItem;
        }, 0, 1);
        this.mover.registerRule((stack, slot) -> {
            return ExtraDataModelItem.matchesInput(this.getSlot(0).getItem(), stack);
        }, 1, 2);
        this.registerInvShuffleRules();
    }

    public boolean stillValid(Player pPlayer) {
        return pPlayer.level.getBlockState(this.pos).getBlock() == ExtraHostile.Blocks.ULTIMATE_SIM_CHAMBER.get();
    }

    public int getEnergyStored() {
        return this.tile.getEnergyStored();
    }

    public int getRuntime() {
        return this.tile.getRuntime();
    }

    public boolean didPredictionSucceed() {
        return this.tile.didPredictionSucceed();
    }

    public UltimateSimChamberTileEntity.FailureState getFailState() {
        return this.tile.getFailState();
    }
}
