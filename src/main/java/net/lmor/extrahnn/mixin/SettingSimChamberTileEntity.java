package net.lmor.extrahnn.mixin;

import dev.shadowsoffire.hostilenetworks.Hostile;
import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.data.DataModelRegistry;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.hostilenetworks.tile.SimChamberTileEntity;
import dev.shadowsoffire.hostilenetworks.util.RedstoneState;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.lmor.extrahnn.api.ISettingCard;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SimChamberTileEntity.class)
public class SettingSimChamberTileEntity implements ISettingCard {
    @Shadow(remap = false) @Final protected SimChamberTileEntity.SimItemHandler inventory;

    @Unique
    protected SimChamberTileEntity extrahnn$self() {
        return (SimChamberTileEntity) (Object) this;
    }

    @Override
    public CompoundTag saveSetting(HolderLookup.Provider regs) {
        CompoundTag tag = new CompoundTag();

        ItemStack item = this.inventory.getStackInSlot(0);
        if (!item.isEmpty()){
            CompoundTag saveTag = new CompoundTag();
            tag.put("dataModel", item.save(regs, saveTag));
        }
        tag.putString("config", extrahnn$self().getClass().getName());
        return tag;
    }

    @Override
    public boolean loadSetting(HolderLookup.Provider regs, CompoundTag tag, Player player) {
        if (!tag.contains("config") || !tag.getString("config").equals(extrahnn$self().getClass().getName())) return false;

        extrahnn$self().setRedstoneState(RedstoneState.values()[tag.getInt("redstoneState")]);

        CompoundTag saveTag = tag.getCompound("dataModel");
        Inventory playerInv = player.getInventory();

        ItemStack item = ItemStack.parseOptional(regs, saveTag);
        int inventoryIndexItem = extrahnn$findDataModel(item, playerInv);

        if (!item.isEmpty() && inventoryIndexItem != -1){
            playerInv.removeItem(inventoryIndexItem, 1);
            if (!this.inventory.getStackInSlot(0).isEmpty()){
                playerInv.add(this.inventory.getStackInSlot(0));
                this.inventory.setStackInSlot(0, ItemStack.EMPTY);
            }

            this.inventory.setStackInSlot(0, item);
        }

        return true;
    }

    private int extrahnn$findDataModel(ItemStack findItem, Inventory playerInv){
        DynamicHolder<DataModel> findModels = findItem.getOrDefault(Hostile.Components.DATA_MODEL, DataModelRegistry.INSTANCE.emptyHolder());
        if (findModels.is(DataModelRegistry.INSTANCE.emptyHolder().getId())) return -1;

        for(int i = 0; i < playerInv.items.size(); ++i) {
            ItemStack itemInv = playerInv.items.get(i);
            if (itemInv.isEmpty() || !itemInv.is(findItem.getItem()) || !(itemInv.getItem() instanceof DataModelItem)) continue;

            DynamicHolder<DataModel> itemInvModels = itemInv.getOrDefault(Hostile.Components.DATA_MODEL, DataModelRegistry.INSTANCE.emptyHolder());
            if (findModels.is(itemInvModels.getId())) return i;
        }

        return -1;
    }
}
