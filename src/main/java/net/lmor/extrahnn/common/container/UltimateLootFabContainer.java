package net.lmor.extrahnn.common.container;

import dev.shadowsoffire.hostilenetworks.Hostile;
import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.hostilenetworks.item.MobPredictionItem;
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

public class UltimateLootFabContainer extends BlockEntityMenu<UltimateLootFabTileEntity> {
    private final Block block;

    public UltimateLootFabContainer(int id, Inventory inv, BlockPos pos, MenuType<?> type, Block block) {
        super(type, id, inv, pos);
        this.block = block;

        FabItemHandler inventory = this.tile.getInventory();
        this.addSlot(new FilteredSlot(inventory, 0, 79, 62, s -> s.is(Hostile.Items.PREDICTION)));

        for(int y = 0; y < 4; ++y) {
            for(int x = 0; x < 4; ++x) {
                this.addSlot(new FilteredSlot(inventory, 1 + y * 4 + x, 100 + x * 18, 7 + y * 18, s -> false));
            }
        }

        this.addPlayerSlots(inv, 8, 92);
        this.mover.registerRule((stack, slot) -> slot == 0, 17, this.slots.size());
        this.mover.registerRule((stack, slot) -> stack.getItem() instanceof MobPredictionItem, 0, 1);
        this.mover.registerRule((stack, slot) -> slot < 17, 17, this.slots.size());
        this.registerInvShuffleRules();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.level().getBlockState(this.pos).is(block);
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int pId) {
        DynamicHolder<DataModel> model = DataModelItem.getStoredModel(this.getSlot(0).getItem());
        if (!model.isBound() || pId >= model.get().fabDrops().size()) return false;
        this.tile.setSelection(model, pId);
        return true;
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