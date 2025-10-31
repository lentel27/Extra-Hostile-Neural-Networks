package net.lmor.extrahnn.client.screen;

import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.data.DataModelRegistry;
import dev.shadowsoffire.hostilenetworks.gui.DeepLearnerScreen;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.screen.PlaceboContainerScreen;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.lmor.extrahnn.ExtraHostileNetworks;
import net.lmor.extrahnn.common.container.UltimateLootFabContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UltimateLootFabScreen extends PlaceboContainerScreen<UltimateLootFabContainer> {

    public static final int WIDTH = 176;
    public static final int HEIGHT = 178;
    public static final ResourceLocation BASE = ExtraHostileNetworks.local("textures/gui/ultimate_loot_fabricator.png");
    public static final ResourceLocation PLAYER = ExtraHostileNetworks.local("textures/gui/inventory.png");
    public static final WidgetSprites LEFT_BUTTON = DeepLearnerScreen.makeSprites("widget/fab_left", "widget/fab_left_hovered");
    public static final WidgetSprites RIGHT_BUTTON = DeepLearnerScreen.makeSprites("widget/fab_right", "widget/fab_right_hovered");

    private DynamicHolder<DataModel> model = DataModelRegistry.INSTANCE.emptyHolder();
    private int currentPage = 0;
    private ImageButton btnLeft, btnRight;

    public UltimateLootFabScreen(UltimateLootFabContainer menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageHeight = HEIGHT;
        this.imageWidth = WIDTH;
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
        this.btnLeft = this.addRenderableWidget(new ImageButton(this.getGuiLeft() + 13, this.getGuiTop() + 68, 29, 12, LEFT_BUTTON, btn -> {
            if (this.model.isBound() && this.currentPage > 0) this.currentPage--;
        }));

        this.btnRight = this.addRenderableWidget(new ImageButton(this.getGuiLeft() + 46, this.getGuiTop() + 68, 29, 12, RIGHT_BUTTON, btn -> {
            if (this.model.isBound() && this.currentPage < this.model.get().fabDrops().size() / 9) this.currentPage++;
        }));
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int pX, int pY) {

    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics graphics, int x, int y) {
        if (this.isHovering(6, 10, 7, 53, x, y)) {
            List<Component> txt = new ArrayList<>(2);
            txt.add(Component.translatable("hostilenetworks.gui.energy", this.menu.getEnergyStored(), ExtraHostileConfig.ultimateFabPowerCap));
            txt.add(Component.translatable("hostilenetworks.gui.fab_cost", ExtraHostileConfig.ultimateFabPowerCost));
            graphics.renderComponentTooltip(this.font, txt, x, y);
        }
        if (this.model.isBound()) {
            int selection = this.menu.getSelectedDrop(this.model.get());
            if (selection != -1 && this.isHovering(79, 5, 16, 16, x, y)) {
                graphics.renderComponentTooltip(this.font, List.of(Component.translatable("hostilenetworks.gui.clear")), x, y);
            }

            List<ItemStack> drops = this.model.get().fabDrops();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (i * 3 + j < Math.min(drops.size() - this.currentPage * 9, 9) && this.isHovering(18 + 18 * j, 10 + 18 * i, 16, 16, x, y)) {
                        this.drawOnLeft(graphics, getTooltipFromItem(Objects.requireNonNull(this.minecraft), drops.get(this.currentPage * 9 + i * 3 + j)), this.getGuiTop() + 15);
                    }
                }
            }
        }

        super.renderTooltip(graphics, x, y);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (this.model.isBound()) {
            List<ItemStack> drops = this.model.get().fabDrops();
            int selection = this.menu.getSelectedDrop(this.model.get());
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (i * 3 + j < drops.size() && this.isHovering(18 + 18 * j, 10 + 18 * i, 16, 16, x, y) && selection != i * 3 + j) {
                        Objects.requireNonNull(Minecraft.getInstance().gameMode).handleInventoryButtonClick(this.menu.containerId, this.currentPage * 9 + i * 3 + j);
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    }
                }
            }

            if (selection != -1 && this.isHovering(79, 5, 16, 16, x, y)) {
                Objects.requireNonNull(Minecraft.getInstance().gameMode).handleInventoryButtonClick(this.menu.containerId, -1);
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }

        }
        return super.mouseClicked(x, y, button);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int x, int y) {
        int left = this.getGuiLeft();
        int top = this.getGuiTop();

        graphics.blit(BASE, left, top, 0.0F, 0.0F, 176, 83, 256, 256);

        int energyHeight = Mth.floor(53.0F * ((float)this.menu.getEnergyStored() / (float)ExtraHostileConfig.ultimateFabPowerCap));
        graphics.blit(BASE, left + 6, top + 10 + 53 - energyHeight, 0.0F, 83.0F, 7, energyHeight, 256, 256);

        int progHeight = Mth.floor(35.0F * (float)this.menu.getRuntime() / ExtraHostileConfig.ultimateFabPowerDuration);
        graphics.blit(BASE, left + 84, top + 23 + 35 - progHeight, 7.0F, 83.0F, 6, progHeight, 256, 256);

        graphics.blit(PLAYER, left, top + 84, 0.0F, 0.0F, 176, 90, 256, 256);

        if (this.model.isBound()) {
            List<ItemStack> drops = this.model.get().fabDrops();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (i * 3 + j < Math.min(drops.size() - this.currentPage * 9, 9) && this.isHovering(18 + 18 * j, 10 + 18 * i, 16, 16, x, y)) {
                        graphics.blit(BASE, left + 16 + 19 * j, top + 8 + 19 * i, 13, 83, 18, 18, 256, 256);
                    }
                }
            }

            int selection = this.menu.getSelectedDrop(this.model.get());

            if (selection != -1 && selection / 9 == this.currentPage) {
                int selIdx = selection - this.currentPage * 9;
                graphics.blit(BASE, left + 16 + 19 * (selIdx % 3), top + 8 + 19 * (selIdx / 3), 31, 83, 18, 18, 256, 256);
            }

            if (selection != -1) {
                graphics.renderItem(drops.get(selection), left + 79, top + 5);
                graphics.renderItemDecorations(this.font, drops.get(selection), left + 79 - 1, top + 5 - 1);
            }

            left += 17;
            top += 9;
            int xItem = 0;
            int yItem = 0;
            for (int i = 0; i < Math.min(drops.size() - this.currentPage * 9, 9); i++) {
                graphics.renderItem(drops.get(i + this.currentPage * 9), left + xItem * 19, top + yItem * 19);
                graphics.renderItemDecorations(this.font, drops.get(i + this.currentPage * 9), left + xItem * 19 - 1, top + yItem * 19 - 1);
                if (++xItem == 3) {
                    yItem++;
                    xItem = 0;
                }
            }

        }
    }

    public void drawOnLeft(GuiGraphics gfx, List<Component> list, int y) {
        if (list.isEmpty()) return;

        int xPos = this.getGuiLeft() - 16 - list.stream().map(this.font::width).max(Integer::compare).get();
        int maxWidth = 9999;
        if (xPos < 0) {
            maxWidth = this.getGuiLeft() - 6;
            xPos = -8;
        }

        List<FormattedText> split = new ArrayList<>();
        int lambdastupid = maxWidth;
        list.forEach(comp -> split.addAll(this.font.getSplitter().splitLines(comp, lambdastupid, comp.getStyle())));

        gfx.renderComponentTooltip(this.font, split, xPos, y, ItemStack.EMPTY);
    }

}
