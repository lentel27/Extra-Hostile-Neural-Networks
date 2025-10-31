package net.lmor.extrahnn;


import net.lmor.extrahnn.ExtraHostileConfig.ConfigMessage;
import net.lmor.extrahnn.api.ISettingCard;
import net.lmor.extrahnn.api.SettingCardMessage;
import net.lmor.extrahnn.common.item.SettingCard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ExtraHostileNetworks.MOD_ID)
public class ExtraHostileEvents {

    public ExtraHostileEvents() {
    }

    @SubscribeEvent
    private static void onServerSetup(ServerAboutToStartEvent event) {
        EHNNUtils.initTiers();
    }

    @SubscribeEvent
    public static void reload(AddReloadListenerEvent e) {
        e.addListener((ResourceManagerReloadListener) resman -> ExtraHostileNetworks.cfg = ExtraHostileConfig.load());
        EHNNUtils.initTiers();
    }

    @SubscribeEvent
    public static void sync(OnDatapackSyncEvent e) {
        ConfigMessage msg = new ConfigMessage();
        e.getRelevantPlayers().forEach(p -> PacketDistributor.sendToPlayer(p, msg));
    }

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
            HolderLookup.Provider provider = level.registryAccess();

            if (!player.isShiftKeyDown() && card.isTag(stack)) {
                boolean success = tile.loadSetting(provider, card.getData(stack), player);

                if (success){
                    card.notifyUser(player, SettingCardMessage.SETTINGS_LOADED);
                } else {
                    card.notifyUser(player, SettingCardMessage.SETTING_INVALID);
                }

                event.setCanceled(true);
                event.setCancellationResult(success ? InteractionResult.SUCCESS : InteractionResult.PASS);
            } else if (player.isShiftKeyDown()) {
                card.notifyUser(player, SettingCardMessage.SETTINGS_SAVED);
                card.setData(stack, tile.saveSetting(provider));

                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }
}
