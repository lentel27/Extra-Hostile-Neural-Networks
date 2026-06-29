package net.lmor.extrahnn.common.container;

import dev.shadowsoffire.hostilenetworks.Hostile;
import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.hostilenetworks.item.MobPredictionItem;
import dev.shadowsoffire.hostilenetworks.util.FabSelection;
import dev.shadowsoffire.hostilenetworks.util.FabSelection.ProductionMode;
import dev.shadowsoffire.hostilenetworks.util.RedstoneState;
import dev.shadowsoffire.placebo.menu.BlockEntityMenu;
import dev.shadowsoffire.placebo.menu.FilteredSlot;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.lmor.extrahnn.common.tile.UltimateLootFabTileEntity;
import net.lmor.extrahnn.common.tile.UltimateLootFabTileEntity.FabItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import static dev.shadowsoffire.hostilenetworks.gui.LootFabMenu.*;

public class UltimateLootFabContainer extends BlockEntityMenu<UltimateLootFabTileEntity> {
    private final Block block;

    public UltimateLootFabContainer(int id, Inventory inv, BlockPos pos, MenuType<?> type, Block block) {
        super(type, id, inv, pos);
        this.block = block;

        FabItemHandler inventory = this.tile.getInventory();
        this.addSlot(new FilteredSlot(inventory, 0, 79, 74, s -> s.is(Hostile.Items.PREDICTION)));

        for(int y = 0; y < 6; ++y) {
            for(int x = 0; x < 6; ++x) {
                this.addSlot(new FilteredSlot(inventory, 1 + y * 6 + x, 100 + x * 18, 7 + y * 18, s -> false));
            }
        }

        this.addPlayerSlots(inv, 26, 128);
        this.mover.registerRule((stack, slot) -> slot == 0, 37, this.slots.size());
        this.mover.registerRule((stack, slot) -> stack.getItem() instanceof MobPredictionItem, 0, 1);
        this.mover.registerRule((stack, slot) -> slot < 37, 37, this.slots.size());
        this.registerInvShuffleRules();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.level().getBlockState(this.pos).is(block);
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int pId) {
        if (pId >= REDSTONE_BASE && pId < REDSTONE_BASE + RedstoneState.values().length) {
            this.setRedstoneState(RedstoneState.values()[pId - REDSTONE_BASE]);
            return true;
        }

        DynamicHolder<DataModel> model = DataModelItem.getStoredModel(this.getSlot(0).getItem());

        if (!model.isBound()) return false;

        if (pId == BTN_CLEAR_QUEUE) {
            this.tile.clearQueue(model);
            return true;
        }
        if (pId == BTN_CYCLE_MODE) {
            this.tile.cycleMode(model);
            return true;
        }
        if (pId == BTN_CLEAR_FIXED) {
            this.tile.setFixedDrop(model, -1);
            return true;
        }
        if (pId >= QUEUE_REMOVE_BASE && pId < REDSTONE_BASE) {
            this.tile.removeFromQueue(model, pId - QUEUE_REMOVE_BASE);
            return true;
        }

        if (pId >= DROP_BASE && pId < QUEUE_REMOVE_BASE && pId < model.get().fabDrops().size()) {
            if (this.tile.getSelection(model.get()).mode() == ProductionMode.QUEUE)
                this.tile.appendToQueue(model, pId);
            else this.tile.setFixedDrop(model, pId);
            return true;
        }

        return false;
    }

    public void setRedstoneState(RedstoneState state) {
        this.tile.setRedstoneState(state);
    }

    public RedstoneState getRedstoneState() {
        return this.tile.getRedstoneState();
    }

    public int getEnergyStored() {
        return this.tile.getEnergyStored();
    }

    public int getMaxEnergyStored(){
        return this.tile.CAP;
    }

    public int getEnergyCost(){
        return this.tile.COST;
    }

    public int getDuration(){
        return this.tile.DURATION;
    }

    public int getRuntime() {
        return this.tile.getRuntime();
    }

    public int getSelectedDrop(DataModel model) {
        return this.tile.getSelectedDrop(model);
    }

    public int getProgress(){
        return this.tile.getRuntime();
    }

    public int getMaxProgress(){
        return this.tile.DURATION;
    }

    public FabSelection getSelection(DataModel model) {
        return this.tile.getSelection(model);
    }
}