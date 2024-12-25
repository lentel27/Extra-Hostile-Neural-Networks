package net.lmor.extrahnn.gui;

import shadows.hostilenetworks.data.CachedModel;
import shadows.hostilenetworks.data.ModelTier;
import shadows.hostilenetworks.item.DataModelItem;
import net.lmor.extrahnn.ExtraHostile;
import net.lmor.extrahnn.tile.MergerCameraTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import shadows.placebo.container.BlockEntityContainer;
import shadows.placebo.container.FilteredSlot;

public class MergerCameraContainer extends BlockEntityContainer<MergerCameraTileEntity> {
    public MergerCameraContainer(int id, Inventory pInv, BlockPos pos) {
        super(ExtraHostile.Containers.MERGER_CAMERA.get(), id, pInv, pos);
        MergerCameraTileEntity.CameraItemHandler inventory = this.tile.getInventory();

        this.addSlot(new FilteredSlot(inventory, 0, 63, 10, (s) -> {
            return s.getItem() instanceof DataModelItem && new CachedModel(s, 0).getTier() == ModelTier.SELF_AWARE;
        }));
        this.addSlot(new FilteredSlot(inventory, 1, 88, 8, (s) -> {
            return s.getItem() instanceof DataModelItem && new CachedModel(s, 1).getTier() == ModelTier.SELF_AWARE;
        }));
        this.addSlot(new FilteredSlot(inventory, 2, 112, 8, (s) -> {
            return s.getItem() instanceof DataModelItem && new CachedModel(s, 2).getTier() == ModelTier.SELF_AWARE;
        }));
        this.addSlot(new FilteredSlot(inventory, 3, 137, 10, (s) -> {
            return s.getItem() instanceof DataModelItem && new CachedModel(s, 3).getTier() == ModelTier.SELF_AWARE;
        }));

        this.addSlot(new FilteredSlot(inventory, 4, 183, 35, (s) -> {
            return s.getItem() == ExtraHostile.Items.BLANK_EXTRA_DATA_MODEL.get();
        }));

        this.addSlot(new FilteredSlot(inventory, 5, 100, 35, (s) -> {
            return false;
        }));


        this.addPlayerSlots(pInv, 28, 161);
        this.mover.registerRule((stack, slot) -> {
            return slot == 5;
        }, 5, 6);
        this.mover.registerRule((stack, slot) -> {
            return stack.getItem() instanceof DataModelItem;
        }, 0, 4);
        this.mover.registerRule((stack, slot) -> {
            return stack.getItem() == ExtraHostile.Items.BLANK_EXTRA_DATA_MODEL.get();
        }, 4,5);

        this.registerInvShuffleRules();
    }

    public boolean stillValid(Player pPlayer) {
        return pPlayer.level.getBlockState(this.pos).getBlock() == ExtraHostile.Blocks.MERGER_CAMERA.get();
    }

    public int getEnergyStored() {
        return this.tile.getEnergyStored();
    }

    public int getRuntime() {
        return this.tile.getRuntime();
    }
    public boolean getOverheat() {
        return this.tile.getOverheat();
    }

    public MergerCameraTileEntity.FailureState getFailState() {
        return this.tile.getFailState();
    }
}
