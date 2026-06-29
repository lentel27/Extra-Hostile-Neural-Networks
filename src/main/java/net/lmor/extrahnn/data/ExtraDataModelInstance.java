package net.lmor.extrahnn.data;

import dev.shadowsoffire.hostilenetworks.HostileConfig;
import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.data.DataModelRegistry;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import lombok.Getter;
import net.lmor.extrahnn.common.item.ExtraDataModelItem;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ExtraDataModelInstance implements TooltipComponent {

    public static final ExtraDataModelInstance EMPTY = new ExtraDataModelInstance(ItemStack.EMPTY);

    protected final ItemStack stack;
    @Getter
    protected List<DynamicHolder<DataModel>> models;

    @Getter
    protected int data;
    @Getter
    protected ExtraModelTier tier;

    public ExtraDataModelInstance(ItemStack stack) {
        this.stack = stack;
        this.models = ExtraDataModelItem.getStoredModels(stack);
        this.data = ExtraDataModelItem.getData(stack);
        this.tier = ExtraModelTier.getByData(this.data);
    }

    public int rollAllPredictions(RandomSource random){
        float accuracy = this.getAccuracy();
        int floor = (int) accuracy;
        float frac = accuracy - floor;
        return floor + (random.nextFloat() < frac ? 1 : 0);
    }

    public int simCost() {
        int cost = 0;
        for (DynamicHolder<DataModel> model: models){
            if (model.isBound()){
                cost = cost + model.get().simCost();
            }
        }
        return cost;
    }

    public int getDataPerKill() {
        return this.tier.data().dataPerKill();
    }

    public int getTierData() {
        return this.tier.data().requiredData();
    }

    public int getNextDataPerKill() {
        return this.tier.next().data().dataPerKill();
    }

    public int getNextTierData() {
        return this.tier.next().data().requiredData();
    }

    public void setData(int data) {
        this.data = data;
        if (this.data >= this.getNextTierData()) this.tier = this.tier.next();

        ExtraDataModelItem.setData(this.stack, data);
    }

    public float getAccuracy() {
        if (!HostileConfig.continuousAccuracy || this.getTier().isMax()){
            return this.getTier().accuracy();
        }

        ExtraModelTier next = this.getNextTier();
        int diff = this.getNextTierData() - this.getTierData();
        float tDiff = next.accuracy() - this.getTier().accuracy();
        return this.getTier().accuracy() + tDiff * (float)(diff - (this.getNextTierData() - this.data)) / (float)diff;
    }


    public ExtraModelTier getNextTier() {
        return getTier().next();
    }

    public ItemStack getSourceStack() {
        return this.stack;
    }

    public boolean isValid() {
        for (DynamicHolder<DataModel> model: this.models){
            if (!model.isBound() || model.is(DataModelRegistry.INSTANCE.emptyHolder().getId())) return false;
        }
        return this.models.size() == 4;
    }
}