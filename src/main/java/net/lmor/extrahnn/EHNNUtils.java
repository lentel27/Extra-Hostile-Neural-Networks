package net.lmor.extrahnn;

import dev.shadowsoffire.hostilenetworks.data.CachedModel;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import net.lmor.extrahnn.data.ExtraCachedModel;
import net.lmor.extrahnn.item.ExtraDataModelItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class EHNNUtils {
    public static List<String> validTiersHostile = List.of(
            "self_aware", "superior", "advanced", "basic", "faulty"
    );

    public static List<String> validTiersExtra = List.of(
            "omnipotent", "synthetic", "adaptive", "intelligent", "autonomous"
    );

    public static boolean allowedTier(ItemStack stack){
        String minimumTierHostile = ExtraHostileConfig.minimumUpgradeTierHostile;
        String minimumTierExtra = ExtraHostileConfig.minimumUpgradeTierExtra;
        String itemTier = "";
        int minIndex = -1;
        int itemIndex = -1;

        if (stack.getItem() instanceof DataModelItem){
            itemTier = new CachedModel(stack, 0).getTier().name;

            minIndex = validTiersHostile.indexOf(minimumTierHostile);
            itemIndex = validTiersHostile.indexOf(itemTier);

        } else if (stack.getItem() instanceof ExtraDataModelItem){
            itemTier = new ExtraCachedModel(stack, 0).getTier().name;

            minIndex = validTiersExtra.indexOf(minimumTierExtra);
            itemIndex = validTiersExtra.indexOf(itemTier);
        }

        if (minIndex == -1 || itemIndex == -1) return false;

        return itemIndex <= minIndex;
    }

    public static List<Component> translateTier(){
        String tierHostile = ExtraHostileConfig.minimumUpgradeTierHostile.toLowerCase();
        String tierExtra = ExtraHostileConfig.minimumUpgradeTierExtra.toLowerCase();

        return List.of(
                Component.translatable("extrahnn.info.minimum_tier_hnn", Component.translatable("hostilenetworks.tier." + tierHostile).getString()),
                Component.translatable("extrahnn.info.minimum_tier_ehnn", Component.translatable("extrahnn.tier." + tierExtra).getString())
        );
    }
}
