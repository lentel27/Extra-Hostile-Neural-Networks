package net.lmor.extrahnn.item;

import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.data.DataModelRegistry;
import dev.shadowsoffire.hostilenetworks.util.Color;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.tabs.ITabFiller;
import net.lmor.extrahnn.client.ExtraDataModelItemStackRenderer;
import net.lmor.extrahnn.data.ExtraCachedModel;
import net.lmor.extrahnn.data.ExtraModelTier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class ExtraDataModelItem extends Item implements ITabFiller {
    public ExtraDataModelItem(Properties properties) {
        super(properties);
    }

    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> list, TooltipFlag pFlag) {
        if (Screen.hasShiftDown()) {
            ExtraCachedModel model = new ExtraCachedModel(pStack, 0);
            if (!model.isValid()) {
                list.add(Component.translatable("Error: %s", Component.literal("Broke_AF").withStyle(ChatFormatting.OBFUSCATED, ChatFormatting.GRAY)));
                return;
            }

            int data = getData(pStack);
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
    public void fillItemCategory(CreativeModeTab tab, CreativeModeTab.Output output) {
        List<ResourceLocation> resourceLocationsList = new ArrayList<>();
        ItemStack item = new ItemStack(this);

        DataModelRegistry.INSTANCE.getKeys().stream().sorted().forEach((key) -> {
            resourceLocationsList.add(key);
            output.accept(item);
        });
        setStoredResources(item, resourceLocationsList);
    }

    @Override
    public Component getName(ItemStack pStack) {
        List<DynamicHolder<DataModel>> models = getStoredModels(pStack);
        List<MutableComponent> modelName = new ArrayList<>();

        for (DynamicHolder<DataModel> model: models){
            if (!model.isBound()) {
                modelName = new ArrayList<>(List.of(Component.literal("BROKEN").withStyle(ChatFormatting.OBFUSCATED)));
                break;
            } else {
                modelName.add(model.get().name());
            }
        }

        if (modelName.size() == 1){
            return Component.translatable("item.extrahnn.extra_data_model.broken", modelName.get(0));
        } else {
            return Component.translatable(this.getDescriptionId(pStack), modelName.get(0), modelName.get(1), modelName.get(2), modelName.get(3));
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            ExtraDataModelItemStackRenderer dmisr = new ExtraDataModelItemStackRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.dmisr;
            }
        });
    }

    public static List<DynamicHolder<DataModel>> getStoredModels(ItemStack stack) {
        DynamicHolder<DataModel> emptyHolder = DataModelRegistry.INSTANCE.emptyHolder();
        List<DynamicHolder<DataModel>> holders = new ArrayList<>(List.of(emptyHolder, emptyHolder, emptyHolder, emptyHolder));

        if (stack.isEmpty()) {
            return holders;
        }

        CompoundTag dataModelTag = stack.getTagElement("data_model");
        if (dataModelTag == null) {
            return holders;
        }

        ListTag idList = dataModelTag.getList("ids", 8);

        holders = new ArrayList<>();
        for (int i = 0; i < idList.size(); i++) {
            String id = idList.getString(i);
            holders.add(DataModelRegistry.INSTANCE.holder(new ResourceLocation(id)));

        }
        return holders;
    }

    public static void setStoredModels(ItemStack stack, List<DataModel> models) {
        List<ResourceLocation> resourceLocationsList = new ArrayList<>();
        for (DataModel model: models){
            resourceLocationsList.add(DataModelRegistry.INSTANCE.getKey(model));
        }
        setStoredResources(stack, resourceLocationsList);
    }

    public static void setStoredResources(ItemStack stack, List<ResourceLocation> models) {
        stack.removeTagKey("data_model");

        CompoundTag dataModelTag = stack.getOrCreateTagElement("data_model");
        ListTag idList = dataModelTag.getList("models", 8);
        for (ResourceLocation model: models){
            idList.add(StringTag.valueOf(model.toString()));
        }
        stack.getOrCreateTagElement("data_model").put("ids", idList);
    }

    public static int getData(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("data_model");
        return !stack.isEmpty() && tag != null ? tag.getInt("data") : 0;
    }

    public static void setData(ItemStack stack, int data) {
        stack.getOrCreateTagElement("data_model").putInt("data", data);
    }

    public static int getIters(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("data_model");
        return !stack.isEmpty() && tag != null ? tag.getInt("iterations") : 0;
    }

    public static void setIters(ItemStack stack, int data) {
        stack.getOrCreateTagElement("data_model").putInt("iterations", data);
    }

    public static boolean matchesInput(ItemStack model, ItemStack stack) {
        List<DynamicHolder<DataModel>> models = getStoredModels(model);

        for (DynamicHolder<DataModel> dm: models){
            if (!dm.isBound()){
                return false;
            } else {
                ItemStack input = dm.get().input();
                boolean inputItem = input.getItem() == stack.getItem();
                if (input.hasTag()){
                    if (stack.hasTag()){
                        CompoundTag tag1 = input.getTag();
                        CompoundTag tag2 = stack.getTag();
                        Iterator<String> iterator = tag1.getAllKeys().iterator();

                        String s;
                        do {
                            if (!iterator.hasNext()) {
                                return true;
                            }
                            s = iterator.next();
                        } while (tag1.get(s).equals(tag2.get(s)));
                    }
                } else {
                    if (inputItem) return true;
                }
            }
        }
        return false;
    }
}
