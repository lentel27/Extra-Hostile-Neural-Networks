package net.lmor.extrahnn.common.tile;

import dev.shadowsoffire.hostilenetworks.data.DataModelInstance;
import dev.shadowsoffire.hostilenetworks.data.ModelTier;
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
import net.lmor.extrahnn.common.item.UpgradeMachine;
import net.lmor.extrahnn.data.ExtraDataModelInstance;
import net.lmor.extrahnn.data.ExtraModelTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SimulationModelingTileEntity extends BlockEntity implements TickingBlockEntity, IDataAutoRegister, IRegTile {

    private final static int SIZE_SLOTS = 3;
    protected final SimulatorModelingItemHandler inventory = new SimulatorModelingItemHandler();
    protected final ModifiableEnergyStorage energy = new ModifiableEnergyStorage(ExtraHostileConfig.simulationModelingPowerCap, ExtraHostileConfig.simulationModelingPowerCap);
    protected final SimpleDataSlots data = new SimpleDataSlots();
    protected int runtime = ExtraHostileConfig.simulationModelingPowerDuration;

    private int runtimeUpgrade = runtime;

    private int energyCost = ExtraHostileConfig.simulationModelingPowerCost;

    private boolean upgradeModuleStack;
    private boolean upgradeDataKill;

    private boolean startCraft = false;

    public SimulationModelingTileEntity(BlockPos pos, BlockState state) {
        super(ExtraHostile.TileEntities.SIMULATOR_MODELING, pos, state);

        this.data.addData(() -> this.runtime, v -> this.runtime = v);
        this.data.addEnergy(this.energy);
        this.energy.setMaxExtract(0);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider regs) {
        super.saveAdditional(tag, regs);
        tag.put("inventory", this.inventory.serializeNBT(regs));
        tag.putInt("energy", this.energy.getEnergyStored());
        tag.putInt("runtime", this.runtime);
        tag.putBoolean("startCraft", this.startCraft);
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider regs) {
        super.loadAdditional(tag, regs);
        this.inventory.deserializeNBT(regs, tag.getCompound("inventory"));
        this.energy.setEnergy(tag.getInt("energy"));
        this.runtime = tag.getInt("runtime");
        this.startCraft = tag.getBoolean("startCraft");

        checkUpgrade();
    }

    @Override
    public void registerSlots(Consumer<DataSlot> consumer) {
        this.data.register(consumer);
    }

    public void checkUpgrade(){
        boolean upgradeSpeed = false;
        this.runtime = 0;
        upgradeModuleStack = false;
        upgradeDataKill = false;
        energyCost = ExtraHostileConfig.simulationModelingPowerCost;
        runtimeUpgrade = ExtraHostileConfig.simulationModelingPowerDuration;

        for (int i = 1; i < SIZE_SLOTS; i++){
            ItemStack upgrade = this.inventory.getStackInSlot(i);
            if (upgrade.is(ExtraHostile.Items.UPGRADE_SPEED)) upgradeSpeed = true;
            if (upgrade.is(ExtraHostile.Items.UPGRADE_MODULE_STACK)) upgradeModuleStack = true;
            if (upgrade.is(ExtraHostile.Items.UPGRADE_DATA_KILL)) upgradeDataKill = true;
        }

        energyCost = upgradeModuleStack ? Math.min(ExtraHostileConfig.simulationModelingPowerCap, ExtraHostileConfig.upgradeModuleStackCost) : energyCost;

        if (upgradeSpeed){
            runtimeUpgrade = (int) Math.max(0, ExtraHostileConfig.simulationModelingPowerDuration * (1 - (float) ExtraHostileConfig.upgradeSpeed / 100));
            energyCost = (int) Math.min(ExtraHostileConfig.simulationModelingPowerCap, energyCost * ExtraHostileConfig.upgradeSpeedEnergy);

            if (runtimeUpgrade < runtime) runtime = runtimeUpgrade;
        }

    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (!startCraft) return;

        if(this.inventory.getStackInSlot(0).isEmpty()) {
            this.runtime = 0;
            return;
        }

        ItemStack item = this.inventory.getStackInSlot(0);

        if (( item.getItem() instanceof DataModelItem && new DataModelInstance(item, 0).getTier().isMax()) ||
                (item.getItem() instanceof ExtraDataModelItem && new ExtraDataModelInstance(item, 0).getTier().isMax())) {
            startCraft = false;
            return;
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

            DataModelInstance model = new DataModelInstance(item, 0);

            ModelTier tier = model.getTier();
            if (!tier.isMax()) {
                if (upgradeModuleStack) model.setData(model.getNextTierData());
                else {
                    int newData = model.getData() + model.getDataPerKill() * (upgradeDataKill ? ExtraHostileConfig.upgradeDataKill : 1);
                    int resData = Math.min(newData, model.getNextTierData());
                    model.setData(resData);
                }
            }

            DataModelItem.setIters(item, DataModelItem.getIters(item) + (upgradeDataKill ? ExtraHostileConfig.upgradeDataKill : 1));
            this.setChanged();

        } else if (item.getItem() instanceof ExtraDataModelItem) {
            ExtraDataModelInstance model = new ExtraDataModelInstance(item, 0);

            ExtraModelTier tier =model.getTier();
            if (!tier.isMax()) {
                if (upgradeModuleStack) model.setData(model.getNextTierData());
                else {
                    int newData = model.getData() + model.getDataPerKill() * (upgradeDataKill ? ExtraHostileConfig.upgradeDataKill : 1);
                    int resData = Math.min(newData, model.getNextTierData());
                    model.setData(resData);
                }
            }

            ExtraDataModelItem.setIters(item, ExtraDataModelItem.getIters(item) + (upgradeDataKill ? ExtraHostileConfig.upgradeDataKill : 1));
            this.setChanged();
        }
    }

    public SimulatorModelingItemHandler getInventory() {
        return this.inventory;
    }

    public IEnergyStorage getEnergy() {
        return energy;
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

    public class SimulatorModelingItemHandler extends InternalItemHandler {
        public SimulatorModelingItemHandler() {
            super(SIZE_SLOTS);
        }

        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0){
                return (stack.getItem() instanceof DataModelItem && !new DataModelInstance(stack, 0).getTier().isMax()) ||
                        (stack.getItem() instanceof ExtraDataModelItem && !new ExtraDataModelInstance(stack, 0).getTier().isMax());
            }

            return slot >= 1 && slot < SIZE_SLOTS && stack.getItem() instanceof UpgradeMachine;
        }

        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return super.insertItem(slot, stack, simulate);
        }

        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 0){
                ItemStack stack = SimulationModelingTileEntity.this.inventory.getStackInSlot(0);

                if ((stack.getItem() instanceof DataModelItem && new DataModelInstance(stack, 0).getTier().isMax()) ||
                        (stack.getItem() instanceof ExtraDataModelItem && new ExtraDataModelInstance(stack, 0).getTier().isMax()) ){
                    return super.extractItem(slot, amount, simulate);
                }
            }
            return ItemStack.EMPTY;
        }

        protected void onContentsChanged(int slot) {
            SimulationModelingTileEntity.this.setChanged();
            if (slot >= 1 && slot < 3) SimulationModelingTileEntity.this.checkUpgrade();
            SimulationModelingTileEntity.this.startCraft = true;
        }

        public NonNullList<ItemStack> getItems() {
            return this.stacks;
        }
    }
}
