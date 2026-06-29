package net.lmor.extrahnn.common.tile;

import dev.shadowsoffire.hostilenetworks.Hostile;
import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.data.DataModelRegistry;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.hostilenetworks.tile.LootFabTileEntity;
import dev.shadowsoffire.hostilenetworks.util.FabSelection;
import dev.shadowsoffire.hostilenetworks.util.FabSelection.ProductionMode;
import dev.shadowsoffire.hostilenetworks.util.RedstoneState;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import dev.shadowsoffire.placebo.cap.ModifiableEnergyStorage;
import dev.shadowsoffire.placebo.menu.SimpleDataSlots;
import dev.shadowsoffire.placebo.menu.SimpleDataSlots.IDataAutoRegister;
import dev.shadowsoffire.placebo.network.VanillaPacketDispatcher;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import lombok.Getter;
import lombok.Setter;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.lmor.extrahnn.api.IRegTile;
import net.lmor.extrahnn.api.ISettingCard;
import net.lmor.extrahnn.api.Version;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class UltimateLootFabTileEntity extends BlockEntity implements TickingBlockEntity, IDataAutoRegister, IRegTile, ISettingCard {
    @Getter
    protected final FabItemHandler inventory = new FabItemHandler();
    @Getter
    protected final ModifiableEnergyStorage energy;
    @Getter
    protected final Map<DynamicHolder<DataModel>, FabSelection> savedSelections = new HashMap<>();
    protected final SimpleDataSlots data = new SimpleDataSlots();
    @Getter
    @Setter
    protected RedstoneState redstoneState = RedstoneState.IGNORED;

    @Getter
    protected int runtime = 0;
    protected int currentSel = -1;

    private boolean checkOutput = true;
    private Version version;

    public int CAP;
    public int COST;
    public int DURATION;

    public UltimateLootFabTileEntity(BlockPos pos, BlockState state, BlockEntityType<UltimateLootFabTileEntity> type, Version version) {
        super(type, pos, state);
        this.version = version;

        setConfig();
        energy = new ModifiableEnergyStorage(CAP, CAP);

        this.data.addData(() -> this.runtime, v -> this.runtime = v);
        this.data.addData(() -> this.redstoneState.ordinal(), v -> this.redstoneState = RedstoneState.values()[v]);
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
        if (!dm.isBound()) {
            this.runtime = 0;
            return;
        }

        int selection = this.getSelectedDrop(dm.get());
        if (this.currentSel != selection) {
            this.currentSel = selection;
            this.runtime = 0;
            return;
        }

        if (selection == -1) {
            this.runtime = 0;
            return;
        }

        if (!this.redstoneState.matches(level.hasNeighborSignal(this.worldPosition))) return;

        if (this.runtime >= DURATION) tryBatchOutput(dm, selection);
        else {
            if (this.energy.getEnergyStored() < COST) return;
            this.energy.setEnergy(this.energy.getEnergyStored() - COST);
            this.runtime++;
            this.setChanged();
        }
    }

    private void tryBatchOutput(DynamicHolder<DataModel> dm, int selection) {
        if (!checkOutput) return;
        checkOutput = false;

        int maxCount = Math.min(this.inventory.getStackInSlot(0).getCount(), 4 * version.getMultiplier());
        if (maxCount == 0) return;

        ItemStack out = dm.get().fabDrops().get(selection);
        int outCount = out.getCount();

        while (maxCount > 0) {
            ItemStack candidate = out.copyWithCount(outCount * maxCount);
            if (this.insertInOutput(candidate, true)) {
                this.runtime = 0;
                this.insertInOutput(candidate, false);
                this.inventory.getStackInSlot(0).shrink(maxCount);
                this.advanceQueue(dm);
                this.setChanged();
                return;
            }
            maxCount /= 2;
        }
    }

    protected boolean insertInOutput(ItemStack stack, boolean sim) {
        int amount = stack.getCount();
        int maxStackSize = stack.getMaxStackSize();

        for (int i = 1; i < 37; ++i) {
            ItemStack slotStack = this.inventory.getStackInSlot(i);

            int toInsert;
            if (slotStack.isEmpty()) toInsert = Math.min(amount, maxStackSize);
            else if (slotStack.is(stack.getItem()) && slotStack.getCount() < slotStack.getMaxStackSize()) {
                toInsert = Math.min(amount, slotStack.getMaxStackSize() - slotStack.getCount());
            } else continue;

            if (!sim) {
                ItemStack insertItem = stack.copyWithCount(toInsert);
                this.inventory.insertItemInternal(i, insertItem, false);
            }
            amount -= toInsert;

            if (amount <= 0) return true;
        }
        return false;
    }

    public int getEnergyStored() {
        return this.energy.getEnergyStored();
    }

    public FabSelection getSelection(DataModel model) {
        return this.savedSelections.getOrDefault(DataModelRegistry.INSTANCE.holder(model), FabSelection.EMPTY);
    }

    public void cycleMode(DynamicHolder<DataModel> model) {
        FabSelection sel = this.savedSelections.getOrDefault(model, FabSelection.EMPTY);
        FabSelection updated;
        if (sel.mode().next() == ProductionMode.QUEUE)
            updated = new FabSelection(ProductionMode.QUEUE, List.copyOf(sel.entries()), 0);
        else {
            int current = sel.current();
            updated = current == -1 ? FabSelection.EMPTY : FabSelection.fixed(current);
        }
        this.savedSelections.put(model, updated);
        this.runtime = 0;
        this.sync();
    }

    public void setFixedDrop(DynamicHolder<DataModel> model, int index) {
        if (index == -1) this.savedSelections.remove(model);
        else this.savedSelections.put(model, FabSelection.fixed(Mth.clamp(index, 0, model.get().fabDrops().size() - 1)));
        this.sync();
    }

    public void appendToQueue(DynamicHolder<DataModel> model, int index) {
        DataModel dm = model.get();
        if (dm == null || index < 0 || index >= dm.fabDrops().size()) return;

        FabSelection sel = this.savedSelections.getOrDefault(model, FabSelection.EMPTY);
        List<Integer> entries = new ArrayList<>(sel.entries());

        entries.add(index);
        this.savedSelections.put(model, new FabSelection(ProductionMode.QUEUE, entries, sel.cursor()));

        this.sync();
    }

    public void clearQueue(DynamicHolder<DataModel> model) {
        FabSelection sel = this.savedSelections.get(model);
        if (sel == null || sel.mode() != ProductionMode.QUEUE) return;

        this.savedSelections.put(model, new FabSelection(ProductionMode.QUEUE, List.of(), 0));

        this.sync();
    }

    public void removeFromQueue(DynamicHolder<DataModel> model, int pos) {
        FabSelection sel = this.savedSelections.get(model);
        if (sel == null || pos < 0 || pos >= sel.entries().size()) return;

        List<Integer> entries = new ArrayList<>(sel.entries());
        entries.remove(pos);
        int cursor = sel.cursor();
        if (pos < cursor) cursor--;

        cursor = entries.isEmpty() ? 0 : Math.floorMod(cursor, entries.size());
        this.savedSelections.put(model, new FabSelection(ProductionMode.QUEUE, entries, cursor));

        this.sync();
    }

    private void advanceQueue(DynamicHolder<DataModel> model) {
        FabSelection sel = this.savedSelections.get(model);

        if (sel != null && sel.mode() == ProductionMode.QUEUE) {
            this.savedSelections.put(model, sel.advanced());
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
        }
    }

    private void sync() {
        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
        this.setChanged();
    }

    @Override
    public CompoundTag saveSetting(HolderLookup.@NotNull Provider regs) {
        CompoundTag tag = new CompoundTag();

        CompoundTag selectionsTag = new CompoundTag();
        this.writeSelections(selectionsTag);
        tag.put("selections", selectionsTag);
        tag.putInt("redstoneState", this.redstoneState.ordinal());

        tag.putString("config", getClass().getName());
        return tag;
    }

    @Override
    public boolean loadSetting(HolderLookup.@NotNull Provider regs, CompoundTag tag, Player player) {
        if (!tag.contains("config")
                || (!tag.getString("config").equals(getClass().getName())
                && !tag.getString("config").equals(LootFabTileEntity.class.getName()))
        ) return false;

        this.readSelections(tag.getCompound("selections"));
        this.redstoneState = RedstoneState.values()[tag.getInt("redstoneState")];

        this.sync();
        return true;
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider regs) {
        super.saveAdditional(tag, regs);
        tag.put("saved_selections", this.writeSelections(new CompoundTag()));
        tag.put("inventory", this.inventory.serializeNBT(regs));
        tag.putInt("energy", this.energy.getEnergyStored());
        tag.putInt("runtime", this.runtime);
        tag.putInt("selection", this.currentSel);
        tag.putInt("redstoneState", this.redstoneState.ordinal());

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
        this.redstoneState = RedstoneState.values()[tag.getInt("redstoneState")];

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
        for (Map.Entry<DynamicHolder<DataModel>, FabSelection> e : this.savedSelections.entrySet()) {
            Tag encoded = FabSelection.CODEC.encodeStart(NbtOps.INSTANCE, e.getValue()).getOrThrow();
            tag.put(e.getKey().getId().toString(), encoded);
        }
        return tag;
    }

    private void readSelections(CompoundTag tag) {
        this.savedSelections.clear();
        for (String s : tag.getAllKeys()) {
            DynamicHolder<DataModel> dm = DataModelRegistry.INSTANCE.holder(ResourceLocation.tryParse(s));
            Tag value = tag.get(s);
            FabSelection sel;
            if (value instanceof IntTag intTag) sel = FabSelection.fixed(intTag.getAsInt());
            else sel = FabSelection.CODEC.parse(NbtOps.INSTANCE, value).result().orElse(FabSelection.EMPTY);
            this.savedSelections.put(dm, sel);
        }
    }

    public int getSelectedDrop(DataModel model) {
        if (model == null) return -1;
        FabSelection sel = this.savedSelections.get(DataModelRegistry.INSTANCE.holder(model));
        if (sel == null) return -1;
        int index = sel.current();
        if (index < 0 || index >= model.fabDrops().size()) return -1;
        return index;
    }

    public class FabItemHandler extends InternalItemHandler {

        public FabItemHandler() {
            super(37);
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