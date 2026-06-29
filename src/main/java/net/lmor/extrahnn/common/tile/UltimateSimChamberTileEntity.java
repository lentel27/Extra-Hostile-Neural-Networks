package net.lmor.extrahnn.common.tile;

import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.util.RedstoneState;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import dev.shadowsoffire.placebo.cap.ModifiableEnergyStorage;
import dev.shadowsoffire.placebo.menu.SimpleDataSlots;
import dev.shadowsoffire.placebo.menu.SimpleDataSlots.IDataAutoRegister;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import lombok.Getter;
import lombok.Setter;
import net.lmor.extrahnn.ExtraHostile;
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
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class UltimateSimChamberTileEntity extends BlockEntity implements TickingBlockEntity, IDataAutoRegister, IRegTile, ISettingCard {

    @Getter
    protected final SimItemHandler inventory = new SimItemHandler();
    @Getter
    protected final ModifiableEnergyStorage energy;
    @Getter
    protected final SimpleDataSlots data = new SimpleDataSlots();

    @Getter
    protected int runtime = 0;
    @Getter
    protected int predictionSuccess = 0;
    @Getter
    protected FailureState failState = FailureState.NONE;
    @Getter @Setter
    protected RedstoneState redstoneState = RedstoneState.IGNORED;

    protected ExtraDataModelInstance currentModel = ExtraDataModelInstance.EMPTY;
    private boolean checkOutput = true;

    private Version version;

    public int CAP;
    public int DURATION;

    public boolean extractDataModel = false;

    public UltimateSimChamberTileEntity(BlockPos pos, BlockState state, BlockEntityType<UltimateSimChamberTileEntity> type, Version version) {
        super(type, pos, state);
        this.version = version;

        setConfig();
        energy = new ModifiableEnergyStorage(CAP, CAP);

        this.data.addData(() -> this.runtime, v -> this.runtime = v);
        this.data.addData(() -> this.predictionSuccess, v -> this.predictionSuccess = v);
        this.data.addData(() -> this.failState.ordinal(), v -> this.failState = FailureState.values()[v]);
        this.data.addData(() -> this.redstoneState.ordinal(), v -> this.redstoneState = RedstoneState.values()[v]);
        this.data.addData(() -> this.extractDataModel, v -> this.extractDataModel = v);
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

        tag.putBoolean("extractModel", extractDataModel);
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
            this.currentModel = new ExtraDataModelInstance(model);
        }

        this.runtime = tag.getInt("runtime");
        this.predictionSuccess = tag.getInt("predSuccess");
        this.failState = FailureState.values()[tag.getInt("failState")];
        this.redstoneState = RedstoneState.values()[tag.getInt("redstoneState")];

        this.extractDataModel = tag.getBoolean("extractModel");

        this.version = Version.getVersion(tag.getString("versionBlockEntity"));
    }

    @Override
    public CompoundTag saveSetting(HolderLookup.Provider regs) {
        CompoundTag tag = new CompoundTag();

        ItemStack item = this.inventory.getStackInSlot(0);
        if (!item.isEmpty()){
            CompoundTag saveTag = new CompoundTag();
            tag.put("dataModel", item.save(regs, saveTag));
        }
        tag.putString("config", getClass().getName());
        tag.putInt("redstoneState", this.redstoneState.ordinal());
        tag.putBoolean("extractDataModel", extractDataModel);
        return tag;
    }

    @Override
    public boolean loadSetting(HolderLookup.@NotNull Provider regs, CompoundTag tag, Player player) {
        if (!tag.contains("config") || !tag.getString("config").equals(getClass().getName())) return false;

        this.redstoneState = RedstoneState.values()[tag.getInt("redstoneState")];
        this.extractDataModel = tag.getBoolean("extractDataModel");

        CompoundTag saveTag = tag.getCompound("dataModel");
        Inventory playerInv = player.getInventory();

        ItemStack item = ItemStack.parseOptional(regs, saveTag);
        if (item.isEmpty()) return false;
        setModelInv(item, playerInv);
        return true;
    }

    private void setModelInv(ItemStack findItem, Inventory playerInv){
        List<DynamicHolder<DataModel>> findModels = findItem.getOrDefault(ExtraHostile.Components.EXTRA_DATA_MODEL, List.of());
        if (findModels.isEmpty()) return;

        for(int i = 0; i < playerInv.items.size(); ++i) {
            ItemStack itemInv = playerInv.items.get(i).copy();
            if (itemInv.isEmpty() || !itemInv.is(findItem.getItem()) || !(itemInv.getItem() instanceof ExtraDataModelItem)) continue;

            List<DynamicHolder<DataModel>> itemInvModels = itemInv.getOrDefault(ExtraHostile.Components.EXTRA_DATA_MODEL, List.of());
            for (DynamicHolder<DataModel> holder: itemInvModels) {
                if (findModels.contains(holder)) {
                    playerInv.removeItem(i, 1);

                    if (!this.inventory.getStackInSlot(0).isEmpty()){
                        playerInv.add(this.inventory.getStackInSlot(0));
                        this.inventory.setStackInSlot(0, ItemStack.EMPTY);
                    }

                    this.inventory.setStackInSlot(0, itemInv);
                    return;
                }
            }
        }
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (this.inventory.getStackInSlot(0).isEmpty()) {
            this.failState = FailureState.MODEL;
            this.runtime = 0;
            return;
        }

        if (this.inventory.getStackInSlot(1).isEmpty() && this.runtime == 0){
            this.failState = FailureState.INPUT;
            return;
        }

        ItemStack model = this.inventory.getStackInSlot(0);
        if (model.isEmpty()) {
            this.failState = FailureState.MODEL;
            this.runtime = 0;
            return;
        }

        ExtraDataModelInstance oldModel = this.currentModel;
        this.currentModel = this.getOrLoadModel(model);
        if (oldModel != this.currentModel) this.runtime = 0;
        if (!this.currentModel.isValid()) return;

        if (!this.currentModel.getTier().data().canSim()) {
            this.failState = FailureState.FAULTY;
            this.runtime = 0;
            return;
        }

        startWork(level, model);
    }

    public void startWork(Level level, ItemStack model){
        if (this.runtime == 0) {
            if (this.canStartSimulation()) {
                this.runtime = DURATION;
                this.predictionSuccess = this.currentModel.rollAllPredictions(level.random);
                this.inventory.getStackInSlot(1).shrink(version.getMultiplier() * 4);
                this.setChanged();
            }
            return;

        } else if (this.hasPowerFor(this.currentModel)) {
            if (!this.getRedstoneState().matches(level.hasNeighborSignal(worldPosition))){
                this.failState = FailureState.REDSTONE;
                return;
            }

            this.failState = FailureState.NONE;
            if (--this.runtime == 0) setResult(model);
            else {
                this.energy.setEnergy(this.energy.getEnergyStored() - this.currentModel.simCost());
                this.setChanged();
            }
            return;
        }

        this.failState = FailureState.ENERGY_MID_CYCLE;
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
        }

        if (this.energy.getEnergyStored() < this.currentModel.simCost()){
            this.failState = FailureState.ENERGY;
            return false;
        }

        checkOutput = false;

        Map<DropType, List<ItemStack>> dropData = Map.of(DropType.BASE, List.of(), DropType.PREDICATE, List.of());
        for (DynamicHolder<DataModel> model: this.currentModel.getModels()) {
            dropData.get(DropType.BASE).add(model.get().baseDrop());
            dropData.get(DropType.PREDICATE).add(model.get().getPredictionDrop());
        }

        boolean dropSet = setItem(dropData, true);
        this.failState = dropSet ? FailureState.NONE: FailureState.OUTPUT;
        return dropSet;
    }

    public void setResult(ItemStack itemModel) {
        Map<DropType, List<ItemStack>> dropData = Map.of(DropType.BASE, List.of(), DropType.PREDICATE, List.of());

        for (DynamicHolder<DataModel> model: this.currentModel.getModels()){
            var predDrop = model.get().getPredictionDrop();
            predDrop.setCount(version.getMultiplier());
            dropData.get(DropType.BASE).add(predDrop);

            var baseDrop = model.get().baseDrop();
            baseDrop.setCount(version.getMultiplier());
            dropData.get(DropType.PREDICATE).add(baseDrop);
        }

        setItem(dropData, false);

        ExtraModelTier tier = this.currentModel.getTier();
        if (!tier.isMax()) {
            int newData = this.currentModel.getData() + this.currentModel.getDataPerKill();
            if (newData <= this.currentModel.getNextTierData()) this.currentModel.setData(newData);
        }
        ExtraDataModelItem.setIters(itemModel, ExtraDataModelItem.getIters(itemModel) + 1);
        this.setChanged();
    }

    public boolean setItem(Map<DropType, List<ItemStack>> dropData, boolean simulate){
        List<Boolean> isAdd = new ArrayList<>();
        for (var entry: dropData.entrySet()){
            DropType type = entry.getKey();
            List<ItemStack> listDrops = entry.getValue();
            if (listDrops.isEmpty()) continue;

            isAdd.add(type.setItemDrop(listDrops, inventory, simulate));
        }

        return isAdd.stream().allMatch(b -> b);
    }

    public boolean hasPowerFor(ExtraDataModelInstance model) {
        return this.energy.getEnergyStored() >= model.simCost();
    }

    protected ExtraDataModelInstance getOrLoadModel(ItemStack stack) {
        return this.currentModel.getSourceStack() == stack ? this.currentModel : new ExtraDataModelInstance(stack);
    }

    public int getEnergyStored() {
        return this.energy.getEnergyStored();
    }

    public boolean didPredictionSucceed() {
        return this.predictionSuccess > 0;
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
            if (slot <= 1 && !extractDataModel) return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
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
        REDSTONE("hostilenetworks.fail.redstone"),
        COUNT("extrahnn.fail.count");

        private final String id;

        FailureState(String id) {
            this.id = id;
        }

        public String getKey() {
            return id;
        }
    }

    public void setConfig(){
        switch (version.getId().toLowerCase()) {
            case "v2" -> {
                CAP = ExtraHostileConfig.ultimateSimV2PowerCap;
                DURATION = ExtraHostileConfig.ultimateSimV2PowerDuration;
            }
            case "v3" -> {
                CAP = ExtraHostileConfig.ultimateSimV3PowerCap;
                DURATION = ExtraHostileConfig.ultimateSimV3PowerDuration;
            }
            case "v4" -> {
                CAP = ExtraHostileConfig.ultimateSimV4PowerCap;
                DURATION = ExtraHostileConfig.ultimateSimV4PowerDuration;
            }
            default -> {
                CAP = ExtraHostileConfig.ultimateSimV1PowerCap;
                DURATION = ExtraHostileConfig.ultimateSimV1PowerDuration;
            }
        }
    }

    public enum DropType {
        BASE(new int[]{2, 3, 4, 5}), PREDICATE(new int[]{6, 7, 7, 9});

        final int[] outputSlots;
        DropType(int[] slots){
            outputSlots = slots;
        }

        public boolean setItemDrop(List<ItemStack> listDrops, SimItemHandler inventory, boolean simulate){
            return insertDropsIntoSlots(listDrops, inventory, outputSlots, simulate);
        }

        private boolean insertDropsIntoSlots(List<ItemStack> listDrops, SimItemHandler inventory, int[] slots, boolean simulate) {
            for (ItemStack drop : listDrops) {
                ItemStack remaining = drop.copy();

                for (int slot : slots) {
                    if (remaining.isEmpty()) break;
                    remaining = inventory.insertItem(slot, remaining, simulate);
                }

                if (!remaining.isEmpty()) return false;
            }
            return true;
        }

    }
}