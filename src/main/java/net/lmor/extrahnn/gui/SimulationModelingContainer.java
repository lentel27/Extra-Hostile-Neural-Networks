package net.lmor.extrahnn.gui;

import dev.shadowsoffire.hostilenetworks.data.CachedModel;
import dev.shadowsoffire.hostilenetworks.data.ModelTier;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.hostilenetworks.item.MobPredictionItem;
import dev.shadowsoffire.placebo.menu.BlockEntityMenu;
import dev.shadowsoffire.placebo.menu.FilteredSlot;
import net.lmor.extrahnn.EHNNUtils;
import net.lmor.extrahnn.ExtraHostile;
import net.lmor.extrahnn.data.ExtraCachedModel;
import net.lmor.extrahnn.data.ExtraModelTier;
import net.lmor.extrahnn.item.ExtraDataModelItem;
import net.lmor.extrahnn.item.UpgradeMachine;
import net.lmor.extrahnn.tile.MergerCameraTileEntity;
import net.lmor.extrahnn.tile.SimulationModelingTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class SimulationModelingContainer extends BlockEntityMenu<SimulationModelingTileEntity> {
    public SimulationModelingContainer(int id, Inventory pInv, BlockPos pos) {
        super(ExtraHostile.Containers.SIMULATOR_MODELING.get(), id, pInv, pos);
        SimulationModelingTileEntity.SimulatorModelingItemHandler inventory = this.tile.getInventory();

        this.addSlot(new FilteredSlot(inventory, 0, -13, 1, (s) -> {
            return EHNNUtils.allowedTier(s) && ((s.getItem() instanceof DataModelItem && new CachedModel(s, 0).getTier() != ModelTier.SELF_AWARE) ||
                    (s.getItem() instanceof ExtraDataModelItem && new ExtraCachedModel(s, 0).getTier() != ExtraModelTier.OMNIPOTENT));
        }));

        this.addSlot(new FilteredSlot(inventory, 1, -13, 21, (s) -> {
            return s.getItem() instanceof UpgradeMachine;
        }));

        this.addSlot(new FilteredSlot(inventory, 2, -13, 39, (s) -> {
            return s.getItem() instanceof UpgradeMachine;
        }));


        this.addPlayerSlots(pInv, 36, 106);
        this.mover.registerRule((stack, slot) -> {
            return slot < 3;
        }, 3, this.slots.size());
        this.mover.registerRule((stack, slot) -> {
            return EHNNUtils.allowedTier(stack) &&
                    ((stack.getItem() instanceof DataModelItem && new CachedModel(stack, 0).getTier() != ModelTier.SELF_AWARE) ||
                    (stack.getItem() instanceof ExtraDataModelItem && new ExtraCachedModel(stack, 0).getTier() != ExtraModelTier.OMNIPOTENT));
        }, 0, 1);
        this.mover.registerRule((stack, slot) -> {
            return stack.getItem() instanceof UpgradeMachine;
        }, 1, 3);

        this.registerInvShuffleRules();
    }

    public boolean stillValid(Player pPlayer) {
        return pPlayer.level().getBlockState(this.pos).getBlock() == ExtraHostile.Blocks.SIMULATOR_MODELING.get();
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
