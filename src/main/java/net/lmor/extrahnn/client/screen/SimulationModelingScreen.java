package net.lmor.extrahnn.client.screen;

import dev.shadowsoffire.hostilenetworks.data.DataModelInstance;
import dev.shadowsoffire.hostilenetworks.data.ModelTier;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.placebo.screen.PlaceboContainerScreen;
import net.lmor.extrahnn.EHNNUtils;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.lmor.extrahnn.ExtraHostileNetworks;
import net.lmor.extrahnn.common.container.SimulationModelingContainer;
import net.lmor.extrahnn.common.item.ExtraDataModelItem;
import net.lmor.extrahnn.data.ExtraDataModelInstance;
import net.lmor.extrahnn.data.ExtraModelTier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

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

    protected void renderTooltip(@NotNull GuiGraphics graphics, int x, int y) {
        if (this.isHovering(210, 4, 9, 89, x, y)) {
            List<Component> txt = new ArrayList<>(2);
            txt.add(Component.translatable("hostilenetworks.gui.energy", this.menu.getEnergyStored(), ExtraHostileConfig.simulationModelingPowerCap));
            txt.add(Component.translatable("extrahnn.info.cost_sim", this.menu.getEnergyCost()));

            graphics.renderComponentTooltip(this.font, txt, x, y);
        }
        else if (this.isHovering(-13, 1, 18, 18, x, y) && this.menu.getSlot(0).getItem().isEmpty()){
            graphics.renderComponentTooltip(this.font, EHNNUtils.translateTier(), x, y);
        }

        super.renderTooltip(graphics, x, y);
    }

    protected void renderLabels(@NotNull GuiGraphics graphics, int pX, int pY) {
        boolean isModel = true;
        String name = "";
        String data = "";
        String dataPerSim = "";
        String killIter = "";

        ModelTier md = null;
        ExtraModelTier emd = null;

        if (this.menu.getSlot(0).getItem().getItem() instanceof DataModelItem){
            DataModelInstance cModel = new DataModelInstance(this.menu.getSlot(0).getItem(), 0);
            int dt= cModel.getData();
            int progress = dt - cModel.getTierData();
            int maxProgress = cModel.getNextTierData() - cModel.getTierData();

            md = cModel.getTier();

            name = Component.translatable("extrahnn.info.data_model.name").getString();
            data = Component.translatable("hostilenetworks.info.data", Component.translatable("hostilenetworks.info.dprog", progress, maxProgress)).getString();
            dataPerSim = Component.translatable("extrahnn.info.dpk", cModel.getDataPerKill()  * (this.menu.getUpgradeDataKill() ? ExtraHostileConfig.upgradeDataKill: 1)).getString();
            killIter = Component.translatable("extrahnn.info.iter_count", DataModelItem.getIters(this.menu.getSlot(0).getItem())).getString();

        } else if (this.menu.getSlot(0).getItem().getItem() instanceof ExtraDataModelItem){
            ExtraDataModelInstance cModel = new ExtraDataModelInstance(this.menu.getSlot(0).getItem(), 0);
            int dt= cModel.getData();
            int progress = dt - cModel.getTierData();
            int maxProgress = cModel.getNextTierData() - cModel.getTierData();

            emd = cModel.getTier();

            name = Component.translatable("extrahnn.info.extra_data_model.name").getString();
            data = Component.translatable("hostilenetworks.info.data", Component.translatable("hostilenetworks.info.dprog", progress, maxProgress)).getString();
            dataPerSim = Component.translatable("extrahnn.info.dpk", cModel.getDataPerKill() * (this.menu.getUpgradeDataKill() ? ExtraHostileConfig.upgradeDataKill: 1)).getString();
            killIter = Component.translatable("extrahnn.info.iter_count", ExtraDataModelItem.getIters(this.menu.getSlot(0).getItem())).getString();
        } else isModel = false;

        if (!isModel) return;

        int left = 20;
        int top = 9;
        int spacing = 4;
        graphics.pose().pushPose();

        graphics.drawString(this.font, name, left, top, 16777215);
        top = top + 9 + spacing;

        String msg = Component.translatable("hostilenetworks.gui.tier", "").getString();
        graphics.drawString(font, msg, left, top, 16777215);

        if (md == null && emd != null) graphics.drawString(this.font, I18n.get("extrahnn.tier." + emd.name), left + this.font.width(msg), top, emd.color());
        else if (md != null) graphics.drawString(this.font, I18n.get("hostilenetworks.tier." + md.name()), left + this.font.width(msg), top, md.color().getValue());
        top = top + 9 + spacing;

        if ((md != null && !md.isMax()) || (emd != null && !emd.isMax())){
            graphics.drawString(this.font, data, left , top, 16777215);
            top = top + 9 + spacing;

            graphics.drawString(this.font, dataPerSim, left , top, 16777215);
            top = top + 9 + spacing;

            graphics.drawString(this.font, killIter, left, top, 16777215);
            top = top + 9 + spacing;

            int rTime = Math.min(99, Mth.ceil(100.0F * (float)(this.menu.getRuntimeUpgrade() - this.menu.getRuntime()) / this.menu.getRuntimeUpgrade()));
            msg = I18n.get("extrahnn.info.progress_sim", rTime + "%");
            graphics.drawString(this.font, msg, left , top, 16777215);
        }

        graphics.pose().popPose();
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
}