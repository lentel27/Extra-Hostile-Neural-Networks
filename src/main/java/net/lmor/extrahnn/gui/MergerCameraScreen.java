package net.lmor.extrahnn.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import shadows.hostilenetworks.item.DataModelItem;
import shadows.placebo.screen.PlaceboContainerScreen;
import shadows.placebo.screen.TickableText;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.lmor.extrahnn.ExtraHostileNetworks;
import net.lmor.extrahnn.tile.MergerCameraTileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MergerCameraScreen extends PlaceboContainerScreen<MergerCameraContainer> {

    public static final int WIDTH = 216;
    public static final int HEIGHT = 241;
    private static final ResourceLocation BASE = ExtraHostileNetworks.local("textures/gui/merger_camera.png");
    private static final ResourceLocation PLAYER = ExtraHostileNetworks.local("textures/gui/inventory.png");
    private List<TickableText> body = new ArrayList(7);
    private MergerCameraTileEntity.FailureState lastFailState;
    private boolean runtimeTextLoaded;
    private static final Component ERROR;

    public MergerCameraScreen(MergerCameraContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.lastFailState = MergerCameraTileEntity.FailureState.NONE;
        this.runtimeTextLoaded = false;
        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;
    }

    protected void renderTooltip(PoseStack poseStack, int pX, int pY) {
        if (this.isHovering(203, 59, 7, 87, pX, pY)) {
            List<Component> txt = new ArrayList<>(2);
            txt.add(Component.translatable("hostilenetworks.gui.energy", this.menu.getEnergyStored(), ExtraHostileConfig.mergerCameraPowerCap));
            txt.add(Component.translatable("hostilenetworks.gui.cost", ExtraHostileConfig.mergerCameraPowerCost));

            this.renderComponentTooltip(poseStack, txt, pX, pY, this.font);

        } else {
            super.renderTooltip(poseStack, pX, pY);
        }

    }

    protected void renderLabels(PoseStack poseStack, int pX, int pY) {
        int runtime = this.menu.getRuntime();
        if (runtime > 0) {
            int rTime = Math.min(99, Mth.ceil(100.0F * (float)(ExtraHostileConfig.mergerCameraPowerDuration - runtime) / (float) ExtraHostileConfig.mergerCameraPowerDuration));
            this.font.drawShadow(poseStack, rTime + "%", 178, 135, 6478079);
        }

        int left = 10;
        int top = 62;
        Objects.requireNonNull(this.font);
        int spacing = 9 + 3;
        int idx = 0;

        for (TickableText t : this.body) {
            t.render(this.font, poseStack, left, top + spacing * idx);
            if (t.causesNewLine()) {
                ++idx;
                left = 10;
            } else {
                left += t.getWidth(this.font);
            }
        }

    }

    @Override
    protected void renderBg(PoseStack poseStack, float pPartialTicks, int pX, int pY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BASE);
        int left = this.getGuiLeft();
        int top = this.getGuiTop();

        GuiComponent.blit(poseStack, left, top, 0.0F, 0.0F, 216, 152, 256, 256);

        int runtime = this.menu.getRuntime();
        int dur = ExtraHostileConfig.mergerCameraPowerDuration;
        int part = runtime == 0 ? 0 : Mth.ceil(9.0F * Math.min(dur - runtime, (int)(dur * 0.05f)) /  (int)(dur * 0.05f));
        for (int i = 0; i < 2; i++){
            GuiComponent.blit(poseStack, left + 62 + (i == 0 ? 0: 18 - part), top + 27, i == 0 ? 0: 9, 177, part, 1, 256, 256);
            GuiComponent.blit(poseStack, left + 87 + (i == 0 ? 0: 18 - part), top + 25, i == 0 ? 0: 9, 177, part, 1, 256, 256);
            GuiComponent.blit(poseStack, left + 111 + (i == 0 ? 0: 18 - part), top + 25, i == 0 ? 0: 9, 177, part, 1, 256, 256);
            GuiComponent.blit(poseStack, left + 136 + (i == 0 ? 0: 18 - part), top + 27, i == 0 ? 0: 9, 177, part, 1, 256, 256);

        }

        if (runtime != 0 && runtime < dur * 0.95) {
            float min = Math.min(dur - runtime, dur * 0.55f);
            part = Mth.ceil(16 * min / (dur * 0.55f));
            int part_2 = Mth.ceil(5 * min / (dur * 0.55f));

            GuiComponent.blit(poseStack, left + 70, top + 28, 0, 192, 2, part, 256, 256);
            GuiComponent.blit(poseStack, left + 95, top + 26, 0, 182, 2, part_2, 256, 256);
            GuiComponent.blit(poseStack, left + 119, top + 26, 0, 182, 2, part_2, 256, 256);
            GuiComponent.blit(poseStack, left + 144, top + 28, 0, 192, 2, part, 256, 256);

            if (runtime < dur - (int) (dur * 0.45f)){
                part = Mth.ceil(27 * (dur * 0.45f - runtime) / (dur * 0.45f));
                GuiComponent.blit(poseStack, left + 72, top + 42, 0, 209, part, 2, 256, 256);
                GuiComponent.blit(poseStack, left + 117 + 27 - part, top + 42, 0, 209, part, 2, 256, 256);

                part =(int) Math.ceil(8 * (dur * 0.60f - runtime) / (dur * 0.60f));
                GuiComponent.blit(poseStack, left + 97, top + 29, 0, 188, part, 2, 256, 256);
                GuiComponent.blit(poseStack, left + 111 + 8 - part, top + 29, 0, 188, part, 2, 256, 256);

                if (runtime < dur * 0.05) {
                    part = Mth.ceil(3 * (dur * 0.05f - runtime) / (dur * 0.05f));
                    GuiComponent.blit(poseStack, left + 103, top + 31, 3, 182, 2, part, 256, 256);
                    GuiComponent.blit(poseStack, left + 111, top + 31, 3, 182, 2, part, 256, 256);
                }
            }
        }

        int energyHeight = 87 - Mth.ceil(87.0F * (float)this.menu.getEnergyStored() / (float)ExtraHostileConfig.mergerCameraPowerCap);
        GuiComponent.blit(poseStack, left + 203, top + 59, 216, 0, 7, energyHeight, 256, 256);

        RenderSystem.setShaderTexture(0, PLAYER);
        GuiComponent.blit(poseStack, left + 20, top + 153, 0.0F, 0.0F, 176, 90, 256, 256);
    }

    public void containerTick() {
        String key;
        int i;
        if (this.menu.getFailState() != MergerCameraTileEntity.FailureState.NONE) {
            MergerCameraTileEntity.FailureState oState = this.lastFailState;
            this.lastFailState = this.menu.getFailState();
            if (oState != this.lastFailState) {
                this.body.clear();
                String[] msg = I18n.get(this.lastFailState.getKey()).split("\\n");

                String[] msg1 = msg;
                i = msg.length;

                for(int j = 0; j < i; ++j) {
                    key = msg1[j];
                    this.body.add(new TickableText(key, 16777215));
                }
            }

            this.runtimeTextLoaded = false;
        } else if (!this.runtimeTextLoaded) {
            int ticks = ExtraHostileConfig.mergerCameraPowerDuration - this.menu.getRuntime();
            float speed = 0.35F;
            this.body.clear();
            int iters = DataModelItem.getIters(this.menu.getSlot(0).getItem());

            for(i = 0; i < 7; ++i) {
                TickableText txt = new TickableText(I18n.get("extrahnn.merger_camera.run." + i, iters), 16777215, i != 0 && i != 5, speed);
                this.body.add(txt.setTicks(ticks));
                ticks = Math.max(0, ticks - txt.getMaxUsefulTicks());
                if (i == 0) {
                    txt = new TickableText("v" + ExtraHostileNetworks.VERSION, ChatFormatting.GOLD.getColor(), true, speed);
                    this.body.add(txt.setTicks(ticks));
                    ticks = Math.max(0, ticks - txt.getMaxUsefulTicks());
                } else if (i == 5) {
                    key = Component.translatable("extrahnn.fail.overheat." + (this.menu.getOverheat() ? "on" : "off")).getString();
                    txt = new TickableText(I18n.get(key), (this.menu.getOverheat() ? ChatFormatting.RED : ChatFormatting.AQUA).getColor(), true, speed);
                    this.body.add(txt.setTicks(ticks));
                    ticks = Math.max(0, ticks - txt.getMaxUsefulTicks());
                }
            }

            this.runtimeTextLoaded = true;
            this.lastFailState = MergerCameraTileEntity.FailureState.NONE;
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
