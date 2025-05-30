package net.lmor.extrahnn.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public interface ISettingCard {

    CompoundTag saveSetting();

    boolean loadSetting(CompoundTag tag, Player player);
}
