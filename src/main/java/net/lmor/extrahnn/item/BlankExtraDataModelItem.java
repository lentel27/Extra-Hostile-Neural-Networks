package net.lmor.extrahnn.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import shadows.hostilenetworks.util.Color;

import java.util.List;

public class BlankExtraDataModelItem extends Item {
    public BlankExtraDataModelItem(Properties pProperties) {
        super(pProperties);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> list, TooltipFlag pFlag) {

        if (Screen.hasShiftDown()) {
            list.add(Component.translatable("extrahnn.info.blank_extra_data_model.shift").withStyle(ChatFormatting.AQUA));
        } else {
            list.add(Component.translatable("extrahnn.info.blank_extra_data_model",
                    Color.withColor("hostilenetworks.tier.self_aware", ChatFormatting.GOLD.getColor())).withStyle(ChatFormatting.GRAY));
            list.add(Component.literal(""));
            list.add(Component.translatable("hostilenetworks.info.hold_shift", Color.withColor("hostilenetworks.color_text.shift", ChatFormatting.WHITE.getColor())).withStyle(ChatFormatting.GRAY));
        }
    }
}
