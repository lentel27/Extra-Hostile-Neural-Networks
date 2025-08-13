package net.lmor.extrahnn.mixin;

import dev.shadowsoffire.hostilenetworks.tile.LootFabTileEntity;
import dev.shadowsoffire.placebo.recipe.VanillaPacketDispatcher;
import net.lmor.extrahnn.api.ISettingCard;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LootFabTileEntity.class)
public abstract class SettingLootFabTileEntity implements ISettingCard {
    @Shadow(remap = false)
    protected abstract void readSelections(CompoundTag tag);

    @Shadow(remap = false)
    protected abstract CompoundTag writeSelections(CompoundTag tag);

    @Unique
    protected LootFabTileEntity self() {
        return (LootFabTileEntity) (Object) this;
    }

    @Override
    public CompoundTag saveSetting() {
        CompoundTag tag = new CompoundTag();

        CompoundTag saveTag = new CompoundTag();
        this.writeSelections(saveTag);
        tag.put("selections", saveTag);

        return tag;
    }

    @Override
    public boolean loadSetting(CompoundTag tag, Player player) {
        if (!tag.contains("selections")) return false;

        this.readSelections(tag.getCompound("selections"));
        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(self());
        self().setChanged();
        return true;
    }
}
