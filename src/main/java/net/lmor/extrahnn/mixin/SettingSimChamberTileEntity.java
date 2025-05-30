package net.lmor.extrahnn.mixin;

import dev.shadowsoffire.hostilenetworks.tile.SimChamberTileEntity;
import net.lmor.extrahnn.api.ISettingCard;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SimChamberTileEntity.class)
public class SettingSimChamberTileEntity implements ISettingCard {
    @Shadow @Final protected SimChamberTileEntity.SimItemHandler inventory;

    @Override
    public CompoundTag saveSetting() {
        CompoundTag tag = new CompoundTag();

        ItemStack item = this.inventory.getStackInSlot(0);
        if (!item.isEmpty()){
            CompoundTag saveTag = new CompoundTag();
            item.save(saveTag);

            tag.put("dataModel", saveTag);
        }
        tag.putString("config", "SimChamberTileEntity");
        return tag;
    }

    @Override
    public boolean loadSetting(CompoundTag tag, Player player) {
        if (!tag.contains("config") || !tag.getString("config").equals("SimChamberTileEntity")) return false;
        if (!tag.contains("dataModel")) return false;

        CompoundTag saveTag = tag.getCompound("dataModel");
        Inventory playerInv = player.getInventory();

        ItemStack item = ItemStack.of(saveTag);
        int inventoryIndexItem = playerInv.findSlotMatchingItem(item);
        ItemStack machineItem = this.inventory.getStackInSlot(0);

        if (machineItem == item) return false;

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
}
