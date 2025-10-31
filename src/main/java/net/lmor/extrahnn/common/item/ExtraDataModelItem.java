package net.lmor.extrahnn.common.item;

import dev.shadowsoffire.hostilenetworks.Hostile;
import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.data.DataModelRegistry;
import dev.shadowsoffire.hostilenetworks.util.Color;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.tabs.ITabFiller;
import net.lmor.extrahnn.ExtraHostile.Components;
import net.lmor.extrahnn.data.ExtraDataModelInstance;
import net.lmor.extrahnn.data.ExtraModelTier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExtraDataModelItem extends Item implements ITabFiller {
    public ExtraDataModelItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            ExtraDataModelInstance model = new ExtraDataModelInstance(stack, 0);
            if (!model.isValid()) {
                list.add(Component.translatable("extrahnn.info.error", Component.literal("Broke_AF").withStyle(ChatFormatting.OBFUSCATED, ChatFormatting.GRAY)));
                return;
            }

            int data = getData(stack);
            ExtraModelTier tier = model.getTier();
            list.add(Component.translatable("hostilenetworks.info.tier", tier.getComponent()));
            int progress = data - model.getTierData();
            int maxProgress = model.getNextTierData() - model.getTierData();

            if (tier != ExtraModelTier.OMNIPOTENT) {
                list.add(Component.translatable("hostilenetworks.info.data", Component.translatable("hostilenetworks.info.dprog", progress, maxProgress).withStyle(ChatFormatting.GRAY)));
                int dataPerKill = model.getDataPerKill();
                if (dataPerKill == 0) {
                    Component c1 = Component.literal("000 ").withStyle(ChatFormatting.GRAY, ChatFormatting.OBFUSCATED);
                    list.add(Component.translatable("hostilenetworks.info.dpk", new Object[]{c1}).append(Component.translatable("hostilenetworks.info.disabled").withStyle(ChatFormatting.RED)));
                } else {
                    list.add(Component.translatable("hostilenetworks.info.dpk", Component.literal(String.valueOf(model.getDataPerKill())).withStyle(ChatFormatting.GRAY)));
                }
            }

            list.add(Component.translatable("hostilenetworks.info.sim_cost", Component.translatable("hostilenetworks.info.rft", model.simCost()).withStyle(ChatFormatting.GRAY)));
        } else {
            list.add(Component.translatable("hostilenetworks.info.hold_shift", Color.withColor("hostilenetworks.color_text.shift", ChatFormatting.WHITE.getColor())).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public void fillItemCategory(CreativeModeTab creativeModeTab, BuildCreativeModeTabContentsEvent event) {
        List<DynamicHolder<DataModel>> holders = new ArrayList<>();
        ItemStack item = new ItemStack(this);

        DataModelRegistry.INSTANCE.getKeys().stream().sorted().map(DataModelRegistry.INSTANCE::holder).forEach(holders::add);
        event.accept(item);

        setStoredModels(item, holders);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        List<DynamicHolder<DataModel>> models = getStoredModels(stack);
        List<MutableComponent> modelName = new ArrayList<>();

        for (DynamicHolder<DataModel> model: models){
            if (!model.isBound()) {
                modelName = new ArrayList<>(List.of(Component.literal("BROKEN").withStyle(ChatFormatting.OBFUSCATED)));
                break;
            } else {
                modelName.add(Component.empty().append(model.get().name()).withColor(model.get().getNameColor()));
            }
        }

        if (modelName.size() == 1){
            return Component.translatable("item.extrahnn.extra_data_model.broken", modelName.get(0));
        } else {
            return Component.translatable(this.getDescriptionId(stack), modelName.get(0), modelName.get(1), modelName.get(2), modelName.get(3));
        }
    }

    public static DynamicHolder<DataModel> getStoredModel(ItemStack stack) {
        return stack.getOrDefault(Hostile.Components.DATA_MODEL, DataModelRegistry.INSTANCE.emptyHolder());
    }
    public static List<DynamicHolder<DataModel>> getStoredModels(ItemStack stack) {
        DynamicHolder<DataModel> emptyHolder = DataModelRegistry.INSTANCE.emptyHolder();
        List<DynamicHolder<DataModel>> emptyHolders = new ArrayList<>(List.of(emptyHolder, emptyHolder, emptyHolder, emptyHolder));

        return stack.getOrDefault(Components.EXTRA_DATA_MODEL, emptyHolders);
    }

    public static void setStoredModel(ItemStack stack, @NotNull List<DataModel> models) {
        List<DynamicHolder<DataModel>> res = new ArrayList<>();
        for (DataModel model: models){
            res.add(DataModelRegistry.INSTANCE.holder(model));
        }

        setStoredModels(stack, res);
    }

    public static void setStoredModels(ItemStack stack, @NotNull List<DynamicHolder<DataModel>> models) {
        stack.set(Components.EXTRA_DATA_MODEL, models);
    }

    public static int getData(ItemStack stack) {
        return stack.getOrDefault(Hostile.Components.DATA, 0);
    }

    public static void setData(ItemStack stack, int data) {
        stack.set(Hostile.Components.DATA, data);
    }

    public static int getIters(ItemStack stack) {
        return stack.getOrDefault(Hostile.Components.ITERATIONS, 0);
    }

    public static void setIters(ItemStack stack, int iterations) {
        stack.set(Hostile.Components.ITERATIONS, iterations);
    }

    public static boolean matchesModelInput(ItemStack model, ItemStack stack) {
        List<DynamicHolder<DataModel>> models = getStoredModels(model);
        if (models.isEmpty()) return false;

        for (DynamicHolder<DataModel> holder : models) {
            if (!holder.isBound()) continue;
            Ingredient input = holder.get().input();
            if (input.test(stack)) {
                return true;
            }
        }

        return false;
    }
}