package net.lmor.extrahnn.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import shadows.hostilenetworks.data.CachedModel;
import shadows.hostilenetworks.data.ModelTier;
import shadows.hostilenetworks.item.DataModelItem;
import shadows.placebo.screen.PlaceboContainerScreen;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.lmor.extrahnn.ExtraHostileNetworks;
import net.lmor.extrahnn.data.ExtraCachedModel;
import net.lmor.extrahnn.data.ExtraModelTier;
import net.lmor.extrahnn.item.ExtraDataModelItem;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class SimulatorModelingScreen extends PlaceboContainerScreen<SimulatorModelingContainer> {
    public static final int WIDTH = 216;
    public static final int HEIGHT = 188;
    private static final ResourceLocation BASE = ExtraHostileNetworks.local("textures/gui/simulation_modeling.png");
    private static final ResourceLocation PLAYER = ExtraHostileNetworks.local("textures/gui/inventory.png");
    private boolean runtimeTextLoaded;

    public SimulatorModelingScreen(SimulatorModelingContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.runtimeTextLoaded = false;
        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;
    }

    protected void renderTooltip(PoseStack poseStack, int pX, int pY) {
        if (this.isHovering(210, 4, 9, 89, pX, pY)) {
            List<Component> txt = new ArrayList<>(2);
            txt.add(Component.translatable("hostilenetworks.gui.energy", this.menu.getEnergyStored(), ExtraHostileConfig.simulationModelingPowerCap));
            txt.add(Component.translatable("extrahnn.info.cost_sim", this.menu.getEnergyCost()));

            this.renderComponentTooltip(poseStack, txt, pX, pY);
        } else {
            super.renderTooltip(poseStack, pX, pY);
        }

    }

    protected void renderLabels(PoseStack poseStack, int pX, int pY) {
        boolean isModel = true;
        String name = "";
        String data = "";
        String dataPerSim = "";
        String killIter = "";

        ModelTier md = null;
        ExtraModelTier emd = null;

        if (this.menu.getSlot(0).getItem().getItem() instanceof DataModelItem){
            CachedModel cModel = new CachedModel(this.menu.getSlot(0).getItem(), 0);
            int dt= cModel.getData();
            int progress = dt - cModel.getTierData();
            int maxProgress = cModel.getNextTierData() - cModel.getTierData();

            md = cModel.getTier();

            name = Component.translatable("extrahnn.info.data_model.name").getString();
            data = Component.translatable("hostilenetworks.info.data", Component.translatable("hostilenetworks.info.dprog", progress, maxProgress)).getString();
            dataPerSim = Component.translatable("extrahnn.info.dpk", cModel.getDataPerKill()  * (this.menu.getUpgradeDataKill() ? ExtraHostileConfig.upgradeDataKill: 1)).getString();
            killIter = Component.translatable("extrahnn.info.iter_count", DataModelItem.getIters(this.menu.getSlot(0).getItem())).getString();

        } else if (this.menu.getSlot(0).getItem().getItem() instanceof ExtraDataModelItem){
            ExtraCachedModel cModel = new ExtraCachedModel(this.menu.getSlot(0).getItem(), 0);
            int dt= cModel.getData();
            int progress = dt - cModel.getTierData();
            int maxProgress = cModel.getNextTierData() - cModel.getTierData();

            emd = cModel.getTier();

            name = Component.translatable("extrahnn.info.extra_data_model.name").getString();
            data = Component.translatable("hostilenetworks.info.data", Component.translatable("hostilenetworks.info.dprog", progress, maxProgress)).getString();
            dataPerSim = Component.translatable("extrahnn.info.dpk", cModel.getDataPerKill() * (this.menu.getUpgradeDataKill() ? ExtraHostileConfig.upgradeDataKill: 1)).getString();
            killIter = Component.translatable("extrahnn.info.iter_count", ExtraDataModelItem.getIters(this.menu.getSlot(0).getItem())).getString();
        } else {
            isModel = false;
        }

        int left;
        int top;
        int spacing;
        poseStack.pushPose();
        if (isModel) {
            left = 20;
            top = 9;
            spacing = 4;

            this.font.draw(poseStack, name, left, top, 16777215);
            top = top + 9 + spacing;

            // Rang Model
            String msg = I18n.get("hostilenetworks.gui.tier");
            this.font.draw(poseStack, msg, left, top, 16777215);
            if (md == null && emd != null) this.font.draw(poseStack, I18n.get("extrahnn.tier." + emd.name), left + this.font.width(msg), top, emd.color());
            else if (md != null) this.font.draw(poseStack, I18n.get("hostilenetworks.tier." + md.name), left + this.font.width(msg), top, md.getComponent().getStyle().getColor().getValue());
            top = top + 9 + spacing;

            if ((md != null && md != ModelTier.SELF_AWARE) || (emd != null && emd != ExtraModelTier.OMNIPOTENT)){
                // Accuracy Model
                this.font.draw(poseStack, data, left , top, 16777215);
                top = top + 9 + spacing;

                this.font.draw(poseStack, dataPerSim, left , top, 16777215);
                top = top + 9 + spacing;

                this.font.draw(poseStack, killIter, left, top, 16777215);
                top = top + 9 + spacing;

                int rTime = Math.min(99, Mth.ceil(100.0F * (float)(this.menu.getRuntimeUpgrade() - this.menu.getRuntime()) / this.menu.getRuntimeUpgrade()));
                msg = I18n.get("extrahnn.info.progress_sim", rTime + "%");
                this.font.draw(poseStack, msg, left , top, 16777215);
            }
        }

        poseStack.popPose();
    }

    protected void renderBg(PoseStack poseStack, float pPartialTicks, int pX, int pY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BASE);
        int left = this.getGuiLeft();
        int top = this.getGuiTop();
        GuiComponent.blit(poseStack, left + 8, top, 0.0F, 0.0F, 216, 97, 256, 256);
        GuiComponent.blit(poseStack, left - 14, top, 223, 0, 18, 18, 256, 256);
        GuiComponent.blit(poseStack, left - 14, top + 20, 223, 18, 18, 18, 256, 256);
        GuiComponent.blit(poseStack, left - 14, top + 38, 223, 18, 18, 18, 256, 256);
        int energyHeight = 87 - Mth.ceil(87.0F * (float)this.menu.getEnergyStored() / (float)ExtraHostileConfig.simulationModelingPowerCap);
        GuiComponent.blit(poseStack, left + 211, top + 5, 216, 0, 7, energyHeight, 256, 256);


        RenderSystem.setShaderTexture(0, PLAYER);
        GuiComponent.blit(poseStack, left + 28, top + 98, 0.0F, 0.0F, 176, 90, 256, 256);
    }

    public void containerTick() {

    }


}
