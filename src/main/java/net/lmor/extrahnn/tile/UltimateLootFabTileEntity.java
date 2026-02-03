package net.lmor.extrahnn.tile;

import dev.shadowsoffire.hostilenetworks.Hostile;
import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.data.DataModelRegistry;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import dev.shadowsoffire.placebo.cap.ModifiableEnergyStorage;
import dev.shadowsoffire.placebo.menu.SimpleDataSlots;
import dev.shadowsoffire.placebo.recipe.VanillaPacketDispatcher;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.lmor.extrahnn.api.ISettingCard;
import net.lmor.extrahnn.api.Version;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

public class UltimateLootFabTileEntity extends BlockEntity implements TickingBlockEntity, SimpleDataSlots.IDataAutoRegister, ISettingCard {
    protected final FabItemHandler inventory = new FabItemHandler();
    protected final ModifiableEnergyStorage energy;
    protected final Object2IntMap<DynamicHolder<DataModel>> savedSelections = new Object2IntOpenHashMap<>();
    protected final SimpleDataSlots data = new SimpleDataSlots();
    protected int runtime = 0;
    protected int currentSel = -1;
    private boolean checkOutput = true;

    private Version version;

    public int CAP;
    public int COST;
    public int DURATION;

    public UltimateLootFabTileEntity(BlockPos pos, BlockState state, BlockEntityType<?> type, Version version) {
        super(type, pos, state);
        this.version = version;

        setConfig();
        energy = new ModifiableEnergyStorage(CAP, CAP);

        this.savedSelections.defaultReturnValue(-1);
        this.data.addData(() -> this.runtime, v -> this.runtime = v);
        this.data.addEnergy(this.energy);
    }

    public void registerSlots(Consumer<DataSlot> consumer) {
        this.data.register(consumer);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        // Forced inventory size update, because it causes a world crash
        // For those updating the mod
        if (inventory.getSlots() == 17) inventory.setSize(37);

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
                if (this.runtime >= DURATION) {
                    if (checkOutput){
                        checkOutput = false;
                        int maxCount = Math.min(this.inventory.getStackInSlot(0).getCount(), 4 * version.getMultiplier());
                        if (maxCount != 0){
                            ItemStack out = (dm.get().fabDrops().get(selection)).copy();
                            ItemStack outCopy = out.copy();
                            outCopy.setCount(out.getCount() * maxCount);


                            int old_maxCount = 0;
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
                                maxCount = (int) Math.ceil(((double) maxCount / 2));
                                outCopy.setCount(out.getCount() * maxCount);

                                // Just in case, protection against looping
                                if (old_maxCount == maxCount){
                                    if (cycle != 0) break;
                                    cycle++;
                                }
                            }
                        }

                    }

                } else {
                    if (this.energy.getEnergyStored() < COST) {
                        return;
                    }

                    this.energy.setEnergy(this.energy.getEnergyStored() - COST);
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
        int amount = stack.getCount();
        for(int i = 1; i < 37; ++i) {
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

    public void setSelection(DynamicHolder<DataModel> model, int selection) {
        if (selection == -1) {
            this.savedSelections.removeInt(model);
        } else {
            this.savedSelections.put(model, Mth.clamp(selection, 0, model.get().fabDrops().size() - 1));
        }

        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
        this.setChanged();
    }

    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return LazyOptional.of(() -> this.inventory).cast();
        if (cap == ForgeCapabilities.ENERGY) return LazyOptional.of(() -> this.energy).cast();
        return super.getCapability(cap, side);
    }

    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("saved_selections", this.writeSelections(new CompoundTag()));
        tag.put("inventory", this.inventory.serializeNBT());
        tag.putInt("energy", this.energy.getEnergyStored());
        tag.putInt("runtime", this.runtime);
        tag.putInt("selection", this.currentSel);

        tag.putString("versionBlockEntity", this.version.getId());
    }

    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.readSelections(tag.getCompound("saved_selections"));
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.energy.setEnergy(tag.getInt("energy"));
        this.runtime = tag.getInt("runtime");
        this.currentSel = tag.getInt("selection");

