package net.lmor.extrahnn.event;

import net.lmor.extrahnn.ExtraHostileNetworks;
import net.lmor.extrahnn.api.ISettingCard;
import net.lmor.extrahnn.api.SettingCardMessage;
import net.lmor.extrahnn.item.SettingCard;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExtraHostileNetworks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RightClickBlockHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();

        if (level.isClientSide) return;

        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof SettingCard card)) return;

        BlockPos pos = event.getPos();
        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof ISettingCard tile) {
            if (!player.isShiftKeyDown() && card.isTag(stack)) {
                boolean success = tile.loadSetting(card.getData(stack), player);

                if (success){
                    card.notifyUser(player, SettingCardMessage.SETTINGS_LOADED);
                } else {
                    card.notifyUser(player, SettingCardMessage.SETTING_INVALID);
                }

                event.setCanceled(true);
                event.setCancellationResult(success ? InteractionResult.SUCCESS : InteractionResult.PASS);
            } else if (player.isShiftKeyDown()) {
                card.notifyUser(player, SettingCardMessage.SETTINGS_SAVED);
                card.setData(stack, tile.saveSetting());

                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }
}
