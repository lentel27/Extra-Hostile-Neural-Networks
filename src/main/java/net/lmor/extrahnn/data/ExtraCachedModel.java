package net.lmor.extrahnn.data;

import shadows.hostilenetworks.data.DataModel;
import net.lmor.extrahnn.item.ExtraDataModelItem;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ExtraCachedModel {
    public static final ExtraCachedModel EMPTY;
    protected final ItemStack stack;
    protected final int slot;
    protected List<DataModel> models;
    protected int data;
    protected ExtraModelTier tier;

    public ExtraCachedModel(ItemStack stack, int slot) {
        this.stack = stack;
        this.slot = slot;
        this.models = ExtraDataModelItem.getStoredModels(stack);
        this.data = ExtraDataModelItem.getData(stack);
        this.tier = ExtraModelTier.getByData(this.data);
    }

    public int getData() {
        return this.data;
    }

    public List<DataModel> getModels(){
        return this.models;
    }

    public void setModels(DataModel model, int slot){
        if (slot >= this.models.size()){
            this.models.add(model);
        }
        else this.models.set(slot, model);

    }

    public int simCost() {
        int cost = 0;
        for (DataModel model: models){
            if (model != null){
                cost = cost + model.getSimCost();
            }
        }
        return cost;
    }

    public ExtraModelTier getTier() {
        return this.tier;
    }

    public int getDataPerKill() {
        return this.tier.data().dataPerKill();
    }

    public int getTierData() {
        return this.tier.data().requiredData();
    }

    public int getNextDataPerKill() {
        return (this.tier.next()).data().dataPerKill();
    }

    public int getNextTierData() {
        return (this.tier.next()).data().requiredData();
    }

    public void setData(int data) {
        this.data = data;
        if (this.data >= this.getNextTierData()) {
            this.tier = this.tier.next();
        }

        ExtraDataModelItem.setData(this.stack, data);
    }

    public int getSlot() {
        return this.slot;
    }

    public float getAccuracy() {
        ExtraModelTier next = this.tier.next();
        if (this.tier == next) {
            return next.accuracy();
        } else {
            int diff = this.getNextTierData() - this.getTierData();
            float tDiff = next.accuracy() - this.tier.accuracy();
            return this.tier.accuracy() + tDiff * (float)(diff - (this.getNextTierData() - this.data)) / (float)diff;
        }
    }

    public List<ItemStack> getPredictionDrop() {
        List<ItemStack> stacks = new ArrayList<>();
        for (DataModel model: this.models){
            stacks.add(model.getPredictionDrop());
        }

        return stacks;
    }

    public ItemStack getSourceStack() {
        return this.stack;
    }

    public boolean isValid() {
        for (DataModel model: this.models){
            if (model == null) return false;
        }
        return true;
    }

    static {
        EMPTY = new ExtraCachedModel(ItemStack.EMPTY, -1);
    }
}
