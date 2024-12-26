package net.lmor.extrahnn.tile;

import dev.shadowsoffire.hostilenetworks.data.CachedModel;
import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.data.DataModelRegistry;
import dev.shadowsoffire.hostilenetworks.data.ModelTier;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import dev.shadowsoffire.placebo.cap.ModifiableEnergyStorage;
import dev.shadowsoffire.placebo.menu.SimpleDataSlots;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.lmor.extrahnn.ExtraHostile;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.lmor.extrahnn.data.ExtraCachedModel;
import net.lmor.extrahnn.item.ExtraDataModelItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class MergerCameraTileEntity extends BlockEntity implements TickingBlockEntity, SimpleDataSlots.IDataAutoRegister {

    private final static int SIZE_SLOTS_MODEL = 4;
    protected final CameraItemHandler inventory = new CameraItemHandler();
    protected final ModifiableEnergyStorage energy;
    protected final SimpleDataSlots data;
    protected List<CachedModel> currentModels = new ArrayList<>();
    protected ExtraCachedModel craftModel = ExtraCachedModel.EMPTY;
    protected FailureState failState;
    protected int runtime;
    protected boolean isOverheat;

    protected boolean checkModel = false;

    public MergerCameraTileEntity(BlockPos pos, BlockState state) {
        super(ExtraHostile.TileEntities.MERGER_CAMERA.get(), pos, state);
        this.energy = new ModifiableEnergyStorage(ExtraHostileConfig.mergerCameraPowerCap, ExtraHostileConfig.mergerCameraPowerCap);
        this.data = new SimpleDataSlots();
        this.currentModels.addAll(List.of(CachedModel.EMPTY, CachedModel.EMPTY, CachedModel.EMPTY, CachedModel.EMPTY));
        this.failState = MergerCameraTileEntity.FailureState.NONE;

        this.isOverheat = false;

        this.data.addData(() -> {
            return this.runtime;
        }, (v) -> {
            this.runtime = v;
        });
        this.data.addData(() -> {
            return this.failState.ordinal();
        }, (v) -> {
            this.failState = MergerCameraTileEntity.FailureState.values()[v];
        });
        this.data.addEnergy(this.energy);
    }

    @Override
    public void registerSlots(Consumer<DataSlot> consumer) {
        this.data.register(consumer);
    }

    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", this.inventory.serializeNBT());
        tag.putInt("energy", this.energy.getEnergyStored());
        tag.putBoolean("checkModel", this.checkModel);
        tag.putBoolean("isOverheat", this.isOverheat);

        for (int i = 0; i < SIZE_SLOTS_MODEL; i++){
            if (i >= this.currentModels.size()) break;

            tag.putString("model_" + i, !this.currentModels.get(i).isValid() ? "null" : Objects.requireNonNull(DataModelRegistry.INSTANCE.getKey(this.currentModels.get(i).getModel())).toString());
        }

        if (runtime != 0){
            for (int i = 0; i < SIZE_SLOTS_MODEL; i++){
                tag.putString("craftModel_" + i, Objects.requireNonNull(DataModelRegistry.INSTANCE.getKey(craftModel.getModels().get(i).get())).toString());
            }
        }

        tag.putInt("runtime", this.runtime);
        tag.putInt("failState", this.failState.ordinal());
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.energy.setEnergy(tag.getInt("energy"));
        this.checkModel = tag.getBoolean("checkModel");
        this.isOverheat = tag.getBoolean("isOverheat");
        this.runtime = tag.getInt("runtime");

        List<CachedModel> models = new ArrayList<>();
        for (int i = 0; i < SIZE_SLOTS_MODEL; i++){
            ItemStack model = this.inventory.getStackInSlot(i);
            CachedModel cModel = this.getOrLoadModel(model, i);
            ResourceLocation modelId = new ResourceLocation(tag.getString("model_" + i));

            if (cModel.isValid() && DataModelRegistry.INSTANCE.getKey(cModel.getModel()).equals(modelId)) {
                models.add(cModel);
            }
        }

        if (this.runtime != 0){
            craftModel = new ExtraCachedModel(ItemStack.EMPTY, 5);
            for (int i = 0; i < SIZE_SLOTS_MODEL; i++){
                ResourceLocation modelId = new ResourceLocation(tag.getString("craftModel_" + i));
                craftModel.setModels(DataModelRegistry.INSTANCE.holder(modelId), i);
            }
        } else {
            craftModel = ExtraCachedModel.EMPTY;
        }

        this.currentModels = models;
        this.failState = MergerCameraTileEntity.FailureState.values()[tag.getInt("failState")];
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (this.runtime == 0) checkSlotModels();

        if (currentModels.size() == SIZE_SLOTS_MODEL || craftModel != ExtraCachedModel.EMPTY){
            if (this.runtime == 0) {
                if (this.canStartSimulation()){
                    this.runtime = ExtraHostileConfig.mergerCameraPowerDuration;
                    assert this.level != null;
                    this.isOverheat = this.level.random.nextFloat() <= 0.05;
                    if (this.isOverheat) this.failState = FailureState.HIGH_TEMP;

                    craftModel = new ExtraCachedModel(ItemStack.EMPTY, 5);
                    for (int i = 0; i < SIZE_SLOTS_MODEL; i++){
                        craftModel.setModels(new CachedModel(this.getInventory().getStackInSlot(i), i).getModel(), i);
                        this.getInventory().getStackInSlot(i).shrink(1);
                    }
                    this.inventory.getStackInSlot(4).shrink(1);
                    this.setChanged();
                }
            }
            else if (this.getEnergyStored() >= ExtraHostileConfig.mergerCameraPowerCost){
                this.failState = FailureState.NONE;
                if (--this.runtime == 0){
                    if (!this.isOverheat) createNewModelData();
                    this.isOverheat = false;
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
        this.currentModels = new ArrayList<>();

        List<DataModel> dataModels = new ArrayList<>();

        for (DynamicHolder<DataModel> model: craftModel.getModels()){
            dataModels.add(model.get());
        }

        ItemStack modelStack = new ItemStack(ExtraHostile.Items.EXTRA_DATA_MODEL.get());
        ExtraDataModelItem.setStoredModels(modelStack, dataModels);

        craftModel = ExtraCachedModel.EMPTY;
        this.inventory.setStackInSlot(5, modelStack);
    }

    public void checkSlotModels(){
        if (!checkModel) return;

        List<CachedModel> newModels = new ArrayList<>();
        for (int i = 0; i < SIZE_SLOTS_MODEL; i++){
            if (!this.inventory.getStackInSlot(i).isEmpty()) {
                newModels.add(new CachedModel(this.inventory.getStackInSlot(i), i));
            }
        }

        this.currentModels = newModels;
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
        if (currentModels.size() != SIZE_SLOTS_MODEL) {
            this.failState = MergerCameraTileEntity.FailureState.NONE;
            return true;
        }

        this.failState = MergerCameraTileEntity.FailureState.NONE;
        return true;
    }

    protected CachedModel getOrLoadModel(ItemStack stack, int index) {
        return this.currentModels.get(index).getSourceStack() == stack ? this.currentModels.get(index) : new CachedModel(stack, index);
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

    public CameraItemHandler getInventory() {
        return this.inventory;
    }

    public int getEnergyStored() {
        return this.energy.getEnergyStored();
    }

    public int getRuntime() {
        return this.runtime;
    }
    public boolean getOverheat() {
        return this.isOverheat;
    }


    public FailureState getFailState() {
        return this.failState;
    }

    public class CameraItemHandler extends InternalItemHandler {
        public CameraItemHandler() {
            super(6);
        }

        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot < SIZE_SLOTS_MODEL) {
                return stack.getItem() instanceof DataModelItem && new CachedModel(stack, slot).getTier() == ModelTier.SELF_AWARE;
            } else if (slot == SIZE_SLOTS_MODEL) {
                return stack.getItem() == ExtraHostile.Items.BLANK_EXTRA_DATA_MODEL.get();
            }
            return true;
        }

        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return slot == 5 ? stack : super.insertItem(slot, stack, simulate);
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot < SIZE_SLOTS_MODEL + 1 ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
        }

        protected void onContentsChanged(int slot) {
            MergerCameraTileEntity.this.setChanged();
            MergerCameraTileEntity.this.checkModel = true;
        }
    }

    public static enum FailureState {
        NONE("none"),
        HIGH_TEMP("high_temperature"),
        OUTPUT("output"),
        ENERGY("energy"),
        MODEL("model"),
        INPUT("input"),
        ENERGY_MID_CYCLE("energy_mid_cycle");

        private final String name;

        private FailureState(String name) {
            this.name = name;
        }

        public String getKey() {
            return "extrahnn.fail." + this.name ;
        }
    }
}
