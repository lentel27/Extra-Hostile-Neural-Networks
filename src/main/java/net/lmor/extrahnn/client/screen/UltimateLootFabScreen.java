package net.lmor.extrahnn.client.screen;

import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.data.DataModelRegistry;
import dev.shadowsoffire.hostilenetworks.gui.DeepLearnerScreen;
import dev.shadowsoffire.hostilenetworks.gui.LootFabMenu;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.hostilenetworks.util.Color;
import dev.shadowsoffire.hostilenetworks.util.FabSelection;
import dev.shadowsoffire.hostilenetworks.util.FabSelection.ProductionMode;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.screen.PlaceboContainerScreen;
import dev.shadowsoffire.placebo.util.DrawsOnLeft;
import net.lmor.extrahnn.ExtraHostileNetworks;
import net.lmor.extrahnn.common.container.UltimateLootFabContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DataFlowIssue")
public class UltimateLootFabScreen extends PlaceboContainerScreen<UltimateLootFabContainer> implements DrawsOnLeft {

    public static final int WIDTH = 212;
    public static final int HEIGHT = 210;
    public static final ResourceLocation BASE = ExtraHostileNetworks.local("textures/gui/ultimate_loot_fabricator.png");
    public static final ResourceLocation PLAYER = ExtraHostileNetworks.local("textures/gui/inventory.png");
    public static final WidgetSprites LEFT_BUTTON = DeepLearnerScreen.makeSprites("widget/fab_left", "widget/fab_left_hovered");
    public static final WidgetSprites RIGHT_BUTTON = DeepLearnerScreen.makeSprites("widget/fab_right", "widget/fab_right_hovered");
    private static final ResourceLocation CLEAR_QUEUE_ICON = ResourceLocation.withDefaultNamespace("textures/item/barrier.png");

    private DynamicHolder<DataModel> model = DataModelRegistry.INSTANCE.emptyHolder();
    private int currentPage = 0;
    private ImageButton btnLeft, btnRight;

    private static final int PREVIEW_X = 79, PREVIEW_Y = 15;
    private static final int MODE_X = WIDTH + 4, MODE_Y = 0;
    private static final int CLEAR_X = MODE_X + 20, CLEAR_Y = MODE_Y;
    private static final int QUEUE_X = WIDTH + 4, QUEUE_Y = 30, GRID_SLOT_SIZE = 18;
    private static final int QUEUE_COLS = 4, QUEUE_ROWS = 4, QUEUE_VISIBLE = QUEUE_COLS * QUEUE_ROWS;
    private static final int QUEUE_LABEL_Y = 20;
    private static final int REDSTONE_X = -22, REDSTONE_Y = 0;

    public UltimateLootFabScreen(UltimateLootFabContainer menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageHeight = HEIGHT;
        this.imageWidth = WIDTH;
    }

