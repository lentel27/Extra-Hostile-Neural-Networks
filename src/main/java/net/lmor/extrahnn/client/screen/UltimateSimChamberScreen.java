package net.lmor.extrahnn.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.shadowsoffire.hostilenetworks.data.DataModelInstance;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.hostilenetworks.tile.SimChamberTileEntity;
import dev.shadowsoffire.hostilenetworks.tile.SimChamberTileEntity.RedstoneState;
import dev.shadowsoffire.placebo.screen.PlaceboContainerScreen;
import dev.shadowsoffire.placebo.screen.TickableTextList;
import net.lmor.extrahnn.EHNNUtils;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.lmor.extrahnn.ExtraHostileNetworks;
import net.lmor.extrahnn.common.container.UltimateSimChamberContainer;
import net.lmor.extrahnn.common.tile.UltimateSimChamberTileEntity.FailureState;
import net.lmor.extrahnn.data.ExtraDataModelInstance;
import net.lmor.extrahnn.data.ExtraModelTier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UltimateSimChamberScreen extends PlaceboContainerScreen<UltimateSimChamberContainer> {

    public static final int WIDTH = 232;
    public static final int HEIGHT = 256;
    public static final int MAX_TEXT_WIDTH = 178;

    private static final Component ERROR = Component.literal("ERROR").withStyle(ChatFormatting.OBFUSCATED);

    DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.##%");

    private static final ResourceLocation BASE = ExtraHostileNetworks.local("textures/gui/ultimate_sim_chamber.png");
    private static final ResourceLocation PLAYER = ExtraHostileNetworks.local("textures/gui/inventory.png");

    private TickableTextList body;
    private FailureState lastFailState = FailureState.NONE;
    private boolean runtimeTextLoaded = false;

    public UltimateSimChamberScreen(UltimateSimChamberContainer menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new RedstoneButton(this.getGuiLeft() + 228, this.getGuiTop()));
        this.body = new TickableTextList(Objects.requireNonNull(this.minecraft).font, MAX_TEXT_WIDTH);
        this.lastFailState = FailureState.NONE;
        this.runtimeTextLoaded = false;
        this.containerTick();
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics graphics, int x, int y) {
        List<Component> txt = new ArrayList<>();
        if (this.isHovering(211, 73, 7, 87, x, y)) {
            txt.add(Component.translatable("hostilenetworks.gui.energy", this.menu.getEnergyStored(), ExtraHostileConfig.ultimateSimPowerCap));
            ExtraDataModelInstance cModel = new ExtraDataModelInstance(this.menu.getSlot(0).getItem(), 0);
            if (cModel.isValid()) {
                txt.add(Component.translatable("hostilenetworks.gui.cost", cModel.simCost()));
            }
        } else if (this.isHovering(14, 73, 7, 87, x, y)) {
            ExtraDataModelInstance cModel = new ExtraDataModelInstance(this.menu.getSlot(0).getItem(), 0);
            if (cModel.isValid()) {
                if (cModel.getTier() != cModel.getTier().next()) {
                    txt.add(Component.translatable("hostilenetworks.gui.data", cModel.getData() - cModel.getTierData(), cModel.getNextTierData() - cModel.getTierData()));
                } else {
                    txt.add(Component.translatable("hostilenetworks.gui.max_data").withStyle(ChatFormatting.RED));
                }
            }
        } else if (this.menu.getSlot(0).getItem().isEmpty() && this.isHovering(-13, 1, 16,16, x, y)){
            txt.add(Component.translatable("extrahnn.info.data_model_slot"));
        } else if (this.isHovering(228, 1, 16, 16, x, y)){
            txt.add(Component.translatable(this.menu.getRedstoneState().getKey()));
        }

        graphics.renderComponentTooltip(this.font, txt, x, y);
        super.renderTooltip(graphics, x, y);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int x, int y) {
        int runtime = this.menu.getRuntime();
        if (runtime > 0) {
            int rTime = Math.min(99, Mth.ceil(100.0F * (float)(ExtraHostileConfig.ultimateSimPowerDuration - runtime) / ExtraHostileConfig.ultimateSimPowerDuration));
            graphics.drawString(this.font, rTime + "%", 186, 150, 6478079, true);
        }

        ExtraDataModelInstance cModel = new ExtraDataModelInstance(this.menu.getSlot(0).getItem(), 0);
        renderModelNames(cModel, graphics, x, y);

        this.body.render(graphics, 28, 75);
    }

    protected void renderModelNames(@NotNull ExtraDataModelInstance cModel, @NotNull GuiGraphics graphics, int x, int y){
        if (!cModel.isValid()) return;

        graphics.pose().pushPose();
        graphics.pose().scale(0.7f, 0.7f, 0.7f);

        int left = 25;
        int top = 9;
        int spacing = 4;

        graphics.drawString(font, I18n.get("extrahnn.gui.targets"), left, top, 0xFFFFFF);
        top += 9 + spacing;

        for (int i = 0; i < cModel.getModels().size(); i++) {
            var modelHolder = cModel.getModels().get(i);
            var modelName = modelHolder.get().name();

            float width = font.width(modelName);
            float scale = width <= 139 ? 1f : 139f / width;
            int yOffset = top + (9 + spacing) * i;

            graphics.pose().pushPose();
            graphics.pose().scale(scale, scale, scale);

            graphics.drawString(font, " - ", left - 2, yOffset, 0x00FF60);
            graphics.drawString(font, modelName, left + 13, yOffset, 0x00FF60);

            graphics.pose().popPose();
        }

        top += (9 + spacing) * 4;

        // Tier
        int tierColor = cModel.getTier().color();
        String tierName = Component.translatable("hostilenetworks.gui.tier",
                Component.translatable("extrahnn.tier." + cModel.getTier().name).withColor(tierColor)
        ).getString();

        graphics.drawString(font, tierName, left, top, 0xFFFFFF);
        top += 9 + spacing;

        // Accuracy
        String accLabel = Component.translatable("hostilenetworks.gui.accuracy",
                Component.literal(DECIMAL_FORMAT.format(cModel.getAccuracy())).withColor(tierColor)
        ).getString();
        graphics.drawString(font, accLabel, left, top, 0xFFFFFF);

        graphics.pose().popPose();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTicks, int pX, int pY) {
        int left = this.getGuiLeft();
        int top = this.getGuiTop();

        graphics.blit(BASE, left + 8, top, 0.0F, 0.0F, 216, 166, 256, 256);
        graphics.blit(BASE, left - 14, top, 216, 0, 18, 18, 256, 256);

        // Redstone
        graphics.blit(BASE, left + 228, top, 216, 18, 18, 18, 256, 256);

        int energyHeight = 87 - Mth.ceil(87.0F * (float)this.menu.getEnergyStored() / (float)ExtraHostileConfig.ultimateSimPowerCap);
        graphics.blit(BASE, left + 211, top + 73, 234, 0, 7, energyHeight, 256, 256);

        int dataHeight = 87;
        ExtraDataModelInstance cModel = new ExtraDataModelInstance(this.menu.getSlot(0).getItem(), 0);
        if (cModel.isValid()) {
            int data = cModel.getData();
            ExtraModelTier tier = cModel.getTier();
            dataHeight = tier.isMax() ? 0: 87 - Mth.ceil(87.0F * (float)(data - cModel.getTierData()) / (float)(cModel.getNextTierData() - cModel.getTierData()));
        }

        graphics.blit(BASE, left + 14, top + 73, 234, 0, 7, dataHeight, 256, 256);
        graphics.blit(PLAYER, left + 28, top + 167, 0.0F, 0.0F, 176, 90, 256, 256);
    }

    @Override
    public void containerTick() {
        if (this.menu.getFailState() != FailureState.NONE) {
            FailureState oState = this.lastFailState;
            this.lastFailState = this.menu.getFailState();
            if (oState != this.lastFailState) {
                this.body.clear();
                MutableComponent msg = Component.translatable(this.lastFailState.getKey());
                if (this.lastFailState == FailureState.INPUT) {
                    DataModelInstance cModel = new DataModelInstance(this.menu.getSlot(0).getItem(), 0);
                    Component name = Component.translatable("item.hostilenetworks.prediction_matrix");
                    if (cModel.isValid()) {
                        name = cModel.getModel().input().getItems()[0].getHoverName();
                    }
                    msg = Component.translatable(this.lastFailState.getKey(), name);
                }
                this.body.addLine(msg, 1);
            }
            this.runtimeTextLoaded = false;
        }
        else if (!this.runtimeTextLoaded) {
            int ticks = ExtraHostileConfig.ultimateSimPowerDuration - this.menu.getRuntime();
            this.body.clear();
            int iters = DataModelItem.getIters(this.menu.getSlot(0).getItem());

            List<Component> textAll = new ArrayList<>();
            for(int i = 0; i < 7; ++i) {
                Component txt = Component.translatable("hostilenetworks.run." + i, iters);

                if (i == 0) {
                    txt = Component.empty().append(txt).append(Component.literal("v" + ExtraHostileNetworks.VERSION).withStyle(ChatFormatting.GOLD));
                } else if (i == 5) {
                    String key = "hostilenetworks.color_text." + (this.menu.isPredictionSucceed() ? "success" : "failed");
                    Component status = Component.translatable(key).withStyle(this.menu.isPredictionSucceed() ? ChatFormatting.GOLD : ChatFormatting.RED);
                    textAll.add(status);
                }

                if (i != 6) textAll.add(txt);
            }
            int allChar = textAll.stream().mapToInt(x -> x.getString().length()).sum();

            float speed = EHNNUtils.textTickRate(allChar, ExtraHostileConfig.ultimateSimPowerDuration);
            for (Component text: textAll){
                this.body.continueLine(Component.empty().append(text).append("\n"), speed);
            }

            this.body.setTicks(ticks);
            this.runtimeTextLoaded = true;
            this.lastFailState = FailureState.NONE;
        }

        this.body.tick();
        if (this.menu.getRuntime() == 0) {
            this.runtimeTextLoaded = false;
        }
    }

    private class RedstoneButton extends AbstractWidget {

        public RedstoneButton(int x, int y) {
            super(x, y, 18, 18, Component.empty());
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onClick(double mouseX, double mouseY) {
            UltimateSimChamberScreen scn = UltimateSimChamberScreen.this;
            int idx = scn.menu.getRedstoneState().next().ordinal();
            Objects.requireNonNull(Objects.requireNonNull(scn.minecraft).gameMode).handleInventoryButtonClick(scn.menu.containerId, idx);
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            int y = UltimateSimChamberScreen.this.menu.getRedstoneState() != RedstoneState.IGNORED ? this.getY() : this.getY() + 1;
            guiGraphics.blit(UltimateSimChamberScreen.this.menu.getRedstoneState().getResourceLocation(), this.getX() + 1, y, 0, 0, 16, 16, 16, 16);
        }
    }

}
