package net.lmor.extrahnn.mixin;

import dev.shadowsoffire.hostilenetworks.tile.LootFabTileEntity;
import dev.shadowsoffire.hostilenetworks.util.RedstoneState;
import dev.shadowsoffire.placebo.network.VanillaPacketDispatcher;
import net.lmor.extrahnn.api.ISettingCard;
import net.lmor.extrahnn.common.tile.UltimateLootFabTileEntity;
import net.minecraft.core.HolderLookup;
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
    protected LootFabTileEntity extrahnn$self() {
        return (LootFabTileEntity) (Object) this;
    }

    @Override
    public CompoundTag saveSetting(HolderLookup.Provider regs) {
        CompoundTag tag = new CompoundTag();

        CompoundTag selectionsTag = new CompoundTag();
        this.writeSelections(selectionsTag);
        tag.put("selections", selectionsTag);
        tag.putInt("redstoneState", extrahnn$self().getRedstoneState().ordinal());

        tag.putString("config", extrahnn$self().getClass().getName());
        return tag;
    }

    @Override
    public boolean loadSetting(HolderLookup.Provider regs, CompoundTag tag, Player player) {
        if (!tag.contains("config")
                || (!tag.getString("config").equals(extrahnn$self().getClass().getName())
                && !tag.getString("config").equals(UltimateLootFabTileEntity.class.getName()))
        ) return false;


        this.readSelections(tag.getCompound("selections"));
        extrahnn$self().setRedstoneState(RedstoneState.values()[tag.getInt("redstoneState")]);

        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(extrahnn$self());
        extrahnn$self().setChanged();
        return true;
    }
}
