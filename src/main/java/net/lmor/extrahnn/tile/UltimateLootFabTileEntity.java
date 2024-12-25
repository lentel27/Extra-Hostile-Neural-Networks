package net.lmor.extrahnn.tile;


import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.lmor.extrahnn.ExtraHostile;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import shadows.hostilenetworks.Hostile;
import shadows.hostilenetworks.data.DataModel;
import shadows.hostilenetworks.data.DataModelManager;
import shadows.hostilenetworks.item.MobPredictionItem;
import shadows.placebo.block_entity.TickingBlockEntity;
import shadows.placebo.cap.InternalItemHandler;
import shadows.placebo.cap.ModifiableEnergyStorage;
import shadows.placebo.container.EasyContainerData;
import shadows.placebo.recipe.VanillaPacketDispatcher;

import java.util.function.Consumer;

public class UltimateLootFabTileEntity extends BlockEntity implements TickingBlockEntity, EasyContainerData.IDataAutoRegister {
    protected final FabItemHandler inventory = new FabItemHandler();
    protected final ModifiableEnergyStorage energy;
    protected final Object2IntMap<DataModel> savedSelections;
    protected final EasyContainerData data;
    protected int runtime;
    protected int currentSel;
    private boolean checkOutput = true;

    public UltimateLootFabTileEntity(BlockPos pos, BlockState state) {
        super(ExtraHostile.TileEntities.ULTIMATE_LOOT_FABRICATOR.get(), pos, state);
        this.energy = new ModifiableEnergyStorage(ExtraHostileConfig.ultimateFabPowerCap, ExtraHostileConfig.ultimateFabPowerCap);
        this.savedSelections = new Object2IntOpenHashMap();
        this.data = new EasyContainerData();
        this.runtime = 0;
        this.currentSel = -1;
        this.savedSelections.defaultReturnValue(-1);
        this.data.addData(() -> {
            return this.runtime;
        }, (v) -> {
            this.runtime = v;
        });
        this.data.addEnergy(this.energy);
    }

    public void registerSlots(Consumer<DataSlot> consumer) {
        this.data.register(consumer);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (!checkOutput) return;

        DataModel dm = MobPredictionItem.getStoredModel(this.inventory.getStackInSlot(0));
        if (dm != null) {
            int selection = this.savedSelections.getInt(dm);
            if (this.currentSel != selection) {
                this.currentSel = selection;
                this.runtime = 0;
                return;
            }

            if (selection != -1) {
                if (this.runtime >= ExtraHostileConfig.ultimateFabPowerDuration) {
                    if (checkOutput){
                        checkOutput = false;
                        for (int i = 0; i < 4; i++){
                            if (this.inventory.getStackInSlot(0).getCount() == 0) break;
                            ItemStack out = dm.getFabDrops().get(selection).copy();

                            if (this.insertInOutput(out, true)) {
                                this.runtime = 0;
                                this.insertInOutput(out, false);
                                this.inventory.getStackInSlot(0).shrink(1);
                                this.setChanged();
                            }
                        }

                    }

                } else {
                    if (this.energy.getEnergyStored() < ExtraHostileConfig.ultimateFabPowerCost) {
                        return;
                    }

                    this.energy.setEnergy(this.energy.getEnergyStored() - ExtraHostileConfig.ultimateFabPowerCost);
                    ++this.runtime;
                    this.setChanged();
                }
            } else {
                this.runtime = 0;
            }
        } else {
            this.runtime = 0;
        }

    }

    protected boolean insertInOutput(ItemStack stack, boolean sim) {
        for(int i = 1; i < 17; ++i) {
            stack = this.inventory.insertItemInternal(i, stack, sim);
            if (stack.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public FabItemHandler getInventory() {
        return this.inventory;
    }

    public void setSelection(DataModel model, int pId) {
        if (pId == -1) {
            this.savedSelections.removeInt(model);
        } else {
            this.savedSelections.put(model, Mth.clamp(pId, 0, model.getFabDrops().size() - 1));
        }

        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
        this.setChanged();
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

    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("saved_selections", this.writeSelections(new CompoundTag()));
        tag.put("inventory", this.inventory.serializeNBT());
        tag.putInt("energy", this.energy.getEnergyStored());
        tag.putInt("runtime", this.runtime);
        tag.putInt("selection", this.currentSel);
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.readSelections(tag.getCompound("saved_selections"));
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.energy.setEnergy(tag.getInt("energy"));
        this.runtime = tag.getInt("runtime");
        this.currentSel = tag.getInt("selection");
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, (t) -> {
            return ((UltimateLootFabTileEntity)t).writeSync();
        });
    }

    private CompoundTag writeSync() {
        CompoundTag tag = new CompoundTag();
        tag.put("saved_selections", this.writeSelections(new CompoundTag()));
        return tag;
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.readSelections(pkt.getTag().getCompound("saved_selections"));
    }

    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put("saved_selections", this.writeSelections(new CompoundTag()));
        return tag;
    }

    private CompoundTag writeSelections(CompoundTag tag) {

        for (Object2IntMap.Entry<DataModel> dataModelEntry : this.savedSelections.object2IntEntrySet()) {
            tag.putInt((dataModelEntry.getKey()).getId().toString(), dataModelEntry.getIntValue());
        }

        return tag;
    }

    private void readSelections(CompoundTag tag) {
        this.savedSelections.clear();

        for (String s : tag.getAllKeys()) {
            DataModel dm = DataModelManager.INSTANCE.getValue(new ResourceLocation(s));
            this.savedSelections.put(dm, Mth.clamp(tag.getInt(s), 0, dm.getFabDrops().size() - 1));
        }

    }

    public int getEnergyStored() {
        return this.energy.getEnergyStored();
    }

    public int getRuntime() {
        return this.runtime;
    }

    public int getSelectedDrop(DataModel model) {
        return model == null ? -1 : this.savedSelections.getInt(model);
    }

    public class FabItemHandler extends InternalItemHandler {
        public FabItemHandler() {
            super(17);
        }

        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0) {
                return stack.getItem() == Hostile.Items.PREDICTION.get();
            } else {
                return true;
            }
        }

        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return slot > 0 ? stack : super.insertItem(slot, stack, simulate);
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot == 0 ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
        }

        protected void onContentsChanged(int slot) {
            checkOutput = true;
            UltimateLootFabTileEntity.this.setChanged();
        }
    }
}
