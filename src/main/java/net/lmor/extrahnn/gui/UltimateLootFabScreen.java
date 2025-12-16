package net.lmor.extrahnn.gui;

import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.data.DataModelRegistry;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.screen.PlaceboContainerScreen;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.lmor.extrahnn.ExtraHostileNetworks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class UltimateLootFabScreen extends PlaceboContainerScreen<UltimateLootFabContainer> {
    public static final int WIDTH = 212;
    public static final int HEIGHT = 210;
    private static final ResourceLocation BASE = ExtraHostileNetworks.local("textures/gui/ultimate_loot_fabricator.png");
    private static final ResourceLocation PLAYER = ExtraHostileNetworks.local("textures/gui/inventory.png");
    private DynamicHolder<DataModel> model;
    private int currentPage;
    private ImageButton btnLeft;
    private ImageButton btnRight;

    public UltimateLootFabScreen(UltimateLootFabContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.model = DataModelRegistry.INSTANCE.holder(new ResourceLocation("empty", "empty"));
        this.currentPage = 0;
        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;
    }

    public void render(GuiGraphics gfx, int pMouseX, int pMouseY, float pPartialTicks) {
        this.model = DataModelItem.getStoredModel(this.menu.getSlot(0).getItem());
        if (this.model.isBound()) {
            this.btnLeft.visible = this.currentPage > 0;
            this.btnRight.visible = this.currentPage < this.model.get().fabDrops().size() / 9;
        } else {
            this.btnLeft.visible = false;
            this.btnRight.visible = false;
        }

        super.render(gfx, pMouseX, pMouseY, pPartialTicks);
    }

    public void init() {
        super.init();
        this.btnLeft = this.addRenderableWidget(new ImageButton(this.getGuiLeft() + 13, this.getGuiTop() + 90, 29, 12, 49, 119, 12, BASE, (btn) -> {
            if (this.model.isBound() && this.currentPage > 0) {
                --this.currentPage;
            }

        }));
        this.btnRight = this.addRenderableWidget(new ImageButton(this.getGuiLeft() + 46, this.getGuiTop() + 90, 29, 12, 78, 119, 12, BASE, (btn) -> {
            if (this.model.isBound() && this.currentPage < this.model.get().fabDrops().size() / 9) {
                ++this.currentPage;
            }
        }));
    }

    protected void renderLabels(GuiGraphics gfx, int pX, int pY) {
    }

    protected void renderTooltip(GuiGraphics gfx, int pX, int pY) {
        if (this.isHovering(6, 33, 7, 53, pX, pY)) {
            List<Component> txt = new ArrayList<>(2);
            txt.add(Component.translatable("hostilenetworks.gui.energy", this.menu.getEnergyStored(), ExtraHostileConfig.ultimateFabPowerCap));
            txt.add(Component.translatable("hostilenetworks.gui.fab_cost", ExtraHostileConfig.ultimateFabPowerCost));
            gfx.renderComponentTooltip(this.font, txt, pX, pY);
        }

        if (this.model.isBound()) {
            int selection = this.menu.getSelectedDrop(this.model.get());
            if (selection != -1 && this.isHovering(79, 15, 16, 16, pX, pY)) {
                gfx.renderComponentTooltip(this.font, List.of(Component.translatable("hostilenetworks.gui.clear")), pX, pY);
            }

            List<ItemStack> drops = this.model.get().fabDrops();

            for(int y = 0; y < 3; ++y) {
                for(int x = 0; x < 3; ++x) {
                    if (y * 3 + x < Math.min(drops.size() - this.currentPage * 9, 9) && this.isHovering(18 + 18 * x, 28 + 18 * y, 16, 16, pX, pY)) {
                        assert this.minecraft != null;
                        this.drawOnLeft(gfx, getTooltipFromItem(this.minecraft, drops.get(this.currentPage * 9 + y * 3 + x)), this.getGuiTop() + 33);
                    }
                }
            }
        }

        super.renderTooltip(gfx, pX, pY);
    }

    public boolean mouseClicked(double pX, double pY, int pButton) {
        if (this.model.isBound()) {
            List<ItemStack> drops = this.model.get().fabDrops();
            int selection = this.menu.getSelectedDrop(this.model.get());

            for(int y = 0; y < 3; ++y) {
                for(int x = 0; x < 3; ++x) {
                    if (y * 3 + x < drops.size() && this.isHovering(18 + 18 * x, 28 + 18 * y, 16, 16, pX, pY) && selection != y * 3 + x) {
                        Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, this.currentPage * 9 + y * 3 + x);
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    }
                }
            }

            if (selection != -1 && this.isHovering(79, 15, 16, 16, pX, pY)) {
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, -1);
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        }

        return super.mouseClicked(pX, pY, pButton);
    }

    protected void renderBg(GuiGraphics gfx, float pPartialTicks, int pX, int pY) {
        int left = this.getGuiLeft();
        int top = this.getGuiTop();
        gfx.blit(BASE, left, top, 0.0F, 0.0F, 212, 119, 256, 256);

        int energyHeight = Mth.floor(53.0F * ((float)this.menu.getEnergyStored() / (float)ExtraHostileConfig.ultimateFabPowerCap));
        gfx.blit(BASE, left + 6, top + 33 + 53 - energyHeight, 0.0F, 119, 7, energyHeight, 256, 256);

        int progHeight = Mth.floor(35.0F * (float)this.menu.getRuntime() / ExtraHostileConfig.ultimateFabPowerDuration);
        gfx.blit(BASE, left + 84, top + 35 + 35 - progHeight, 7.0F, 119, 6, progHeight, 256, 256);
        gfx.blit(PLAYER, left + 18, top + 120, 0.0F, 0.0F, 176, 90, 256, 256);

        if (this.model.isBound()) {
            List<ItemStack> drops = this.model.get().fabDrops();

            int selection;
            int x;
            for(selection = 0; selection < 3; ++selection) {
                for(x = 0; x < 3; ++x) {
                    if (selection * 3 + x < Math.min(drops.size() - this.currentPage * 9, 9) && this.isHovering(18 + 18 * x, 31 + 18 * selection, 16, 16, pX, pY)) {
                        gfx.blit(BASE, left + 16 + 19 * x, top + 31 + 19 * selection, 13.0F, 119, 18, 18, 256, 256);
                    }
                }
            }

            selection = this.menu.getSelectedDrop(this.model.get());
            if (selection != -1 && selection / 9 == this.currentPage) {
                x = selection - this.currentPage * 9;
                gfx.blit(BASE, left + 16 + 19 * (x % 3), top + 31 + 19 * (x / 3), 31.0F, 119, 18, 18, 256, 256);
            }

            if (selection != -1) {
                gfx.renderItem(drops.get(selection), left + 79, top + 15);
                gfx.renderItemDecorations(this.font, drops.get(selection), left + 79 - 1, top + 15 - 1);
            }

            left += 17;
            top += 9;
            x = 0;
            int y = 0;

            for(int i = 0; i < Math.min(drops.size() - this.currentPage * 9, 9); ++i) {
                gfx.renderItem(drops.get(i + this.currentPage * 9), left + x * 19, top + 23 + y * 19);
                gfx.renderItemDecorations(this.font, drops.get(i + this.currentPage * 9), left + x * 19 - 1, top + 23 + y * 19 - 1);
                ++x;
                if (x == 3) {
                    ++y;
                    x = 0;
                }
            }
        }

    }

    public void drawOnLeft(GuiGraphics gfx, List<Component> list, int y) {
        if (!list.isEmpty()) {
            int var = this.getGuiLeft() - 16;
            Stream<Component> stream = list.stream();
            Font font1 = this.font;
            Objects.requireNonNull(font1);
            int xPos = var - stream.map(font1::width).max(Integer::compare).get();
            int maxWidth;
            if (xPos < 0) {
                maxWidth = this.getGuiLeft() - 6;
                xPos = -8;
            } else {
                maxWidth = 9999;
            }

            List<FormattedText> split = new ArrayList<>();
            list.forEach((comp) -> {
                split.addAll(this.font.getSplitter().splitLines(comp, maxWidth, comp.getStyle()));
            });
            gfx.renderComponentTooltip(this.font, split, xPos, y, ItemStack.EMPTY);
        }
    }
}
