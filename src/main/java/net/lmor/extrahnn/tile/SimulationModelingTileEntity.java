package net.lmor.extrahnn.tile;

import dev.shadowsoffire.hostilenetworks.data.CachedModel;
import dev.shadowsoffire.hostilenetworks.data.ModelTier;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import dev.shadowsoffire.placebo.cap.ModifiableEnergyStorage;
import dev.shadowsoffire.placebo.menu.SimpleDataSlots;
import net.lmor.extrahnn.ExtraHostile;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.lmor.extrahnn.data.ExtraCachedModel;
import net.lmor.extrahnn.data.ExtraModelTier;
import net.lmor.extrahnn.item.ExtraDataModelItem;
import net.lmor.extrahnn.item.UpgradeMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import javax.xml.crypto.Data;
import java.util.function.Consumer;

public class SimulationModelingTileEntity extends BlockEntity implements TickingBlockEntity, SimpleDataSlots.IDataAutoRegister {

    private final static int SIZE_SLOTS = 3;
    protected final SimulatorModelingItemHandler inventory = new SimulatorModelingItemHandler();
    protected final ModifiableEnergyStorage energy;
    protected final SimpleDataSlots data;
    protected int runtime = ExtraHostileConfig.simulationModelingPowerDuration;

    private int runtimeUpgrade = runtime;

    private int energyCost = ExtraHostileConfig.simulationModelingPowerCost;

    private boolean upgradeSpeed;
    private boolean upgradeModuleStack;
    private boolean upgradeDataKill;

    private boolean startCraft = false;

