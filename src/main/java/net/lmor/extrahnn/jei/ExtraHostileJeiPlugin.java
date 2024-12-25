package net.lmor.extrahnn.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.lmor.extrahnn.ExtraHostile.Blocks;
import net.lmor.extrahnn.ExtraHostile.Items;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import shadows.hostilenetworks.jei.LootFabCategory;

@JeiPlugin
public class ExtraHostileJeiPlugin implements IModPlugin {
    public static final ResourceLocation UID = new ResourceLocation("extrahnn", "plugin");

    public ExtraHostileJeiPlugin() {
    }

    public void registerRecipes(IRecipeRegistration reg) {
        reg.addItemStackInfo(new ItemStack(Items.BLANK_EXTRA_DATA_MODEL.get()),
                Component.translatable("extrahnn.info.jei",
                        Component.translatable("item.hostilenetworks.data_model", "?").withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.BOLD),
                        Component.translatable("hostilenetworks.tier.self_aware").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD),
                        Component.translatable("block.extrahnn.merger_camera"),
                        Component.translatable("item.extrahnn.extra_data_model", "?", "?", "?", "?").withStyle(ChatFormatting.DARK_PURPLE).withStyle(ChatFormatting.BOLD)
                        ));
    }

    public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
        reg.addRecipeCatalyst(new ItemStack(Blocks.ULTIMATE_LOOT_FABRICATOR.get()), LootFabCategory.TYPE);
    }

    public ResourceLocation getPluginUid() {
        return UID;
    }
}
