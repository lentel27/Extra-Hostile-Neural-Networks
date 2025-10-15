package net.lmor.extrahnn.gui;

import dev.shadowsoffire.hostilenetworks.data.CachedModel;
import dev.shadowsoffire.hostilenetworks.data.ModelTier;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.placebo.screen.PlaceboContainerScreen;
import net.lmor.extrahnn.EHNNUtils;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.lmor.extrahnn.ExtraHostileNetworks;
import net.lmor.extrahnn.data.ExtraCachedModel;
import net.lmor.extrahnn.data.ExtraModelTier;
import net.lmor.extrahnn.item.ExtraDataModelItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class SimulationModelingScreen extends PlaceboContainerScreen<SimulationModelingContainer> {
    public static final int WIDTH = 216;
    public static final int HEIGHT = 188;
    private static final ResourceLocation BASE = ExtraHostileNetworks.local("textures/gui/simulation_modeling.png");
    private static final ResourceLocation PLAYER = ExtraHostileNetworks.local("textures/gui/inventory.png");

    public SimulationModelingScreen(SimulationModelingContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;
    }

    protected void renderTooltip(GuiGraphics gfx, int pX, int pY) {
        if (this.isHovering(210, 4, 9, 89, pX, pY)) {
            List<Component> txt = new ArrayList<>(2);
            txt.add(Component.translatable("hostilenetworks.gui.energy", this.menu.getEnergyStored(), ExtraHostileConfig.simulationModelingPowerCap));
            txt.add(Component.translatable("extrahnn.info.cost_sim", this.menu.getEnergyCost()));

            gfx.renderComponentTooltip(this.font, txt, pX, pY);
        }
        else if (this.isHovering(-13, 1, 18, 18, pX, pY)){
            gfx.renderComponentTooltip(this.font, EHNNUtils.translateTier(), pX, pY);
        } else {
            super.renderTooltip(gfx, pX, pY);
        }

    }

    protected void renderLabels(GuiGraphics gfx, int pX, int pY) {
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
        gfx.pose().pushPose();
        if (isModel) {
            left = 20;
            top = 9;
            spacing = 4;

            gfx.drawString(this.font, name, left, top, 16777215);
            top = top + 9 + spacing;

            // Rang Model
            String msg = I18n.get("hostilenetworks.gui.tier");
            gfx.drawString(font, msg, left, top, 16777215);
            if (md == null && emd != null) gfx.drawString(this.font, I18n.get("extrahnn.tier." + emd.name), left + this.font.width(msg), top, emd.color());
            else if (md != null) gfx.drawString(this.font, I18n.get("hostilenetworks.tier." + md.name), left + this.font.width(msg), top, md.color());
            top = top + 9 + spacing;

            if ((md != null && md != ModelTier.SELF_AWARE) || (emd != null && emd != ExtraModelTier.OMNIPOTENT)){
                // Accuracy Model
                gfx.drawString(this.font, data, left , top, 16777215);
                top = top + 9 + spacing;

                gfx.drawString(this.font, dataPerSim, left , top, 16777215);
                top = top + 9 + spacing;

                gfx.drawString(this.font, killIter, left, top, 16777215);
                top = top + 9 + spacing;

                int rTime = Math.min(99, Mth.ceil(100.0F * (float)(this.menu.getRuntimeUpgrade() - this.menu.getRuntime()) / this.menu.getRuntimeUpgrade()));
                msg = I18n.get("extrahnn.info.progress_sim", rTime + "%");
                gfx.drawString(this.font, msg, left , top, 16777215);
            }
        }

        gfx.pose().popPose();
    }

    protected void renderBg(GuiGraphics gfx, float pPartialTicks, int pX, int pY) {
        int left = this.getGuiLeft();
        int top = this.getGuiTop();
        gfx.blit(BASE, left + 8, top, 0.0F, 0.0F, 216, 97, 256, 256);
        gfx.blit(BASE, left - 14, top, 223, 0, 18, 18, 256, 256);
        gfx.blit(BASE, left - 14, top + 20, 223, 18, 18, 18, 256, 256);
        gfx.blit(BASE, left - 14, top + 38, 223, 18, 18, 18, 256, 256);
        int energyHeight = 87 - Mth.ceil(87.0F * (float)this.menu.getEnergyStored() / (float)ExtraHostileConfig.simulationModelingPowerCap);
        gfx.blit(BASE, left + 211, top + 5, 216, 0, 7, energyHeight, 256, 256);

        gfx.blit(PLAYER, left + 28, top + 98, 0.0F, 0.0F, 176, 90, 256, 256);
    }

    public void containerTick() {
    }
}