    public SimulationModelingTileEntity(BlockPos pos, BlockState state) {
        super(ExtraHostile.TileEntities.SIMULATOR_MODELING.get(), pos, state);

        this.energy = new ModifiableEnergyStorage(ExtraHostileConfig.simulationModelingPowerCap, ExtraHostileConfig.simulationModelingPowerCap);
        this.data = new SimpleDataSlots();

        this.data.addData(() -> {
            return this.runtime;
        }, (v) -> {
            this.runtime = v;
        });
        this.data.addEnergy(this.energy);

    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.energy.setEnergy(tag.getInt("energy"));
        this.runtime = tag.getInt("runtime");
        this.startCraft = tag.getBoolean("startCraft");

        checkUpgrade();
    }

    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", this.inventory.serializeNBT());
        tag.putInt("energy", this.energy.getEnergyStored());
        tag.putInt("runtime", this.runtime);
        tag.putBoolean("startCraft", this.startCraft);
    }

    @Override
    public void registerSlots(Consumer<DataSlot> consumer) {
        this.data.register(consumer);
    }

    public void checkUpgrade(){
        upgradeSpeed = false;
        upgradeModuleStack = false;
        upgradeDataKill = false;
        energyCost = ExtraHostileConfig.simulationModelingPowerCost;
        runtimeUpgrade = ExtraHostileConfig.simulationModelingPowerDuration;

        for (int i = 1; i < SIZE_SLOTS; i++){
            Item upgrade = this.inventory.getStackInSlot(i).getItem();
            if (upgrade == ExtraHostile.Items.UPGRADE_SPEED.get()) upgradeSpeed = true;
            if (upgrade == ExtraHostile.Items.UPGRADE_MODULE_STACK.get()) upgradeModuleStack = true;
            if (upgrade == ExtraHostile.Items.UPGRADE_DATA_KILL.get()) upgradeDataKill = true;
        }


        if (upgradeSpeed){
            runtimeUpgrade = (int) Math.max(0, ExtraHostileConfig.simulationModelingPowerDuration * (1 - (float) ExtraHostileConfig.upgradeSpeed / 100));
            energyCost = (int) Math.min(ExtraHostileConfig.simulationModelingPowerCap, ExtraHostileConfig.simulationModelingPowerCost * ExtraHostileConfig.upgradeSpeedEnergy);

            if (runtimeUpgrade < runtime){
                runtime = runtimeUpgrade;
            }
        }
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (!startCraft) return;

        if(this.inventory.getStackInSlot(0).isEmpty()) {
            this.runtime = 0;
            return;
        }

        if ((this.inventory.getStackInSlot(0).getItem() instanceof DataModelItem &&
                new CachedModel(this.inventory.getStackInSlot(0), 0).getTier() == ModelTier.SELF_AWARE) ||
                (this.inventory.getStackInSlot(0).getItem() instanceof ExtraDataModelItem && new ExtraCachedModel(this.inventory.getStackInSlot(0), 0).getTier() == ExtraModelTier.OMNIPOTENT)) {
            startCraft = false;
        }

        if (this.runtime == 0){
            this.runtime = runtimeUpgrade;
            this.setChanged();

        } else if (this.getEnergyStored() >= energyCost) {
            if (--this.runtime == 0) {
                result();
            } else {
                this.energy.setEnergy(this.energy.getEnergyStored() - energyCost);
                this.setChanged();
            }
        }
    }

    public void result(){
        ItemStack item = this.inventory.getStackInSlot(0);
        if (item.getItem() instanceof DataModelItem) {

            CachedModel model = new CachedModel(item, 0);

            ModelTier tier = model.getTier();

            if (tier != tier.next()) {
                if (upgradeModuleStack){
                    model.setData(model.getNextTierData());
                }
                else {
                    int newData = model.getData() + model.getDataPerKill() * (upgradeDataKill ? ExtraHostileConfig.upgradeDataKill : 1);
                    if (newData <= model.getNextTierData()) {
                        model.setData(newData);
                    }
                }
            }

            DataModelItem.setIters(item, DataModelItem.getIters(item) + (upgradeDataKill ? ExtraHostileConfig.upgradeDataKill : 1));
            this.setChanged();

        } else if (item.getItem() instanceof ExtraDataModelItem) {
            ExtraCachedModel model = new ExtraCachedModel(item, 0);

            ExtraModelTier tier =model.getTier();
            if (tier != tier.next()) {
                if (upgradeModuleStack){
                    model.setData(model.getNextTierData());
                } else {
                    int newData = model.getData() + model.getDataPerKill() * (upgradeDataKill ? ExtraHostileConfig.upgradeDataKill : 1);
                    if (newData <= model.getNextTierData()) {
                        model.setData(newData);
                    }
                }
            }

            ExtraDataModelItem.setIters(item, ExtraDataModelItem.getIters(item) + (upgradeDataKill ? ExtraHostileConfig.upgradeDataKill : 1));
            this.setChanged();
        }
    }

    public SimulatorModelingItemHandler getInventory() {
        return this.inventory;
    }

    public int getEnergyStored() {
        return this.energy.getEnergyStored();
    }

    public int getEnergyCost(){
        return this.energyCost;
    }

    public int getRuntime() {
        return this.runtime;
    }

    public int getRuntimeUpgrade() {
        return runtimeUpgrade;
    }

    public boolean getUpgradeDataKill(){
        return upgradeDataKill;
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

    public class SimulatorModelingItemHandler extends InternalItemHandler {
        public SimulatorModelingItemHandler() {
            super(SIZE_SLOTS);
        }

        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0){
                return (stack.getItem() instanceof DataModelItem && new CachedModel(stack, 0).getTier() != ModelTier.SELF_AWARE) ||
                        (stack.getItem() instanceof ExtraDataModelItem && new ExtraCachedModel(stack, 0).getTier() != ExtraModelTier.OMNIPOTENT);
            }

            if (slot >= 1 && slot < SIZE_SLOTS){
                return stack.getItem() instanceof UpgradeMachine;
            }

            return false;
        }

        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return super.insertItem(slot, stack, simulate);
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 0){
                ItemStack stack = SimulationModelingTileEntity.this.inventory.getStackInSlot(0);

                if ((stack.getItem() instanceof DataModelItem && new CachedModel(stack, 0).getTier() == ModelTier.SELF_AWARE) ||
                        (stack.getItem() instanceof ExtraDataModelItem && new ExtraCachedModel(stack, 0).getTier() == ExtraModelTier.OMNIPOTENT) ){
                    return super.extractItem(slot, amount, simulate);
                }
            }
            return ItemStack.EMPTY;
        }

        protected void onContentsChanged(int slot) {
            SimulationModelingTileEntity.this.setChanged();
            SimulationModelingTileEntity.this.checkUpgrade();
            SimulationModelingTileEntity.this.startCraft = true;
        }
    }

}
