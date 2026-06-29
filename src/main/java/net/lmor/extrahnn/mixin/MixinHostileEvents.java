package net.lmor.extrahnn.mixin;

import dev.shadowsoffire.hostilenetworks.Hostile;
import dev.shadowsoffire.hostilenetworks.HostileEvents;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HostileEvents.class)
public class MixinHostileEvents {

    @Inject(
            method = "modelAttunement",
            at = @At("HEAD"), remap = false, cancellable = true
    )
    private static void clickModel(EntityInteractSpecific interact, CallbackInfo callbackInfo) {
        Player player = interact.getEntity();
        ItemStack stack = player.getItemInHand(interact.getHand());
        if (!stack.is(Hostile.Items.BLANK_DATA_MODEL.value())) return;

        if (!player.level().isClientSide && extrahnn$blackListFound(interact.getTarget().getType())) {
            Component msg = Component.translatable("extrahnn.info.not_build").withStyle(ChatFormatting.RED);
            player.sendSystemMessage(msg);
            interact.setCancellationResult(InteractionResult.FAIL);
            callbackInfo.cancel();
        }
    }

    @Unique
    private static boolean extrahnn$blackListFound(EntityType<?> entity){
        return ExtraHostileConfig.blackListClick.contains(entity.getDescriptionId());
    }
}
