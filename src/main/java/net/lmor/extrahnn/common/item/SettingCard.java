package net.lmor.extrahnn.common.item;

import net.lmor.extrahnn.ExtraHostile;
import net.lmor.extrahnn.api.SettingCardMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SettingCard extends Item {
    public SettingCard() {
        super(new Item.Properties().stacksTo(1));
    }

    public void setData(ItemStack item, CompoundTag data) {
        item.set(ExtraHostile.Components.SETTING_CARD, data);
    }

    public CompoundTag getData(ItemStack item) {
        return item.getOrDefault(ExtraHostile.Components.SETTING_CARD, new CompoundTag());
    }

    public void clearDataCard(Player player, InteractionHand hand){
        SettingCard mem = (SettingCard)player.getItemInHand(hand).getItem();
        mem.notifyUser(player, SettingCardMessage.SETTINGS_CLEARED);
        player.getItemInHand(hand).set(ExtraHostile.Components.SETTING_CARD, new CompoundTag());
    }

    public boolean isTag(ItemStack item){
        return !item.getOrDefault(ExtraHostile.Components.SETTING_CARD, new CompoundTag()).equals(new CompoundTag());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        if (!player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        HitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (hit.getType() == HitResult.Type.MISS) {
            if (!level.isClientSide) {
                this.clearDataCard(player, hand);
            }
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
        }

        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    public void notifyUser(Player player, SettingCardMessage msg) {
        if (!player.getCommandSenderWorld().isClientSide()) {
            player.displayClientMessage(msg.getTranslate(), true);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        if (isTag(stack)) {
            list.add(Component.translatable("extrahnn.info.setting_card").withStyle(ChatFormatting.AQUA));
        }
    }
}
