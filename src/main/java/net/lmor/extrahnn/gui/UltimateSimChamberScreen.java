package net.lmor.extrahnn.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import shadows.hostilenetworks.data.CachedModel;
import shadows.hostilenetworks.item.DataModelItem;
import shadows.placebo.screen.PlaceboContainerScreen;
import shadows.placebo.screen.TickableText;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.lmor.extrahnn.ExtraHostileNetworks;
import net.lmor.extrahnn.data.ExtraCachedModel;
import net.lmor.extrahnn.data.ExtraModelTier;
import net.lmor.extrahnn.tile.UltimateSimChamberTileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UltimateSimChamberScreen extends PlaceboContainerScreen<UltimateSimChamberContainer> {
    public static final int WIDTH = 232;
    public static final int HEIGHT = 256;
    private static final ResourceLocation BASE = ExtraHostileNetworks.local("textures/gui/ultimate_sim_chamber.png");
    private static final ResourceLocation PLAYER = ExtraHostileNetworks.local("textures/gui/inventory.png");
    private final List<TickableText> body = new ArrayList<>(7);
    private UltimateSimChamberTileEntity.FailureState lastFailState;
    private boolean runtimeTextLoaded;
    private static final Component ERROR;

    public UltimateSimChamberScreen(UltimateSimChamberContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.lastFailState = UltimateSimChamberTileEntity.FailureState.NONE;
        this.runtimeTextLoaded = false;
        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;
    }

    protected void renderTooltip(PoseStack poseStack, int pX, int pY) {
        if (this.isHovering(211, 73, 7, 87, pX, pY)) {
            List<Component> txt = new ArrayList<>(2);
            txt.add(Component.translatable("hostilenetworks.gui.energy", this.menu.getEnergyStored(), ExtraHostileConfig.ultimateSimPowerCap));
            ExtraCachedModel cModel = new ExtraCachedModel(this.menu.getSlot(0).getItem(), 0);
            if (cModel.isValid()) {
                txt.add(Component.translatable("hostilenetworks.gui.cost", cModel.simCost()));
            }

            this.renderComponentTooltip(poseStack, txt, pX, pY, this.font);
        } else if (this.isHovering(14, 73, 7, 87, pX, pY)) {
            ExtraCachedModel cModel = new ExtraCachedModel(this.menu.getSlot(0).getItem(), 0);
            if (cModel.isValid()) {
                List<Component> txt = new ArrayList<>(1);
                if (cModel.getTier() != cModel.getTier().next()) {
                    txt.add(Component.translatable("hostilenetworks.gui.data", cModel.getData() - cModel.getTierData(), cModel.getNextTierData() - cModel.getTierData()));
                } else {
                    txt.add(Component.translatable("hostilenetworks.gui.max_data").withStyle(ChatFormatting.RED));
                }

                this.renderComponentTooltip(poseStack, txt, pX, pY, this.font);
            }
        } else if (this.menu.getSlot(0).getItem().isEmpty() && this.isHovering(-13, 1, 16,16, pX, pY)){
            List<Component> txt = new ArrayList<>(1);
            txt.add(Component.translatable("extrahnn.info.data_model_slot"));
            this.renderComponentTooltip(poseStack, txt, pX, pY, this.font);
        }
        else {
            super.renderTooltip(poseStack, pX, pY);
        }

    }

    protected void renderLabels(PoseStack poseStack, int pX, int pY) {
        int runtime = this.menu.getRuntime();
        if (runtime > 0) {
            int rTime = Math.min(99, Mth.ceil(100.0F * (float)(ExtraHostileConfig.ultimateSimPowerDuration - runtime) / ExtraHostileConfig.ultimateSimPowerDuration));
            this.font.drawShadow(poseStack, rTime + "%", 186.0F, 150.0F, 6478079);
        }

        ExtraCachedModel cModel = new ExtraCachedModel(this.menu.getSlot(0).getItem(), 0);
        int left;
        int top;
        int spacing;
        poseStack.pushPose();
        poseStack.scale(0.7f, 0.7f, 0.7f);
        if (!this.menu.getSlot(0).getItem().isEmpty() && cModel.isValid()) {
            left = 25;
            top = 9;
            spacing = 4;

            String msg = I18n.get("extrahnn.gui.targets");
            this.font.draw(poseStack, msg, left, top, 16777215);
            top = top + 9 + spacing;

            float scale;
            for (int i = 0; i < cModel.getModels().size(); i++){
                if (font.width(cModel.getModels().get(i).getName()) <= 139) {
                    scale =  1f;
                } else {
                    scale = (float) 139 / font.width(cModel.getModels().get(i).getName());
                }
                poseStack.pushPose();
                poseStack.scale(scale, scale, scale);
                this.font.draw(poseStack, " - ", left - 2, top + (9 + spacing) * i, 65472);
                this.font.draw(poseStack, cModel.getModels().get(i).getName(), left + 13, top + (9 + spacing) * i, 65472);

                poseStack.popPose();
            }
            top = top + (9 + spacing) * 4;

            // Rang Model
            msg = I18n.get("hostilenetworks.gui.tier");

            this.font.draw(poseStack, msg, left, top, 16777215);
            this.font.draw(poseStack, I18n.get("extrahnn.tier." + cModel.getTier().name),
                    left + this.font.width(msg), top, cModel.getTier().color());

            top = top + 9 + spacing;

            // Accuracy Model
            msg = I18n.get("hostilenetworks.gui.accuracy");
            this.font.draw(poseStack, msg, left, top, 16777215);
            DecimalFormat fmt = new DecimalFormat("##.##%");
            this.font.draw(poseStack, fmt.format(cModel.getAccuracy()), left + this.font.width(msg), top, cModel.getTier().color());

        }

        poseStack.popPose();

        left = 29;
        top = 77;
        Objects.requireNonNull(this.font);
        spacing = 9 + 3;
        int idx = 0;

        for (TickableText t : this.body) {
            t.render(this.font, poseStack, left, top + spacing * idx);
            if (t.causesNewLine()) {
                ++idx;
                left = 29;
            } else {
                left += t.getWidth(this.font);
            }
        }

    }

    protected void renderBg(PoseStack poseStack, float pPartialTicks, int pX, int pY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BASE);
        int left = this.getGuiLeft();
        int top = this.getGuiTop();

        GuiComponent.blit(poseStack, left + 8, top, 0.0F, 0.0F, 216, 166, 256, 256);
        GuiComponent.blit(poseStack, left - 14, top, 216, 0, 18, 18, 256, 256);

        int energyHeight = 87 - Mth.ceil(87.0F * (float)this.menu.getEnergyStored() / (float)ExtraHostileConfig.ultimateSimPowerCap);
        GuiComponent.blit(poseStack, left + 211, top + 73, 234, 0.0F, 7, energyHeight, 256, 256);

        int dataHeight = 87;
        ExtraCachedModel cModel = new ExtraCachedModel(this.menu.getSlot(0).getItem(), 0);
        if (cModel.isValid()) {
            int data = cModel.getData();
            ExtraModelTier tier = cModel.getTier();
            ExtraModelTier next = tier.next();
            if (tier == next) {
                dataHeight = 0;
            } else {
                dataHeight = 87 - Mth.ceil(87.0F * (float)(data - cModel.getTierData()) / (float)(cModel.getNextTierData() - cModel.getTierData()));
            }
        }
        GuiComponent.blit(poseStack, left + 14, top + 73, 234, 0, 7, dataHeight, 256, 256);

        RenderSystem.setShaderTexture(0, PLAYER);
        GuiComponent.blit(poseStack, left + 28, top + 167, 0.0F, 0.0F, 176, 90, 256, 256);
    }

    public void containerTick() {
        String key;
        int i;
        if (this.menu.getFailState() != UltimateSimChamberTileEntity.FailureState.NONE) {
            UltimateSimChamberTileEntity.FailureState oState = this.lastFailState;
            this.lastFailState = this.menu.getFailState();
            if (oState != this.lastFailState) {
                this.body.clear();
                String[] msg = I18n.get(this.lastFailState.getKey()).split("\\n");
                if (this.lastFailState == UltimateSimChamberTileEntity.FailureState.INPUT) {
                    CachedModel cModel = new CachedModel(this.menu.getSlot(0).getItem(), 0);
                    Component name = Component.translatable("item.hostilenetworks.empty_prediction");
                    if (cModel.isValid()) {
                        name = cModel.getModel().getInput().getHoverName();
                    }

                    msg = I18n.get(this.lastFailState.getKey(), name.getString()).split("\\n");
                }

                String[] msg1 = msg;
                i = msg.length;

                for(int j = 0; j < i; ++j) {
                    key = msg1[j];
                    this.body.add(new TickableText(key, 16777215));
                }
            }

            this.runtimeTextLoaded = false;
        } else if (!this.runtimeTextLoaded) {
            int ticks = ExtraHostileConfig.ultimateSimPowerDuration - this.menu.getRuntime();
            float speed = 1.3f;
            this.body.clear();
            int iters = DataModelItem.getIters(this.menu.getSlot(0).getItem());

            for(i = 0; i < 7; ++i) {
                TickableText txt = new TickableText(I18n.get("hostilenetworks.run." + i, iters), 16777215, i != 0 && i != 5, speed);
                this.body.add(txt.setTicks(ticks));
                ticks = Math.max(0, ticks - txt.getMaxUsefulTicks());
                if (i == 0) {
                    txt = new TickableText("v" + ExtraHostileNetworks.VERSION, ChatFormatting.GOLD.getColor(), true, speed);
                    this.body.add(txt.setTicks(ticks));
                    ticks = Math.max(0, ticks - txt.getMaxUsefulTicks());
                } else if (i == 5) {
                    String s = this.menu.didPredictionSucceed() ? "success" : "failed";
                    key = "hostilenetworks.color_text." + s;
                    txt = new TickableText(I18n.get(key), (this.menu.didPredictionSucceed() ? ChatFormatting.GOLD : ChatFormatting.RED).getColor(), true, speed);
                    this.body.add(txt.setTicks(ticks));
                    ticks = Math.max(0, ticks - txt.getMaxUsefulTicks());
                }
            }

            this.runtimeTextLoaded = true;
            this.lastFailState = UltimateSimChamberTileEntity.FailureState.NONE;
        }

        TickableText.tickList(this.body);
        if (this.menu.getRuntime() == 0) {
            this.runtimeTextLoaded = false;
        }

    }

    static {
        ERROR = Component.literal("ERROR").withStyle(ChatFormatting.OBFUSCATED);
    }
}
