package net.lmor.extrahnn;

import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.data.DataModelInstance;
import dev.shadowsoffire.hostilenetworks.data.ModelTier;
import dev.shadowsoffire.hostilenetworks.data.ModelTierRegistry;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.lmor.extrahnn.common.item.ExtraDataModelItem;
import net.lmor.extrahnn.data.ExtraDataModelInstance;
import net.lmor.extrahnn.data.ExtraModelTier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EHNNUtils {
    public static List<String> validTiersHostile = new ArrayList<>();
    public static List<String> validTiersExtra = new ArrayList<>();

    public static void initTiers() {
        validTiersHostile.clear();
        validTiersExtra.clear();

        try {
            ModelTier tierHNN = ModelTierRegistry.getMinTier();
            while (tierHNN != null) {
                validTiersHostile.add(tierHNN.name().toLowerCase());
                if (tierHNN == ModelTierRegistry.getMaxTier()) break;
                tierHNN = ModelTierRegistry.next(tierHNN);
            }
        } catch (Exception e) {
            ExtraHostileNetworks.LOGGER.warn("Hostile NN tiers not yet available!");
        }

        ExtraModelTier tierENHH = ExtraModelTier.getMinTier();
        while (tierENHH != null) {
            validTiersExtra.add(tierENHH.name().toLowerCase());
            if (tierENHH == ExtraModelTier.getMaxTier()) break;
            tierENHH = ExtraModelTier.next(tierENHH);
        }

        ExtraHostileNetworks.LOGGER.info("Loaded tiers: HNN={}, EHNN={}", validTiersHostile, validTiersExtra);
    }

    public static boolean allowedTier(ItemStack stack){
        if (validTiersHostile.isEmpty() || validTiersExtra.isEmpty()) initTiers();

        String minimumTierHostile = ExtraHostileConfig.minimumTierHostile;
        String minimumTierExtra = ExtraHostileConfig.minimumTierExtra;
        int minIndex = -1;
        int itemIndex = -1;

        if (stack.getItem() instanceof DataModelItem){
            String itemTier = new DataModelInstance(stack, 0).getTier().name().toLowerCase();

            minIndex = validTiersHostile.indexOf(minimumTierHostile.toLowerCase());
            itemIndex = validTiersHostile.indexOf(itemTier);

        } else if (stack.getItem() instanceof ExtraDataModelItem){
            String itemTier = new ExtraDataModelInstance(stack, 0).getTier().name.toLowerCase();

            minIndex = validTiersExtra.indexOf(minimumTierExtra.toLowerCase());
            itemIndex = validTiersExtra.indexOf(itemTier);
        }

        if (minIndex == -1 || itemIndex == -1) return false;
        return itemIndex >= minIndex;
    }

    public static boolean allowedBlackListModel(ItemStack stack){
        if (validTiersHostile.isEmpty() || validTiersExtra.isEmpty()) initTiers();

        List<String> blackListedTiers = Arrays.stream(ExtraHostileConfig.blackList).toList();
        if (blackListedTiers.isEmpty()) return true;

        List<DynamicHolder<DataModel>> allEntities = new ArrayList<>();

        if (stack.getItem() instanceof DataModelItem){
            allEntities = List.of(DataModelItem.getStoredModel(stack));

        } else if (stack.getItem() instanceof ExtraDataModelItem){
            allEntities = ExtraDataModelItem.getStoredModels(stack);
        }

        for (DynamicHolder<DataModel> model: allEntities){
            if (blackListedTiers.contains(model.getId().toString())) return false;
        }

        return true;
    }

    public static List<Component> translateTier(){
        String tierHostile = ExtraHostileConfig.minimumTierHostile.toLowerCase();
        String tierExtra = ExtraHostileConfig.minimumTierExtra.toLowerCase();

        return List.of(
                Component.translatable("extrahnn.info.minimum_tier_hnn", Component.translatable("hostilenetworks.tier." + tierHostile).getString()),
                Component.translatable("extrahnn.info.minimum_tier_ehnn", Component.translatable("extrahnn.tier." + tierExtra).getString())
        );
    }

    public static float textTickRate(int allChar, int durationMachine){
        float textDuration = durationMachine * 0.9f;
        float charPerTick = allChar / textDuration;

        return (float) Math.max(0.1, charPerTick);
    }
}
