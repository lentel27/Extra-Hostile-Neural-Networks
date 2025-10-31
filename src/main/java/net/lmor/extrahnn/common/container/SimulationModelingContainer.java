package net.lmor.extrahnn.common.container;

import dev.shadowsoffire.hostilenetworks.data.DataModelInstance;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.placebo.menu.BlockEntityMenu;
import dev.shadowsoffire.placebo.menu.FilteredSlot;
import net.lmor.extrahnn.EHNNUtils;
import net.lmor.extrahnn.ExtraHostile;
import net.lmor.extrahnn.common.item.ExtraDataModelItem;
import net.lmor.extrahnn.common.item.UpgradeMachine;
import net.lmor.extrahnn.common.tile.SimulationModelingTileEntity;
import net.lmor.extrahnn.data.ExtraDataModelInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SimulationModelingContainer extends BlockEntityMenu<SimulationModelingTileEntity> {
    public SimulationModelingContainer(int id, Inventory inv, BlockPos pos) {
        super(ExtraHostile.Containers.SIMULATOR_MODELING, id, inv, pos);
        SimulationModelingTileEntity.SimulatorModelingItemHandler inventory = this.tile.getInventory();

        this.addSlot(new FilteredSlot(inventory, 0, -13, 1, this::checkModelSlot));
        this.addSlot(new FilteredSlot(inventory, 1, -13, 21, s -> s.getItem() instanceof UpgradeMachine));
        this.addSlot(new FilteredSlot(inventory, 2, -13, 39, s -> s.getItem() instanceof UpgradeMachine));


        this.addPlayerSlots(inv, 36, 106);
        this.mover.registerRule((stack, slot) -> slot < 3, 3, this.slots.size());
        this.mover.registerRule((stack, slot) -> checkModelSlot(stack), 0, 1);
        this.mover.registerRule((stack, slot) -> stack.getItem() instanceof UpgradeMachine, 1, 3);

        this.registerInvShuffleRules();
    }

    private boolean checkModelSlot(ItemStack stack){
        return EHNNUtils.allowedTier(stack) && EHNNUtils.allowedBlackListModel(stack) &&
                ((stack.getItem() instanceof DataModelItem && !new DataModelInstance(stack, 0).getTier().isMax()) ||
                (stack.getItem() instanceof ExtraDataModelItem && !new ExtraDataModelInstance(stack, 0).getTier().isMax()));
    }

    public boolean stillValid(Player pPlayer) {
        return pPlayer.level().getBlockState(this.pos).is(ExtraHostile.Blocks.SIMULATOR_MODELING);
    }

    public int getEnergyStored() {
        return this.tile.getEnergyStored();
    }

    public int getEnergyCost() {
        return this.tile.getEnergyCost();
    }

    public int getRuntime() {
        return this.tile.getRuntime();
    }

    public int getRuntimeUpgrade() {
        return this.tile.getRuntimeUpgrade();
    }

    public boolean getUpgradeDataKill() {
        return this.tile.getUpgradeDataKill();
    }

}
