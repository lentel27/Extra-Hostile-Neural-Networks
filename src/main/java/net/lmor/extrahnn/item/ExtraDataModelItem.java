package net.lmor.extrahnn.item;

import net.lmor.extrahnn.client.ExtraDataModelItemStackRenderer;
import net.lmor.extrahnn.data.ExtraCachedModel;
import net.lmor.extrahnn.data.ExtraModelTier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.NonNullList;
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
import shadows.hostilenetworks.data.DataModel;
import shadows.hostilenetworks.data.DataModelManager;
import shadows.hostilenetworks.util.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ExtraDataModelItem extends Item {
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
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> output) {
        List<ResourceLocation> resourceLocationsList = new ArrayList<>();
        ItemStack item = new ItemStack(this);

        DataModelManager.INSTANCE.getKeys().stream().sorted().forEach((key) -> {
            resourceLocationsList.add(key);
            output.add(item);
        });
        setStoredResources(item, resourceLocationsList);
    }

    @Override
    public Component getName(ItemStack pStack) {
        List<DataModel> models = getStoredModels(pStack);
        List<MutableComponent> modelName = new ArrayList<>();

        for (DataModel model: models){
            if (model == null) {
                modelName = new ArrayList<>(List.of(Component.literal("BROKEN").withStyle(ChatFormatting.OBFUSCATED)));
                break;
            } else {
                modelName.add(model.getName());
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

    public static List<DataModel> getStoredModels(ItemStack stack) {
        List<DataModel> holders = new ArrayList<>();

        if (stack.isEmpty()) {
            return holders;
        }

        CompoundTag dataModelTag = stack.getTagElement("data_model");
        if (dataModelTag == null) {
            return holders;
        }

        ListTag idList = dataModelTag.getList("ids", 8);
        for (int i = 0; i < idList.size(); i++) {
            String id = idList.getString(i);
            holders.add(DataModelManager.INSTANCE.getValue(new ResourceLocation(id)));

        }
        return holders;
    }

    public static void setStoredModels(ItemStack stack, List<DataModel> models) {
        List<ResourceLocation> resourceLocationsList = new ArrayList<>();
        for (DataModel model: models){
            resourceLocationsList.add(Objects.requireNonNull(DataModelManager.INSTANCE.getValue(model.getId())).getId());
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
        List<DataModel> models = getStoredModels(model);

        for (DataModel dm: models){
            if (dm == null){
                return false;
            } else {
                ItemStack input = dm.getInput();
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
