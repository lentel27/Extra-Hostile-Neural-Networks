package net.lmor.extrahnn.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import net.lmor.extrahnn.ExtraHostileConfig;
import net.lmor.extrahnn.ExtraHostileNetworks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import shadows.hostilenetworks.data.DataModel;
import shadows.hostilenetworks.gui.LootFabContainer;
import shadows.hostilenetworks.item.MobPredictionItem;
import shadows.placebo.screen.PlaceboContainerScreen;

public class UltimateLootFabScreen extends PlaceboContainerScreen<UltimateLootFabContainer> {
    private static final int WIDTH = 176;
    private static final int HEIGHT = 178;
    private static final ResourceLocation BASE = ExtraHostileNetworks.local("textures/gui/ultimate_loot_fabricator.png");
    private static final ResourceLocation PLAYER = ExtraHostileNetworks.local("textures/gui/inventory.png");
    private DataModel model = null;
    private int currentPage = 0;
    private ImageButton btnLeft;
    private ImageButton btnRight;

    public UltimateLootFabScreen(UltimateLootFabContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;
    }

    public void render(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTicks) {
        this.model = MobPredictionItem.getStoredModel(this.menu.getSlot(0).getItem());
        if (this.model != null) {
            this.btnLeft.visible = this.currentPage > 0;
            this.btnRight.visible = this.currentPage < this.model.getFabDrops().size() / 9;
        } else {
            this.btnLeft.visible = false;
            this.btnRight.visible = false;
        }

        super.render(poseStack, pMouseX, pMouseY, pPartialTicks);
    }

    public void init() {
        super.init();
        this.btnLeft = this.addRenderableWidget(new ImageButton(this.getGuiLeft() + 13, this.getGuiTop() + 68, 29, 12, 49, 83, 12, BASE, (btn) -> {
            if (this.model != null && this.currentPage > 0) {
                --this.currentPage;
            }

        }));
        this.btnRight = this.addRenderableWidget(new ImageButton(this.getGuiLeft() + 46, this.getGuiTop() + 68, 29, 12, 78, 83, 12, BASE, (btn) -> {
            if (this.model != null && this.currentPage < this.model.getFabDrops().size() / 9) {
                ++this.currentPage;
            }
        }));
    }

    protected void renderLabels(PoseStack poseStack, int pX, int pY) {
    }

    protected void renderTooltip(PoseStack poseStack, int pX, int pY) {
        if (this.isHovering(6, 10, 7, 53, pX, pY)) {
            List<Component> txt = new ArrayList<>(2);
            txt.add(Component.translatable("hostilenetworks.gui.energy", this.menu.getEnergyStored(), ExtraHostileConfig.ultimateFabPowerCap));
            txt.add(Component.translatable("hostilenetworks.gui.fab_cost", ExtraHostileConfig.ultimateFabPowerCost));
            this.renderComponentTooltip(poseStack, txt, pX, pY, this.font);
        }

        if (this.model != null) {
            int selection = this.menu.getSelectedDrop(this.model);
            if (selection != -1 && this.isHovering(79, 5, 16, 16, pX, pY)) {
                this.renderComponentTooltip(poseStack, Arrays.asList(Component.translatable("hostilenetworks.gui.clear")), pX, pY, this.font);
            }

            List<ItemStack> drops = this.model.getFabDrops();

            for(int y = 0; y < 3; ++y) {
                for(int x = 0; x < 3; ++x) {
                    if (y * 3 + x < Math.min(drops.size() - this.currentPage * 9, 9) && this.isHovering(18 + 18 * x, 10 + 18 * y, 16, 16, (double)pX, (double)pY)) {
                        this.drawOnLeft(poseStack, this.getTooltipFromItem(drops.get(this.currentPage * 9 + y * 3 + x)), this.getGuiTop() + 15);
                    }
                }
            }
        }

        super.renderTooltip(poseStack, pX, pY);
    }

