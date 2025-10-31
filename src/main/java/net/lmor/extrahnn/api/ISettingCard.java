package net.lmor.extrahnn.api;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public interface ISettingCard {

    CompoundTag saveSetting(HolderLookup.Provider regs);

    boolean loadSetting(HolderLookup.Provider regs, CompoundTag tag, Player player);
}
