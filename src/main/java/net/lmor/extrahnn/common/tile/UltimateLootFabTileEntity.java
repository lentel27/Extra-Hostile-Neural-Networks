package net.lmor.extrahnn.common.tile;

import dev.shadowsoffire.hostilenetworks.Hostile;
import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.data.DataModelRegistry;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import dev.shadowsoffire.placebo.cap.ModifiableEnergyStorage;
import dev.shadowsoffire.placebo.menu.SimpleDataSlots;
import dev.shadowsoffire.placebo.menu.SimpleDataSlots.IDataAutoRegister;
import dev.shadowsoffire.placebo.network.VanillaPacketDispatcher;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.lmor.extrahnn.api.IRegTile;
import net.lmor.extrahnn.api.ISettingCard;
import net.lmor.extrahnn.api.Version;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class UltimateLootFabTileEntity extends BlockEntity implements TickingBlockEntity, IDataAutoRegister, IRegTile, ISettingCard {
    protected final FabItemHandler inventory = new FabItemHandler();
    protected final ModifiableEnergyStorage energy = new ModifiableEnergyStorage(ExtraHostileConfig.ultimateFabPowerCap, ExtraHostileConfig.ultimateFabPowerCap);
    protected final Object2IntMap<DynamicHolder<DataModel>> savedSelections = new Object2IntOpenHashMap<>();
    protected final SimpleDataSlots data = new SimpleDataSlots();

    protected int runtime = 0;
    protected int currentSel = -1;

    private boolean checkOutput = true;
    private Version version;

    public UltimateLootFabTileEntity(BlockPos pos, BlockState state, BlockEntityType<UltimateLootFabTileEntity> type, Version version) {
        super(type, pos, state);
        this.version = version;

        this.savedSelections.defaultReturnValue(-1);
        this.data.addData(() -> this.runtime, v -> this.runtime = v);
        this.data.addEnergy(this.energy);
        this.energy.setMaxExtract(0);
    }

    @Override
    public void registerSlots(Consumer<DataSlot> consumer) {
        this.data.register(consumer);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (!checkOutput) return;

        DynamicHolder<DataModel> dm = DataModelItem.getStoredModel(this.inventory.getStackInSlot(0));
        if (dm.isBound()) {
            int selection = this.getSelectedDrop(dm.get());
            if (this.currentSel != selection) {
                this.currentSel = selection;
                this.runtime = 0;
                return;
            }
            if (selection != -1) {
                if (this.runtime >= ExtraHostileConfig.ultimateFabPowerDuration) {
                    if (!checkOutput) return;
                    checkOutput = false;

                    int maxCount = Math.min(this.inventory.getStackInSlot(0).getCount(), 4 * version.getMultiplier());
                    if (maxCount == 0) return;


                    ItemStack out = dm.get().fabDrops().get(selection).copy();
                    ItemStack outCopy = out.copy();
                    outCopy.setCount(out.getCount() * maxCount);

                    int old_maxCount;
                    int cycle = 0;

                    while (maxCount > 0){
                        if (this.insertInOutput(outCopy, true)) {
                            this.runtime = 0;
                            this.insertInOutput(outCopy, false);
                            this.inventory.getStackInSlot(0).shrink(maxCount);
                            this.setChanged();
                            break;
                        }
                        old_maxCount = maxCount;
                        maxCount = (int) Math.ceil((double) maxCount / 2);
                        outCopy.setCount(out.getCount() * maxCount);

                        // Just in case, protection against looping
                        if (old_maxCount == maxCount){
                            if (cycle != 0) break;
                            cycle++;
                        }
                    }
                } else {
                    if (this.energy.getEnergyStored() < ExtraHostileConfig.ultimateFabPowerCost) return;
                    this.energy.setEnergy(this.energy.getEnergyStored() - ExtraHostileConfig.ultimateFabPowerCost);
                    this.runtime++;
                    this.setChanged();
                }
            } else this.runtime = 0;
        } else this.runtime = 0;
    }

    protected boolean insertInOutput(ItemStack stack, boolean sim) {
        int amount = stack.getCount();
        for(int i = 1; i < 17; ++i) {
            ItemStack slotStack = this.inventory.getStackInSlot(i);
            ItemStack insertItem = stack.copy();
            if (slotStack.isEmpty()){
                int setCount = Math.min(amount, insertItem.getMaxStackSize());
                insertItem.setCount(setCount);
                amount -= setCount;
                this.inventory.insertItemInternal(i, insertItem, sim);
            } else if (slotStack.is(stack.getItem()) && slotStack.getMaxStackSize() != slotStack.getCount()) {
                int setCount = Math.min(stack.getCount(), slotStack.getMaxStackSize() - slotStack.getCount());
                insertItem.setCount(setCount);
                amount -= setCount;
                this.inventory.insertItemInternal(i, insertItem, sim);
            }

            if (amount <= 0) return true;
        }
        return false;
    }

    public FabItemHandler getInventory() {
        return this.inventory;
    }

    public IEnergyStorage getEnergy() {
        return this.energy;
    }

    public Object2IntMap<DynamicHolder<DataModel>> getSelections() {
        return this.savedSelections;
    }

    public int getEnergyStored() {
        return this.energy.getEnergyStored();
    }

    public int getRuntime() {
        return this.runtime;
    }

    public void setSelections(Object2IntMap<DynamicHolder<DataModel>> selections) {
        this.savedSelections.clear();
        this.savedSelections.putAll(selections);
        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
        this.setChanged();
    }

    public void setSelection(DynamicHolder<DataModel> model, int selection) {
        if (selection == -1) this.savedSelections.removeInt(model);
        else this.savedSelections.put(model, Mth.clamp(selection, 0, model.get().fabDrops().size() - 1));
        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
        this.setChanged();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider regs) {
        super.saveAdditional(tag, regs);
        tag.put("saved_selections", this.writeSelections(new CompoundTag()));
        tag.put("inventory", this.inventory.serializeNBT(regs));
        tag.putInt("energy", this.energy.getEnergyStored());
        tag.putInt("runtime", this.runtime);
        tag.putInt("selection", this.currentSel);

        tag.putString("versionBlockEntity", this.version.getId());
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider regs) {
        super.loadAdditional(tag, regs);
        this.readSelections(tag.getCompound("saved_selections"));
        this.inventory.deserializeNBT(regs, tag.getCompound("inventory"));
        this.energy.setEnergy(tag.getInt("energy"));
        this.runtime = tag.getInt("runtime");
        this.currentSel = tag.getInt("selection");

        this.version = Version.getVersion(tag.getString("versionBlockEntity"));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, (be, regs) -> ((UltimateLootFabTileEntity) be).writeSync());
    }

    private @NotNull CompoundTag writeSync() {
        CompoundTag tag = new CompoundTag();
        tag.put("saved_selections", this.writeSelections(new CompoundTag()));
        return tag;
    }

    @Override
    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt, HolderLookup.@NotNull Provider regs) {
        this.readSelections(pkt.getTag().getCompound("saved_selections"));
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider regs) {
        CompoundTag tag = super.getUpdateTag(regs);
        tag.put("saved_selections", this.writeSelections(new CompoundTag()));
        return tag;
    }

    private CompoundTag writeSelections(CompoundTag tag) {
        for (Object2IntMap.Entry<DynamicHolder<DataModel>> e : this.savedSelections.object2IntEntrySet()) {
            tag.putInt(e.getKey().getId().toString(), e.getIntValue());
        }
        return tag;
    }

    private void readSelections(@NotNull CompoundTag tag) {
        this.savedSelections.clear();
        for (String s : tag.getAllKeys()) {
            DynamicHolder<DataModel> dm = DataModelRegistry.INSTANCE.holder(ResourceLocation.tryParse(s));
            this.savedSelections.put(dm, tag.getInt(s));
        }
    }

    public int getSelectedDrop(DataModel model) {
        if (model == null) return -1;
        int index = this.savedSelections.getInt(DataModelRegistry.INSTANCE.holder(model));
        if (index >= model.fabDrops().size()) return -1;
        return index;
    }

    @Override
    public CompoundTag saveSetting(HolderLookup.@NotNull Provider regs) {
        CompoundTag tag = new CompoundTag();

        CompoundTag saveTag = new CompoundTag();
        this.writeSelections(saveTag);
        tag.put("selections", saveTag);

        return tag;
    }

    @Override
    public boolean loadSetting(HolderLookup.@NotNull Provider regs, CompoundTag tag, Player player) {
        if (!tag.contains("selections")) return false;

        this.readSelections(tag.getCompound("selections"));
        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
        this.setChanged();
        return true;
    }

    public class FabItemHandler extends InternalItemHandler {

        public FabItemHandler() {
            super(17);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot != 0 || stack.is(Hostile.Items.PREDICTION);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return slot > 0 ? stack : super.insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot == 0 ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            checkOutput = true;
            UltimateLootFabTileEntity.this.setChanged();
        }

        public NonNullList<ItemStack> getItems() {
            return this.stacks;
        }
    }
}