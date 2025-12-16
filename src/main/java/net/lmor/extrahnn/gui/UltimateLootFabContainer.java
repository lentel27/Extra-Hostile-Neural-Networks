package net.lmor.extrahnn.gui;

import dev.shadowsoffire.hostilenetworks.Hostile;
import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.hostilenetworks.item.MobPredictionItem;
import dev.shadowsoffire.placebo.menu.BlockEntityMenu;
import dev.shadowsoffire.placebo.menu.FilteredSlot;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.lmor.extrahnn.ExtraHostile;
import net.lmor.extrahnn.tile.UltimateLootFabTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;

public class UltimateLootFabContainer extends BlockEntityMenu<UltimateLootFabTileEntity> {

    private final Block block;

    public UltimateLootFabContainer(int id, Inventory pInv, BlockPos pos, MenuType<?> type, Block block) {
        super(type, id, pInv, pos);

        this.block = block;

        UltimateLootFabTileEntity.FabItemHandler inv = this.tile.getInventory();
        this.addSlot(new FilteredSlot(inv, 0, 79, 74, (s) -> {
            return s.getItem() == Hostile.Items.PREDICTION.get();
        }));

        for(int y = 0; y < 6; ++y) {
            for(int x = 0; x < 6; ++x) {
                this.addSlot(new FilteredSlot(inv, 1 + y * 6 + x, 100 + x * 18, 7 + y * 18, (s) -> {
                    return false;
                }));
            }
        }

        this.addPlayerSlots(pInv, 26, 128);
        this.mover.registerRule((stack, slot) -> {
            return slot == 0;
        }, 37, this.slots.size());
        this.mover.registerRule((stack, slot) -> {
            return stack.getItem() instanceof MobPredictionItem;
        }, 0, 1);
        this.mover.registerRule((stack, slot) -> {
            return slot < 37;
        }, 37, this.slots.size());
        this.registerInvShuffleRules();
    }

    public boolean stillValid(Player pPlayer) {
        return pPlayer.level().getBlockState(this.pos).getBlock() == block;
    }

    public boolean clickMenuButton(Player pPlayer, int pId) {
        DynamicHolder<DataModel> model = DataModelItem.getStoredModel(this.getSlot(0).getItem());
        if (model.isBound() && pId < model.get().fabDrops().size()) {
            this.tile.setSelection(model, pId);
            return true;
        } else {
            return false;
        }
    }

    public int getEnergyStored() {
        return this.tile.getEnergyStored();
    }

    public int getRuntime() {
        return this.tile.getRuntime();
    }

    public int getSelectedDrop(DataModel model) {
        return this.tile.getSelectedDrop(model);
    }
}