        this.version = Version.getVersion(tag.getString("versionBlockEntity"));
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, t -> ((UltimateLootFabTileEntity) t).writeSync());
    }

    private CompoundTag writeSync() {
        CompoundTag tag = new CompoundTag();
        tag.put("saved_selections", this.writeSelections(new CompoundTag()));
        return tag;
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.readSelections(Objects.requireNonNull(pkt.getTag()).getCompound("saved_selections"));
    }

    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put("saved_selections", this.writeSelections(new CompoundTag()));
        return tag;
    }

    private CompoundTag writeSelections(CompoundTag tag) {
        for (Object2IntMap.Entry<DynamicHolder<DataModel>> e : this.savedSelections.object2IntEntrySet()) {
            tag.putInt((e.getKey()).getId().toString(), e.getIntValue());
        }
        return tag;
    }

    private void readSelections(CompoundTag tag) {
        this.savedSelections.clear();

        for (String s : tag.getAllKeys()) {
            DynamicHolder<DataModel> dm = DataModelRegistry.INSTANCE.holder(new ResourceLocation(s));
            this.savedSelections.put(dm, tag.getInt(s));
        }
    }

    public int getEnergyStored() {
        return this.energy.getEnergyStored();
    }

    public int getRuntime() {
        return this.runtime;
    }

    public int getSelectedDrop(DataModel model) {
        if (model == null) {
            return -1;
        } else {
            int index = this.savedSelections.getInt(DataModelRegistry.INSTANCE.holder(model));
            return index >= model.fabDrops().size() ? -1 : index;
        }
    }

    @Override
    public CompoundTag saveSetting() {
        CompoundTag tag = new CompoundTag();

        CompoundTag saveTag = new CompoundTag();
        this.writeSelections(saveTag);
        tag.put("selections", saveTag);

        return tag;
    }

    @Override
    public boolean loadSetting(CompoundTag tag, Player player) {
        if (!tag.contains("selections")) return false;

        this.readSelections(tag.getCompound("selections"));
        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
        this.setChanged();
        return true;
    }

    public class FabItemHandler extends InternalItemHandler {
        public FabItemHandler() {
            super(37);
        }

        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0) {
                return stack.getItem() == Hostile.Items.PREDICTION.get();
            } else {
                return true;
            }
        }

        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return slot > 0 ? stack : super.insertItem(slot, stack, simulate);
        }

        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot == 0 ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
        }

        protected void onContentsChanged(int slot) {
            checkOutput = true;
            UltimateLootFabTileEntity.this.setChanged();
        }
    }

    public void setConfig(){
        switch (version.getId().toLowerCase()) {
            case "v2" -> {
                CAP = ExtraHostileConfig.ultimateFabV2PowerCap;
                DURATION = ExtraHostileConfig.ultimateFabV2PowerDuration;
                COST = ExtraHostileConfig.ultimateFabV2PowerCost;
            }
            case "v3" -> {
                CAP = ExtraHostileConfig.ultimateFabV3PowerCap;
                DURATION = ExtraHostileConfig.ultimateFabV3PowerDuration;
                COST = ExtraHostileConfig.ultimateFabV3PowerCost;
            }
            case "v4" -> {
                CAP = ExtraHostileConfig.ultimateFabV4PowerCap;
                DURATION = ExtraHostileConfig.ultimateFabV4PowerDuration;
                COST = ExtraHostileConfig.ultimateFabV4PowerCost;
            }
            default -> {
                CAP = ExtraHostileConfig.ultimateFabV1PowerCap;
                DURATION = ExtraHostileConfig.ultimateFabV1PowerDuration;
                COST = ExtraHostileConfig.ultimateFabV1PowerCost;
            }
        }
    }
}
