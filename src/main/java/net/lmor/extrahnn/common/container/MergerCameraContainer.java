package net.lmor.extrahnn.common.container;

import dev.shadowsoffire.hostilenetworks.data.DataModelInstance;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.placebo.menu.BlockEntityMenu;
import dev.shadowsoffire.placebo.menu.FilteredSlot;
import net.lmor.extrahnn.ExtraHostile;
import net.lmor.extrahnn.common.tile.MergerCameraTileEntity;
import net.lmor.extrahnn.common.tile.MergerCameraTileEntity.CameraItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class MergerCameraContainer extends BlockEntityMenu<MergerCameraTileEntity> {
    public MergerCameraContainer(int id, Inventory inv, BlockPos pos) {
        super(ExtraHostile.Containers.MERGER_CAMERA, id, inv, pos);
        CameraItemHandler inventory = this.tile.getInventory();

        int[][] modelSlots = {{0, 63, 10}, {1, 88, 8}, {2, 112, 8}, {3, 137, 10}};
        for (int[] slot : modelSlots) {
            addModelSlot(inventory, slot[0], slot[1], slot[2]);
        }

        this.addSlot(new FilteredSlot(inventory, 4, 183, 35, s -> s.is(ExtraHostile.Items.BLANK_EXTRA_DATA_MODEL)));
        this.addSlot(new FilteredSlot(inventory, 5, 100, 35, s -> false));

        this.addPlayerSlots(inv, 28, 161);
        this.mover.registerRule((stack, slot) -> slot < 6, 6, this.slots.size());
        this.mover.registerRule((stack, slot) -> stack.getItem() instanceof DataModelItem, 0, 4);
        this.mover.registerRule((stack, slot) -> stack.is(ExtraHostile.Items.BLANK_EXTRA_DATA_MODEL), 4,5);

        this.registerInvShuffleRules();
    }

    private void addModelSlot(CameraItemHandler inventory, int index, int x, int y) {
        this.addSlot(new FilteredSlot(inventory, index, x, y,
                s -> s.getItem() instanceof DataModelItem && new DataModelInstance(s, index).getTier().isMax()));
    }

    public boolean stillValid(Player pPlayer) {
        return pPlayer.level().getBlockState(this.pos).is(ExtraHostile.Blocks.MERGER_CAMERA);
    }

    public int getEnergyStored() {
        return this.tile.getEnergyStored();
    }

    public int getRuntime() {
        return this.tile.getRuntime();
    }

    public MergerCameraTileEntity.FailureState getFailState() {
        return this.tile.getFailState();
    }
}
