package net.lmor.extrahnn.common.tile;

import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.data.DataModelInstance;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import dev.shadowsoffire.placebo.cap.ModifiableEnergyStorage;
import dev.shadowsoffire.placebo.menu.SimpleDataSlots;
import dev.shadowsoffire.placebo.menu.SimpleDataSlots.IDataAutoRegister;
import net.lmor.extrahnn.ExtraHostile;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.lmor.extrahnn.api.IRegTile;
import net.lmor.extrahnn.common.item.ExtraDataModelItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MergerCameraTileEntity extends BlockEntity implements TickingBlockEntity, IDataAutoRegister, IRegTile {

    private final static int SIZE_SLOTS_MODEL = 4;
    protected final CameraItemHandler inventory = new CameraItemHandler();
    protected final ModifiableEnergyStorage energy = new ModifiableEnergyStorage(ExtraHostileConfig.mergerCameraPowerCap, ExtraHostileConfig.mergerCameraPowerCap);
    protected final SimpleDataSlots data = new SimpleDataSlots();
    protected boolean validModels = false;
    protected FailureState failState = FailureState.NONE;
    protected int runtime = 0;

    protected boolean checkModel = false;

    public MergerCameraTileEntity(BlockPos pos, BlockState state) {
        super(ExtraHostile.TileEntities.MERGER_CAMERA, pos, state);

        this.data.addData(() -> this.runtime, v -> this.runtime = v);
        this.data.addData(() -> this.failState.ordinal(), v -> this.failState = FailureState.values()[v]);
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
        tag.putBoolean("checkModel", this.checkModel);
        tag.putInt("runtime", this.runtime);
        tag.putBoolean("validModels", this.validModels);
        tag.putInt("failState", this.failState.ordinal());
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider regs) {
        super.loadAdditional(tag, regs);
        this.inventory.deserializeNBT(regs, tag.getCompound("inventory"));
        this.energy.setEnergy(tag.getInt("energy"));
        this.checkModel = tag.getBoolean("checkModel");
        this.runtime = tag.getInt("runtime");
        this.validModels = tag.getBoolean("validModels");
        this.failState = FailureState.values()[tag.getInt("failState")];
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (this.runtime == 0) checkSlotModels();

        if (validModels){
            if (this.runtime == 0) {
                if (this.canStartSimulation()){
                    this.runtime = ExtraHostileConfig.mergerCameraPowerDuration;
                    this.setChanged();
                }
            }
            else if (this.getEnergyStored() >= ExtraHostileConfig.mergerCameraPowerCost){
                this.failState = FailureState.NONE;
                if (--this.runtime == 0){
                    createNewModelData();
                } else {
                    this.energy.setEnergy(this.energy.getEnergyStored() - ExtraHostileConfig.mergerCameraPowerCost);
                }
            } else {
                this.failState = FailureState.ENERGY_MID_CYCLE;
            }

            return;

        }

        this.failState = FailureState.MODEL;
        this.runtime = 0;
    }

    public void createNewModelData(){
        List<DataModel> dataModels = new ArrayList<>();
        this.inventory.getStackInSlot(4).shrink(1);

        for (int i = 0; i < 4; i++){
            DataModelInstance instance = new DataModelInstance(inventory.getStackInSlot(i), i);
            if (instance.isValid()){
                dataModels.add(instance.getModel());
                inventory.getStackInSlot(i).shrink(1);
            }
        }

        if (dataModels.size() != 4) return;

        ItemStack modelStack = new ItemStack(ExtraHostile.Items.EXTRA_DATA_MODEL);
        ExtraDataModelItem.setStoredModel(modelStack, dataModels);

        this.inventory.setStackInSlot(5, modelStack);
    }

    public void checkSlotModels(){
        if (!checkModel) return;
        validModels = true;

        List<DataModelInstance> newModels = new ArrayList<>();
        for (int i = 0; i < SIZE_SLOTS_MODEL; i++){
            newModels.add(new DataModelInstance(this.inventory.getStackInSlot(i), i));
        }

        for (DataModelInstance model: newModels){
            if (!model.isValid()) {
                validModels = false;
                break;
            }
        }
        this.checkModel = false;
    }

    public boolean canStartSimulation() {
        if (this.inventory.getStackInSlot(4).isEmpty()){
            this.failState = FailureState.INPUT;
            return false;
        }

        if (!this.inventory.getStackInSlot(5).isEmpty()) {
            this.failState = FailureState.OUTPUT;
            return false;
        }

        if (this.getEnergyStored() < ExtraHostileConfig.mergerCameraPowerCost){
            this.failState = FailureState.ENERGY;
            return false;
        }

        checkSlotModels();
        if (validModels) {
            this.failState = FailureState.NONE;
            return true;
        }

        this.failState = FailureState.NONE;
        return true;
    }

    public CameraItemHandler getInventory() {
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

    public FailureState getFailState() {
        return this.failState;
    }

    public class CameraItemHandler extends InternalItemHandler {
        public CameraItemHandler() {
            super(6);
        }

        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot < SIZE_SLOTS_MODEL) {
                return stack.getItem() instanceof DataModelItem && new DataModelInstance(stack, slot).getTier().isMax();
            } else if (slot == SIZE_SLOTS_MODEL) {
                return stack.getItem() == ExtraHostile.Items.BLANK_EXTRA_DATA_MODEL;
            }
            return true;
        }

        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return slot == 5 ? stack : super.insertItem(slot, stack, simulate);
        }

        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot < SIZE_SLOTS_MODEL + 1 ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
        }

        protected void onContentsChanged(int slot) {
            MergerCameraTileEntity.this.setChanged();
            MergerCameraTileEntity.this.checkModel = true;
        }

        public NonNullList<ItemStack> getItems() {
            return this.stacks;
        }
    }

    public enum FailureState {
        NONE("none"),
        OUTPUT("output"),
        ENERGY("energy"),
        MODEL("model"),
        INPUT("input"),
        ENERGY_MID_CYCLE("energy_mid_cycle");

        private final String name;

        FailureState(String name) {
            this.name = name;
        }

        public String getKey() {
            return "extrahnn.fail." + this.name ;
        }
    }
}
