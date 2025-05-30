package net.lmor.extrahnn.tile;

import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import dev.shadowsoffire.placebo.cap.ModifiableEnergyStorage;
import dev.shadowsoffire.placebo.menu.SimpleDataSlots;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.lmor.extrahnn.ExtraHostile;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.lmor.extrahnn.api.ISettingCard;
import net.lmor.extrahnn.data.ExtraCachedModel;
import net.lmor.extrahnn.data.ExtraModelTier;
import net.lmor.extrahnn.item.ExtraDataModelItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UltimateSimChamberTileEntity extends BlockEntity implements TickingBlockEntity, SimpleDataSlots.IDataAutoRegister, ISettingCard {
    protected final SimItemHandler inventory = new SimItemHandler();
    protected final ModifiableEnergyStorage energy;
    protected final SimpleDataSlots data;
    protected ExtraCachedModel currentModel;
    protected int runtime;
    protected boolean predictionSuccess;
    protected FailureState failState;
    private boolean checkOutput = true;

    public UltimateSimChamberTileEntity(BlockPos pos, BlockState state) {
        super(ExtraHostile.TileEntities.ULTIMATE_SIM_CHAMBER.get(), pos, state);
        this.energy = new ModifiableEnergyStorage(ExtraHostileConfig.ultimateSimPowerCap, ExtraHostileConfig.ultimateSimPowerCap);

        this.data = new SimpleDataSlots();
        this.currentModel = ExtraCachedModel.EMPTY;
        this.runtime = 0;
        this.predictionSuccess = false;
        this.failState = FailureState.NONE;

        this.data.addData(() -> {
            return this.runtime;
        }, (v) -> {
            this.runtime = v;
        });
        this.data.addData(() -> {
            return this.predictionSuccess;
        }, (v) -> {
            this.predictionSuccess = v;
        });
        this.data.addData(() -> {
            return this.failState.ordinal();
        }, (v) -> {
            this.failState = FailureState.values()[v];
        });
        this.data.addEnergy(this.energy);
    }

    public void registerSlots(Consumer<DataSlot> consumer) {
        this.data.register(consumer);
    }

    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", this.inventory.serializeNBT());
        tag.putInt("energy", this.energy.getEnergyStored());

        tag.putInt("runtime", this.runtime);
        tag.putBoolean("predSuccess", this.predictionSuccess);
        tag.putInt("failState", this.failState.ordinal());
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.energy.setEnergy(tag.getInt("energy"));

        ItemStack model = this.inventory.getStackInSlot(0);
        if (!model.isEmpty()){
            this.currentModel = new ExtraCachedModel(model, 0);
        }
        this.runtime = tag.getInt("runtime");
        this.predictionSuccess = tag.getBoolean("predSuccess");
        this.failState = FailureState.values()[tag.getInt("failState")];
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (this.inventory.getStackInSlot(0).isEmpty()) {
            this.failState = FailureState.MODEL;
            this.runtime = 0;
            return;
        }
        else if (this.inventory.getStackInSlot(1).isEmpty() && this.runtime == 0){
            this.failState = FailureState.INPUT;
            return;
        }

        ItemStack model = this.inventory.getStackInSlot(0);
        if (!model.isEmpty()) {
            ExtraCachedModel oldModel = this.currentModel;
            this.currentModel = this.getOrLoadModel(model);
            if (oldModel != this.currentModel) {
                this.runtime = 0;
            }

            if (this.currentModel.isValid()) {
                if (this.runtime == 0) {
                    if (this.canStartSimulation()) {
                        this.runtime = ExtraHostileConfig.ultimateSimPowerDuration;
                        this.predictionSuccess = this.level.random.nextFloat() <= this.currentModel.getAccuracy();
                        this.inventory.getStackInSlot(1).shrink(4);
                        this.setChanged();
                    }
                } else if (this.energy.getEnergyStored() >= this.currentModel.simCost()) {
                    this.failState = FailureState.NONE;
                    if (--this.runtime == 0) {
                        setResult(model);
                    } else {
                        this.energy.setEnergy(this.energy.getEnergyStored() - this.currentModel.simCost());
                        this.setChanged();
                    }
                } else {
                    this.failState = FailureState.ENERGY_MID_CYCLE;
                }
                return;
            }
        }

        this.failState = FailureState.MODEL;
        this.runtime = 0;
    }

    public void setResult(ItemStack itemModel) {
        List<ItemStack> predicateDrop = new ArrayList<>();
        List<ItemStack> baseDrop = new ArrayList<>();
        for (DynamicHolder<DataModel> model: this.currentModel.getModels()){
            predicateDrop.add(model.get().getPredictionDrop());
            baseDrop.add(model.get().baseDrop());
        }

        setItem(baseDrop, 2, 6);
        if (this.predictionSuccess) setItem(predicateDrop, 6, 10);

        ExtraModelTier tier = this.currentModel.getTier();
        if (tier != tier.next()) {
            int newData = this.currentModel.getData() + this.currentModel.getDataPerKill();
            if (newData <= this.currentModel.getNextTierData()) {
                this.currentModel.setData(newData);
            }
        }
        ExtraDataModelItem.setIters(itemModel, ExtraDataModelItem.getIters(itemModel) + 1);
        this.setChanged();
    }

    public boolean canStartSimulation() {
        if (!checkOutput && this.failState == FailureState.OUTPUT) return false;

        if (this.inventory.getStackInSlot(1).getCount() < 4){
            this.failState = FailureState.COUNT;
            return false;
        } else if (this.energy.getEnergyStored() < this.currentModel.simCost()){
            this.failState = FailureState.ENERGY;
            return false;
        } else {
            checkOutput = false;

            ItemStack[] baseDropSlots = new ItemStack[4];
            ItemStack[] basePredicateSlots = new ItemStack[4];
            for (int i = 2; i < 6; i++) {
                baseDropSlots[i - 2] = inventory.getStackInSlot(i).copy();
                basePredicateSlots[i - 2] = inventory.getStackInSlot(i + 4).copy();
            }

            List<ItemStack> predicateDrop = new ArrayList<>();
            List<ItemStack> baseDrop = new ArrayList<>();
            for (DynamicHolder<DataModel> model: this.currentModel.getModels()){
                predicateDrop.add(model.get().getPredictionDrop());
                baseDrop.add(model.get().baseDrop());
            }

            boolean dropSet = setItemEmulate(baseDropSlots, baseDrop);
            boolean predicateSet = setItemEmulate(basePredicateSlots, predicateDrop);
            if (predicateSet && dropSet){
                this.failState = FailureState.NONE;
                return true;
            } else {
                this.failState = FailureState.OUTPUT;
                return false;
            }
        }
    }

    public boolean setItemEmulate(ItemStack[] slots, List<ItemStack> itemsToAdd){
        for (ItemStack item : itemsToAdd) {
            int remainingCount = item.getCount();

            for (int i = 0; i < slots.length; i++) {
                if (!slots[i].isEmpty() && canStack(slots[i], item)) {
                    int spaceLeft = slots[i].getMaxStackSize() - slots[i].getCount();
                    if (spaceLeft > 0) {
                        int toAdd = Math.min(spaceLeft, remainingCount);
                        slots[i].grow(toAdd);
                        remainingCount -= toAdd;

                        if (remainingCount <= 0) {
                            break;
                        }
                    }
                }
            }

            if (remainingCount > 0) {
                for (int i = 0; i < slots.length; i++) {
                    if (slots[i].isEmpty()) {
                        int toPlace = Math.min(item.getMaxStackSize(), remainingCount);
                        slots[i] = item.copy();
                        remainingCount -= toPlace;

                        if (remainingCount <= 0) {
                            break;
                        }
                    }
                }
            }

            if (remainingCount > 0) {
                return false;
            }
        }

        return true;
    }

    public void setItem(List<ItemStack> itemsToAdd, int startSlot, int endSlot){
        for (ItemStack item : itemsToAdd) {
            int remainingCount = item.getCount();
            for (int i = startSlot; i < endSlot; i++) {
                ItemStack slot = inventory.getStackInSlot(i);

                if (!slot.isEmpty() && canStack(slot, item)){
                    int spaceLeft = slot.getMaxStackSize() - slot.getCount();
                    if (spaceLeft > 0) {
                        int toAdd = Math.min(spaceLeft, remainingCount);
                        slot.grow(toAdd);
                        remainingCount -= toAdd;

                        if (remainingCount <= 0) {
                            break;
                        }
                    }
                }
            }

            if (remainingCount > 0) {
                for (int i = startSlot; i < endSlot; i++) {
                    ItemStack slot = inventory.getStackInSlot(i);

                    if (slot.isEmpty()) {
                        int toPlace = Math.min(item.getMaxStackSize(), remainingCount);
                        ItemStack newItem = item.copy();
                        newItem.setCount(toPlace);
                        inventory.setStackInSlot(i, newItem);
                        remainingCount -= toPlace;

                        if (remainingCount <= 0) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public boolean canStack(ItemStack a, ItemStack b) {
        if (a.isEmpty()) {
            return true;
        } else {
            return ItemStack.isSameItemSameTags(a, b) && a.getCount() < a.getMaxStackSize();
        }
    }

    protected ExtraCachedModel getOrLoadModel(ItemStack stack) {
        return this.currentModel.getSourceStack() == stack ? this.currentModel : new ExtraCachedModel(stack, 0);
    }

    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.of(() -> {
                return this.inventory;
            }).cast();
        } else {
            return cap == ForgeCapabilities.ENERGY ? LazyOptional.of(() -> {
                return this.energy;
            }).cast() : super.getCapability(cap, side);
        }
    }

    public SimItemHandler getInventory() {
        return this.inventory;
    }

    public int getEnergyStored() {
        return this.energy.getEnergyStored();
    }

    public int getRuntime() {
        return this.runtime;
    }

    public boolean didPredictionSucceed() {
        return this.predictionSuccess;
    }

    public FailureState getFailState() {
        return this.failState;
    }

    @Override
    public CompoundTag saveSetting() {
        CompoundTag tag = new CompoundTag();

        ItemStack item = this.inventory.getStackInSlot(0);
        if (!item.isEmpty()){
            CompoundTag saveTag = new CompoundTag();
            item.save(saveTag);

            tag.put("dataModel", saveTag);
        }
        tag.putString("config", "UltimateSimChamberTileEntity");
        return tag;
    }

    @Override
    public boolean loadSetting(CompoundTag tag, Player player) {
        if (!tag.contains("config") || !tag.getString("config").equals("UltimateSimChamberTileEntity")) return false;
        if (!tag.contains("dataModel")) return false;

        CompoundTag saveTag = tag.getCompound("dataModel");
        Inventory playerInv = player.getInventory();

        ItemStack item = ItemStack.of(saveTag);
        int inventoryIndexItem = playerInv.findSlotMatchingItem(item);
        ItemStack machineItem = this.inventory.getStackInSlot(0);

        if (machineItem == item) return false;

        if (!item.isEmpty() && inventoryIndexItem != -1){
            playerInv.removeItem(inventoryIndexItem, 1);
            if (!this.inventory.getStackInSlot(0).isEmpty()){
                playerInv.add(this.inventory.getStackInSlot(0));
                this.inventory.setStackInSlot(0, ItemStack.EMPTY);
            }

            this.inventory.setStackInSlot(0, item);
        }

        return true;
    }

    public class SimItemHandler extends InternalItemHandler {
        public SimItemHandler() {
            super(10);
        }

        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0) {
                return stack.getItem() instanceof ExtraDataModelItem;
            } else {
                return slot != 1 || ExtraDataModelItem.matchesInput(this.getStackInSlot(0), stack);
            }
        }

        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return slot >= 2 ? stack : super.insertItem(slot, stack, simulate);
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate) {

            return slot <= 1 ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
        }

        protected void onContentsChanged(int slot) {
            checkOutput = true;
            UltimateSimChamberTileEntity.this.setChanged();
        }
    }

    public static enum FailureState {
        NONE("hostilenetworks.fail.none"),
        OUTPUT("hostilenetworks.fail.output"),
        ENERGY("hostilenetworks.fail.energy"),
        INPUT("hostilenetworks.fail.input"),
        MODEL("hostilenetworks.fail.model"),
        ENERGY_MID_CYCLE("hostilenetworks.fail.energy_mid_cycle"),
        COUNT("extrahnn.fail.count");

        private final String id;

        private FailureState(String id) {
            this.id = id;
        }

        public String getKey() {
            return id;
        }
    }
}