    private FabSelection selection() {
        return this.model.isBound() ? this.menu.getSelection(this.model.get()) : FabSelection.EMPTY;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.model = DataModelItem.getStoredModel(this.menu.getSlot(0).getItem());

        if (this.model.isBound()) {
            this.btnLeft.visible = this.currentPage > 0;
            this.btnRight.visible = this.currentPage < this.model.get().fabDrops().size() / 9;
        }
        else {
            this.btnLeft.visible = false;
            this.btnRight.visible = false;
        }

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void init() {
        super.init();
        this.btnLeft = this.addRenderableWidget(new ImageButton(this.getGuiLeft() + 13, this.getGuiTop() + 90, 29, 12, LEFT_BUTTON, btn -> {
            if (this.model.isBound() && this.currentPage > 0) this.currentPage--;
        }));

        this.btnRight = this.addRenderableWidget(new ImageButton(this.getGuiLeft() + 46, this.getGuiTop() + 90, 29, 12, RIGHT_BUTTON, btn -> {
            if (this.model.isBound() && this.currentPage < this.model.get().fabDrops().size() / 9) this.currentPage++;
        }));
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int pX, int pY) {
        if (this.model.isBound() && this.selection().mode() == ProductionMode.QUEUE) {
            graphics.drawString(this.font, Component.translatable("hostilenetworks.gui.queue_current"), QUEUE_X, QUEUE_LABEL_Y, Color.AQUA);
        }
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics graphics, int x, int y) {
        if (this.isHovering(6, 33, 7, 53, x, y)) {
            List<Component> txt = new ArrayList<>(2);
            txt.add(Component.translatable("hostilenetworks.gui.energy", this.menu.getEnergyStored(), this.menu.getMaxEnergyStored()));
            txt.add(Component.translatable("hostilenetworks.gui.fab_cost", this.menu.getEnergyCost()));
            graphics.renderComponentTooltip(this.font, txt, x, y);
        }

        if (this.isHovering(84, 35, 6, 35, x, y)){
            graphics.renderComponentTooltip(this.font,
                    List.of(
                            Component.translatable("extrahnn.info.progress", this.menu.getProgress(), this.menu.getMaxProgress())
                    ),
                    x, y
            );
        }

        if (this.isHovering(REDSTONE_X, REDSTONE_Y, 18, 18, x, y)) {
            graphics.renderTooltip(this.font, Component.translatable(this.menu.getRedstoneState().getKey()), x, y);
        }

        if (this.model.isBound()) {
            FabSelection sel = this.selection();
            boolean queue = sel.mode() == ProductionMode.QUEUE;
            List<ItemStack> drops = this.model.get().fabDrops();

            if (this.isHovering(MODE_X, MODE_Y, 18, 18, x, y)) {
                graphics.renderTooltip(this.font, Component.translatable(sel.mode().getKey()), x, y);
            }

            int selection = this.menu.getSelectedDrop(this.model.get());
            if (selection != -1 && this.isHovering(PREVIEW_X, PREVIEW_Y, 16, 16, x, y)) {
                if (queue) {
                    List<Component> txt = new ArrayList<>(getTooltipFromItem(this.minecraft, drops.get(selection)));
                    txt.add(Component.translatable("hostilenetworks.gui.queue_clear_current"));
                    graphics.renderComponentTooltip(this.font, txt, x, y);
                }
                else {
                    graphics.renderComponentTooltip(this.font, List.of(Component.translatable("hostilenetworks.gui.clear")), x, y);
                }
            }

            if (queue) renderQueue(graphics, x, y, sel, drops);
            drawItemGrid(graphics, x, y, drops, queue);
        }

        super.renderTooltip(graphics, x, y);
    }

    private void renderQueue(@NotNull GuiGraphics graphics, int x, int y, FabSelection sel, List<ItemStack> drops){
        if (this.isHovering(CLEAR_X, CLEAR_Y, 18, 18, x, y)) {
            graphics.renderTooltip(this.font, Component.translatable("hostilenetworks.gui.clear_queue"), x, y);
        }

        List<Integer> entries = sel.entries();
        int size = entries.size();
        boolean hasOverflow = size > QUEUE_VISIBLE;
        int visibleEntries = hasOverflow ? QUEUE_VISIBLE - 1 : size;
        for (int i = 0; i < visibleEntries; i++) {
            int col = i % QUEUE_COLS;
            int row = i / QUEUE_COLS;
            if (this.isHovering(QUEUE_X + col * GRID_SLOT_SIZE, QUEUE_Y + row * GRID_SLOT_SIZE, 16, 16, x, y)) {
                int dropIdx = entries.get(i);
                if (dropIdx >= 0 && dropIdx < drops.size()) {
                    List<Component> txt = new ArrayList<>(getTooltipFromItem(this.minecraft, drops.get(dropIdx)));
                    txt.add(Component.translatable("hostilenetworks.gui.queue_remove"));
                    graphics.renderComponentTooltip(this.font, txt, x, y);
                }
            }
        }

        if (hasOverflow) {
            int col = (QUEUE_VISIBLE - 1) % QUEUE_COLS;
            int row = (QUEUE_VISIBLE - 1) / QUEUE_COLS;
            if (this.isHovering(QUEUE_X + col * GRID_SLOT_SIZE, QUEUE_Y + row * GRID_SLOT_SIZE, 16, 16, x, y)) {
                List<Component> txt = new ArrayList<>();
                for (int j = visibleEntries; j < size; j++) {
                    int dropIdx = entries.get(j);
                    if (dropIdx >= 0 && dropIdx < drops.size()) {
                        txt.add(drops.get(dropIdx).getHoverName());
                    }
                }
                if (!txt.isEmpty()) {
                    graphics.renderComponentTooltip(this.font, txt, x, y);
                }
            }
        }

    }

    private void drawItemGrid(@NotNull GuiGraphics graphics, int x, int y, List<ItemStack> drops, boolean queue){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i * 3 + j < Math.min(drops.size() - this.currentPage * 9, 9) && this.isHovering(18 + 18 * j, 10 + 18 * i, 16, 16, x, y)) {
                    List<Component> tip = new ArrayList<>(getTooltipFromItem(this.minecraft, drops.get(this.currentPage * 9 + i * 3 + j)));
                    if (queue) tip.add(Component.translatable("hostilenetworks.gui.queue_add"));

                    graphics.pose().pushPose();
                    graphics.pose().translate(-4, 0, 0);

                    this.drawOnLeft(graphics, tip, this.getGuiTop() + 33, Math.min(this.getGuiLeft(), 240));

                    graphics.pose().popPose();
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (this.isHovering(REDSTONE_X, REDSTONE_Y, 18, 18, x, y)) {
            this.click(LootFabMenu.REDSTONE_BASE + this.menu.getRedstoneState().next().ordinal());
        }

        if (this.model.isBound()) {
            List<ItemStack> drops = this.model.get().fabDrops();
            FabSelection sel = this.selection();
            boolean queue = sel.mode() == ProductionMode.QUEUE;
            int selection = this.menu.getSelectedDrop(this.model.get());

            if (this.isHovering(MODE_X, MODE_Y, 18, 18, x, y)) {
                this.click(LootFabMenu.BTN_CYCLE_MODE);
            }

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    int idx = this.currentPage * 9 + i * 3 + j;
                    if (idx < drops.size() && this.isHovering(18 + 18 * j, 28 + 18 * i, 16, 16, x, y)) {
                        if (queue || selection != idx) this.click(LootFabMenu.DROP_BASE + idx);
                    }
                }
            }

            if (selection != -1 && this.isHovering(PREVIEW_X, PREVIEW_Y, 16, 16, x, y)) {
                if (queue && !sel.entries().isEmpty())
                    this.click(LootFabMenu.QUEUE_REMOVE_BASE + sel.cursor() % sel.entries().size());
                else if (!queue) this.click(LootFabMenu.BTN_CLEAR_FIXED);
            }

            if (queue) clickQueue(x, y, sel);
        }
        return super.mouseClicked(x, y, button);
    }

    public void clickQueue(double x, double y, FabSelection sel){
        if (this.isHovering(CLEAR_X, CLEAR_Y, 18, 18, x, y)) {
            this.click(LootFabMenu.BTN_CLEAR_QUEUE);
        }

        List<Integer> entries = sel.entries();
        int size = entries.size();
        boolean hasOverflow = size > QUEUE_VISIBLE;
        int visibleEntries = hasOverflow ? QUEUE_VISIBLE - 1 : size;
        for (int i = 0; i < visibleEntries; i++) {
            int col = i % QUEUE_COLS;
            int row = i / QUEUE_COLS;
            if (this.isHovering(QUEUE_X + col * GRID_SLOT_SIZE, QUEUE_Y + row * GRID_SLOT_SIZE, 18, 18, x, y))
                this.click(LootFabMenu.QUEUE_REMOVE_BASE + i);
        }
    }

    private void click(int id) {
        Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int x, int y) {
        int left = this.getGuiLeft();
        int top = this.getGuiTop();

        graphics.blit(BASE, left, top, 0.0F, 0.0F, 212, 119, 256, 256);

        int energyHeight = Mth.floor(53.0F * ((float)this.menu.getEnergyStored() / (float) this.menu.getMaxEnergyStored()));
        graphics.blit(BASE, left + 6, top + 33 + 53 - energyHeight, 0.0F, 119, 7, energyHeight, 256, 256);

        int progHeight = Mth.floor(35.0F * (float)this.menu.getRuntime() / this.menu.getDuration());
        graphics.blit(BASE, left + 84, top + 35 + 35 - progHeight, 7.0F, 119, 6, progHeight, 256, 256);

        graphics.blit(PLAYER, left + 18, top + 120, 0.0F, 0.0F, 176, 90, 256, 256);

        graphics.blit(BASE, left + REDSTONE_X, top + REDSTONE_Y, 31, 119, 18, 18, 256, 256);
        graphics.blit(this.menu.getRedstoneState().getResourceLocation(), left + REDSTONE_X + 1, top + REDSTONE_Y + 1, 0, 0, 16, 16, 16, 16);

        if (this.model.isBound()) {
            List<ItemStack> drops = this.model.get().fabDrops();
            FabSelection sel = this.selection();
            boolean queue = sel.mode() == ProductionMode.QUEUE;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (i * 3 + j < Math.min(drops.size() - this.currentPage * 9, 9) && this.isHovering(18 + 18 * j, 31 + 18 * i, 16, 16, x, y)) {
                        graphics.blit(BASE, left + 16 + 19 * j, top + 31 + 19 * i, 13, 119, 18, 18, 256, 256);
                    }
                }
            }

            int selection = this.menu.getSelectedDrop(this.model.get());

            if (!queue && selection != -1 && selection / 9 == this.currentPage) {
                int selIdx = selection - this.currentPage * 9;
                graphics.blit(BASE, left + 16 + 19 * (selIdx % 3), top + 31 + 19 * (selIdx / 3), 31, 119, 18, 18, 256, 256);
            }

            if (selection != -1) {
                graphics.renderItem(drops.get(selection), left + PREVIEW_X, top + PREVIEW_Y);
                graphics.renderItemDecorations(this.font, drops.get(selection), left + PREVIEW_X - 1, top + PREVIEW_Y - 1);
            }

            if (queue) this.renderQueueGrid(graphics, left, top, sel, drops);

            int gridLeft = left + 17;
            int gridTop = top + 9;
            for (int i = 0; i < Math.min(drops.size() - this.currentPage * 9, 9); i++) {
                int dX = i % 3;
                int dY = i / 3;
                graphics.renderItem(drops.get(i + this.currentPage * 9), gridLeft + dX * 19, gridTop + 23 + dY * 19);
                graphics.renderItemDecorations(this.font, drops.get(i + this.currentPage * 9), gridLeft + dX * 19 - 1, gridTop + 23 + dY * 19 - 1);
            }

            graphics.blit(BASE, left + MODE_X, top + MODE_Y, 31, 119, 18, 18, 256, 256);
            graphics.blit(sel.mode().getResourceLocation(), left + MODE_X + 1, top + MODE_Y + 1, 0, 0, 16, 16, 16, 16);

            if (queue) {
                graphics.blit(BASE, left + CLEAR_X, top + CLEAR_Y, 31, 119, 18, 18, 256, 256);
                graphics.blit(CLEAR_QUEUE_ICON, left + CLEAR_X + 1, top + CLEAR_Y + 1, 0, 0, 16, 16, 16, 16);
            }
        }
    }

    private void renderQueueGrid(GuiGraphics gfx, int left, int top, FabSelection sel, List<ItemStack> drops) {
        List<Integer> entries = sel.entries();
        int size = entries.size();
        boolean hasOverflow = size > QUEUE_VISIBLE;
        int visibleEntries = hasOverflow ? QUEUE_VISIBLE - 1 : size;
        int cursor = entries.isEmpty() ? -1 : sel.cursor() % size;

        for (int i = 0; i < QUEUE_VISIBLE; i++) {
            int col = i % QUEUE_COLS;
            int row = i / QUEUE_COLS;
            int cx = left + QUEUE_X + col * GRID_SLOT_SIZE;
            int cy = top + QUEUE_Y + row * GRID_SLOT_SIZE;
            boolean isCursor = i < visibleEntries && i == cursor;
            gfx.blit(BASE, cx, cy, isCursor ? 31 : 13, 119, 18, 18, 256, 256);

            if (i < visibleEntries) {
                int dropIdx = entries.get(i);
                if (dropIdx >= 0 && dropIdx < drops.size()) {
                    gfx.renderItem(drops.get(dropIdx), cx + 1, cy + 1);
                    gfx.renderItemDecorations(this.font, drops.get(dropIdx), cx, cy);
                }
            }
            else if (i == QUEUE_VISIBLE - 1 && hasOverflow) {
                String txt = "+" + (size - visibleEntries);
                int textX = cx + 1 + (16 - this.font.width(txt)) / 2;
                int textY = cy + 2 + (16 - this.font.lineHeight) / 2;
                gfx.drawString(this.font, txt, textX, textY, Color.WHITE, true);
            }
        }
    }
}