    public boolean mouseClicked(double pX, double pY, int pButton) {
        if (this.model != null) {
            List<ItemStack> drops = this.model.getFabDrops();
            int selection = this.menu.getSelectedDrop(this.model);

            for(int y = 0; y < 3; ++y) {
                for(int x = 0; x < 3; ++x) {
                    if (y * 3 + x < drops.size() && this.isHovering(18 + 18 * x, 10 + 18 * y, 16, 16, pX, pY) && selection != y * 3 + x) {
                        assert Minecraft.getInstance().gameMode != null;
                        Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, this.currentPage * 9 + y * 3 + x);
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    }
                }
            }

            if (selection != -1 && this.isHovering(79, 5, 16, 16, pX, pY)) {
                assert Minecraft.getInstance().gameMode != null;
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, -1);
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        }

        return super.mouseClicked(pX, pY, pButton);
    }

    protected void renderBg(PoseStack poseStack, float pPartialTicks, int pX, int pY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BASE);
        int left = this.getGuiLeft();
        int top = this.getGuiTop();

        GuiComponent.blit(poseStack, left, top, 0.0F, 0.0F, 176, 83, 256, 256);

        int energyHeight = Mth.floor(53.0F * ((float)this.menu.getEnergyStored() / (float)ExtraHostileConfig.ultimateFabPowerCap));
        GuiComponent.blit(poseStack, left + 6, top + 10 + 53 - energyHeight, 0.0F, 83.0F, 7, energyHeight, 256, 256);

        int progHeight = Mth.floor(35.0F * (float)this.menu.getRuntime() / ExtraHostileConfig.ultimateFabPowerDuration);
        GuiComponent.blit(poseStack, left + 84, top + 23 + 35 - progHeight, 7.0F, 83.0F, 6, progHeight, 256, 256);

        RenderSystem.setShaderTexture(0, PLAYER);
        GuiComponent.blit(poseStack, left, top + 84, 0.0F, 0.0F, 176, 90, 256, 256);

        RenderSystem.setShaderTexture(0, BASE);
        if (this.model != null) {
            List<ItemStack> drops = this.model.getFabDrops();

            int selection;
            int x;
            for(selection = 0; selection < 3; ++selection) {
                for(x = 0; x < 3; ++x) {
                    if (selection * 3 + x < Math.min(drops.size() - this.currentPage * 9, 9) && this.isHovering(18 + 18 * x, 10 + 18 * selection, 16, 16, pX, pY)) {
                        GuiComponent.blit(poseStack, left + 16 + 19 * x, top + 8 + 19 * selection, 13.0F, 83.0F, 18, 18, 256, 256);
                    }
                }
            }

            selection = this.menu.getSelectedDrop(this.model);
            if (selection != -1 && selection / 9 == this.currentPage) {
                x = selection - this.currentPage * 9;
                GuiComponent.blit(poseStack, left + 16 + 19 * (x % 3), top + 8 + 19 * (x / 3), 31.0F, 83.0F, 18, 18, 256, 256);
            }

            Minecraft mc = Minecraft.getInstance();
            if (selection != -1) {
                mc.getItemRenderer().renderAndDecorateItem(drops.get(selection), left + 79, top + 5);
                mc.getItemRenderer().renderGuiItemDecorations(this.font, drops.get(selection), left + 78, top + 4);
            }

            left += 17;
            top += 9;
            x = 0;
            int y = 0;

            for(int i = 0; i < Math.min(drops.size() - this.currentPage * 9, 9); ++i) {
                mc.getItemRenderer().renderAndDecorateItem(drops.get(i + this.currentPage * 9), left + x * 19, top + y * 19);
                mc.getItemRenderer().renderGuiItemDecorations(this.font, drops.get(i + this.currentPage * 9), left + x * 19 - 1, top + y * 19 - 1);
                ++x;
                if (x == 3) {
                    ++y;
                    x = 0;
                }
            }
        }
    }

    public void drawOnLeft(PoseStack poseStack, List<Component> list, int y) {
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
            this.renderComponentTooltip(poseStack, split, xPos, y, this.font);
        }
    }
}
