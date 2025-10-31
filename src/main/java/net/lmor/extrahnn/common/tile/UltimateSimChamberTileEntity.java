package net.lmor.extrahnn.common.tile;

import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.tile.SimChamberTileEntity.RedstoneState;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import dev.shadowsoffire.placebo.cap.ModifiableEnergyStorage;
import dev.shadowsoffire.placebo.menu.SimpleDataSlots;
import dev.shadowsoffire.placebo.menu.SimpleDataSlots.IDataAutoRegister;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.lmor.extrahnn.api.IRegTile;
import net.lmor.extrahnn.api.ISettingCard;
import net.lmor.extrahnn.api.Version;
import net.lmor.extrahnn.common.item.ExtraDataModelItem;
import net.lmor.extrahnn.data.ExtraDataModelInstance;
import net.lmor.extrahnn.data.ExtraModelTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class UltimateSimChamberTileEntity extends BlockEntity implements TickingBlockEntity, IDataAutoRegister, IRegTile, ISettingCard {

    protected final SimItemHandler inventory = new SimItemHandler();
    protected final ModifiableEnergyStorage energy = new ModifiableEnergyStorage(ExtraHostileConfig.ultimateSimPowerCap, ExtraHostileConfig.ultimateSimPowerCap);
    protected final SimpleDataSlots data = new SimpleDataSlots();

    protected ExtraDataModelInstance currentModel = ExtraDataModelInstance.EMPTY;
    protected int runtime = 0;
    protected int predictionSuccess = 0;
    protected FailureState failState = FailureState.NONE;
    protected RedstoneState redstoneState = RedstoneState.IGNORED;

    private boolean checkOutput = true;

    private Version version;

    public UltimateSimChamberTileEntity(BlockPos pos, BlockState state, BlockEntityType<UltimateSimChamberTileEntity> type, Version version) {
        super(type, pos, state);
        this.version = version;

        this.data.addData(() -> this.runtime, v -> this.runtime = v);
        this.data.addData(() -> this.predictionSuccess, v -> this.predictionSuccess = v);
        this.data.addData(() -> this.failState.ordinal(), v -> this.failState = FailureState.values()[v]);
        this.data.addData(() -> this.redstoneState.ordinal(), v -> this.redstoneState = RedstoneState.values()[v]);
        this.data.addEnergy(this.energy);
        this.energy.setMaxExtract(0);
    }

    @Override
    public void registerSlots(Consumer<DataSlot> consumer) {
        this.data.register(consumer);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider regs) {
        super.saveAdditional(tag, regs);
        tag.put("inventory", this.inventory.serializeNBT(regs));
        tag.putInt("energy", this.energy.getEnergyStored());

        tag.putInt("runtime", this.runtime);
        tag.putInt("predSuccess", this.predictionSuccess);
        tag.putInt("failState", this.failState.ordinal());
        tag.putInt("redstoneState", this.redstoneState.ordinal());

        tag.putString("versionBlockEntity", this.version.getId());
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider regs) {
        super.loadAdditional(tag, regs);
        this.inventory.deserializeNBT(regs, tag.getCompound("inventory"));
        this.energy.setEnergy(tag.getInt("energy"));

        ItemStack model = this.inventory.getStackInSlot(0);
        ExtraDataModelInstance cModel = this.getOrLoadModel(model);
        if (cModel.isValid() && !model.isEmpty()){
            this.currentModel = new ExtraDataModelInstance(model, 0);
        }

        this.runtime = tag.getInt("runtime");
        this.predictionSuccess = tag.getInt("predSuccess");
        this.failState = FailureState.values()[tag.getInt("failState")];
        this.redstoneState = RedstoneState.values()[tag.getInt("redstoneState")];

        this.version = Version.getVersion(tag.getString("versionBlockEntity"));
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (this.inventory.getStackInSlot(0).isEmpty()) {
            this.failState = FailureState.MODEL;
            this.runtime = 0;
            return;
        } else if (this.inventory.getStackInSlot(1).isEmpty() && this.runtime == 0){
            this.failState = FailureState.INPUT;
            return;
        }

        ItemStack model = this.inventory.getStackInSlot(0);
        if (!model.isEmpty()) {
            ExtraDataModelInstance oldModel = this.currentModel;
            this.currentModel = this.getOrLoadModel(model);
            if (oldModel != this.currentModel) {
                this.runtime = 0;
            }

            if (this.currentModel.isValid()) {
                if (!this.currentModel.getTier().data().canSim()) {
                    this.failState = FailureState.FAULTY;
                    this.runtime = 0;
                    return;
                }

                if (this.runtime == 0) {
                    if (this.canStartSimulation()) {
                        this.runtime = ExtraHostileConfig.ultimateSimPowerDuration;

                        float accuracy = this.currentModel.getAccuracy();
                        this.predictionSuccess = (int) accuracy + (Objects.requireNonNull(this.level).random.nextFloat() <= this.currentModel.getAccuracy() % 1 ? 1 : 0);
                        this.inventory.getStackInSlot(1).shrink(version.getMultiplier() * 4);
                        this.setChanged();
                    }
                } else if (this.hasPowerFor(this.currentModel)) {
                    if (this.getRedstoneState().matches(level.hasNeighborSignal(worldPosition))) {
                        this.failState = FailureState.NONE;
                        if (--this.runtime == 0) setResult(model);
                        else {
                            this.energy.setEnergy(this.energy.getEnergyStored() - this.currentModel.simCost());
                            this.setChanged();
                        }
                    } else this.failState = FailureState.REDSTONE;
                }
                else this.failState = FailureState.ENERGY_MID_CYCLE;
                return;
            }
        }
        this.failState = FailureState.MODEL;
        this.runtime = 0;
    }

    public boolean canStartSimulation() {
        if (!checkOutput && this.failState == FailureState.OUTPUT) return false;

        if (!this.redstoneState.matches(Objects.requireNonNull(this.level).hasNeighborSignal(this.worldPosition))) {
            this.failState = FailureState.REDSTONE;
            return false;
        }

        if (this.inventory.getStackInSlot(1).getCount() < 4 * version.getMultiplier()){
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

    public void setResult(ItemStack itemModel) {
        List<ItemStack> predicateDrop = new ArrayList<>();
        List<ItemStack> baseDrop = new ArrayList<>();
        for (DynamicHolder<DataModel> model: this.currentModel.getModels()){
            var predDrop = model.get().getPredictionDrop();
            predDrop.setCount(version.getMultiplier());
            predicateDrop.add(predDrop);

            var base = model.get().baseDrop();
            base.setCount(version.getMultiplier());
            baseDrop.add(base);
        }

        setItem(baseDrop, 2, 6);
        if (isPredictionSucceed()) setItem(predicateDrop, 6, 10);

        ExtraModelTier tier = this.currentModel.getTier();
        if (!tier.isMax()) {
            int newData = this.currentModel.getData() + this.currentModel.getDataPerKill();
            if (newData <= this.currentModel.getNextTierData()) {
                this.currentModel.setData(newData);
            }
        }
        ExtraDataModelItem.setIters(itemModel, ExtraDataModelItem.getIters(itemModel) + 1);
        this.setChanged();
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

    public boolean setItemEmulate(ItemStack[] slots, List<ItemStack> itemsToAdd){
        for (ItemStack item : itemsToAdd) {
            int remainingCount = item.getCount();

            for (ItemStack slot : slots) {
                if (!slot.isEmpty() && canStack(slot, item)) {
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

    public boolean canStack(ItemStack a, ItemStack b) {
        if (a.isEmpty()) return true;
        return ItemStack.isSameItemSameComponents(a, b) && a.getCount() < a.getMaxStackSize();
    }

    public boolean hasPowerFor(ExtraDataModelInstance model) {
        return this.energy.getEnergyStored() >= model.simCost();
    }

    protected ExtraDataModelInstance getOrLoadModel(ItemStack stack) {
        return this.currentModel.getSourceStack() == stack ? this.currentModel : new ExtraDataModelInstance(stack, 0);
    }

    public SimItemHandler getInventory() {
        return this.inventory;
    }

    public IEnergyStorage getEnergy() {
        return energy;
    }

    public int getEnergyStored() {
        return this.energy.getEnergyStored();
    }

    public int getRuntime() {
        return this.runtime;
    }

    public boolean isPredictionSucceed() {
        return this.predictionSuccess > 0;
    }

    public FailureState getFailState() {
        return this.failState;
    }

    public void setRedstoneState(RedstoneState state) {
        this.redstoneState = state;
    }

    public RedstoneState getRedstoneState() {
        return this.redstoneState;
    }

    @Override
    public CompoundTag saveSetting(HolderLookup.Provider regs) {
        CompoundTag tag = new CompoundTag();

        ItemStack item = this.inventory.getStackInSlot(0);
        if (!item.isEmpty()){
            CompoundTag saveTag = new CompoundTag();
            tag.put("dataModel", item.save(regs, saveTag));
        }
        tag.putString("config", "UltimateSimChamberTileEntity");
        return tag;
    }

    @Override
    public boolean loadSetting(HolderLookup.@NotNull Provider regs, CompoundTag tag, Player player) {
        if (!tag.contains("config") || !tag.getString("config").equals("UltimateSimChamberTileEntity")) return false;
        if (!tag.contains("dataModel")) return false;

        CompoundTag saveTag = tag.getCompound("dataModel");
        Inventory playerInv = player.getInventory();

        ItemStack item = ItemStack.parseOptional(regs, saveTag);
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

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0) return stack.getItem() instanceof ExtraDataModelItem;
            return slot != 1 || ExtraDataModelItem.matchesModelInput(this.getStackInSlot(0), stack);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return slot >= 2 ? stack : super.insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot <= 1 ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            checkOutput = true;
            UltimateSimChamberTileEntity.this.setChanged();
        }

        public NonNullList<ItemStack> getItems() {
            return this.stacks;
        }
    }

    public enum FailureState {
        NONE("hostilenetworks.fail.none"),
        OUTPUT("hostilenetworks.fail.output"),
        ENERGY("hostilenetworks.fail.energy"),
        INPUT("hostilenetworks.fail.input"),
        MODEL("hostilenetworks.fail.model"),
        FAULTY("hostilenetworks.fail.faulty"),
        ENERGY_MID_CYCLE("hostilenetworks.fail.energy_mid_cycle"),
        COUNT("extrahnn.fail.count"),
        REDSTONE("hostilenetworks.fail.redstone");

        private final String id;

        FailureState(String id) {
            this.id = id;
        }

        public String getKey() {
            return id;
        }
    }
}
