package net.lmor.extrahnn.item;

import net.lmor.extrahnn.api.SettingCardMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class SettingCard extends Item {
    public SettingCard(Properties properties) {
        super(properties);
    }

    public void setData(ItemStack is, CompoundTag data) {
        CompoundTag c = is.getOrCreateTag();
        c.put("Data", data);
    }

    public CompoundTag getData(ItemStack is) {
        CompoundTag c = is.getOrCreateTag();
        return c.getCompound("Data").copy();
    }

    public void clearDataCard(Player player, InteractionHand hand){
        SettingCard mem = (SettingCard)player.getItemInHand(hand).getItem();
        mem.notifyUser(player, SettingCardMessage.SETTINGS_CLEARED);
        player.getItemInHand(hand).setTag(null);
    }

    public boolean isTag(ItemStack itemStack){
        return itemStack.hasTag();
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
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
}
