package net.lmor.extrahnn.client.screen;

import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.placebo.screen.PlaceboContainerScreen;
import dev.shadowsoffire.placebo.screen.TickableTextList;
import net.lmor.extrahnn.EHNNUtils;
import net.lmor.extrahnn.ExtraHostileConfig;
import net.lmor.extrahnn.ExtraHostileNetworks;
import net.lmor.extrahnn.common.container.MergerCameraContainer;
import net.lmor.extrahnn.common.tile.MergerCameraTileEntity.FailureState;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MergerCameraScreen extends PlaceboContainerScreen<MergerCameraContainer> {

    public static final int WIDTH = 216;
    public static final int HEIGHT = 241;
    public static final int MAX_TEXT_WIDTH = 189;

    private static final ResourceLocation BASE = ExtraHostileNetworks.local("textures/gui/merger_camera.png");
    private static final ResourceLocation PLAYER = ExtraHostileNetworks.local("textures/gui/inventory.png");

    private TickableTextList body;
    private FailureState lastFailState = FailureState.NONE;
    private boolean runtimeTextLoaded = false;

    public MergerCameraScreen(MergerCameraContainer menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;
    }

    @Override
    public void init() {
        super.init();
        this.body = new TickableTextList(Objects.requireNonNull(this.minecraft).font, MAX_TEXT_WIDTH);
        this.lastFailState = FailureState.NONE;
        this.containerTick();
    }

    protected void renderTooltip(@NotNull GuiGraphics graphics, int pX, int pY) {
        if (this.isHovering(203, 59, 7, 87, pX, pY)) {
            List<Component> txt = new ArrayList<>(2);
            txt.add(Component.translatable("hostilenetworks.gui.energy", this.menu.getEnergyStored(), ExtraHostileConfig.mergerCameraPowerCap));
            txt.add(Component.translatable("hostilenetworks.gui.cost", ExtraHostileConfig.mergerCameraPowerCost));

            graphics.renderComponentTooltip(this.font, txt, pX, pY);

        } else {
            super.renderTooltip(graphics, pX, pY);
        }

    }

    protected void renderLabels(@NotNull GuiGraphics graphics, int pX, int pY) {
        int runtime = this.menu.getRuntime();
        if (runtime > 0) {
            int rTime = Math.min(99, Mth.ceil(100.0F * (float)(ExtraHostileConfig.mergerCameraPowerDuration - runtime) / (float) ExtraHostileConfig.mergerCameraPowerDuration));
            graphics.drawString(this.font, rTime + "%", 178, 135, 6478079, true);
        }

        this.body.render(graphics, 8, 61);
    }


    @Override
    protected void renderBg(GuiGraphics gfx, float pPartialTicks, int pX, int pY) {
        int left = this.getGuiLeft();
        int top = this.getGuiTop();
        gfx.blit(BASE, left, top, 0.0F, 0.0F, 216, 152, 256, 256);

        int runtime = this.menu.getRuntime();
        int dur = ExtraHostileConfig.mergerCameraPowerDuration;
        int part = runtime == 0 ? 0 : Mth.ceil(9.0F * Math.min(dur - runtime, (int)(dur * 0.05f)) /  (int)(dur * 0.05f));
        for (int i = 0; i < 2; i++){
            gfx.blit(BASE, left + 62 + (i == 0 ? 0: 18 - part), top + 27, i == 0 ? 0: 9, 177, part, 1, 256, 256);
            gfx.blit(BASE, left + 87 + (i == 0 ? 0: 18 - part), top + 25, i == 0 ? 0: 9, 177, part, 1, 256, 256);
            gfx.blit(BASE, left + 111 + (i == 0 ? 0: 18 - part), top + 25, i == 0 ? 0: 9, 177, part, 1, 256, 256);
            gfx.blit(BASE, left + 136 + (i == 0 ? 0: 18 - part), top + 27, i == 0 ? 0: 9, 177, part, 1, 256, 256);
        }

        if (runtime != 0 && runtime < dur * 0.95) {
            float min = Math.min(dur - runtime, dur * 0.55f);
            part = Mth.ceil(16 * min / (dur * 0.55f));
            int part_2 = Mth.ceil(5 * min / (dur * 0.55f));
            gfx.blit(BASE, left + 70, top + 28, 0, 192, 2, part, 256, 256);
            gfx.blit(BASE, left + 95, top + 26, 0, 182, 2, part_2, 256, 256);
            gfx.blit(BASE, left + 119, top + 26, 0, 182, 2, part_2, 256, 256);
            gfx.blit(BASE, left + 144, top + 28, 0, 192, 2, part, 256, 256);

            if (runtime < dur - (int) (dur * 0.45f)){
                part = Mth.ceil(27 * (dur * 0.45f - runtime) / (dur * 0.45f));
                gfx.blit(BASE, left + 72, top + 42, 0, 209, part, 2, 256, 256);
                gfx.blit(BASE, left + 117 + 27 - part, top + 42, 0, 209, part, 2, 256, 256);

                part =(int) Math.ceil(8 * (dur * 0.60f - runtime) / (dur * 0.60f));
                gfx.blit(BASE, left + 97, top + 29, 0, 188, part, 2, 256, 256);
                gfx.blit(BASE, left + 111 + 8 - part, top + 29, 0, 188, part, 2, 256, 256);

                if (runtime < dur * 0.05) {
                    part = Mth.ceil(3 * (dur * 0.05f - runtime) / (dur * 0.05f));
                    gfx.blit(BASE, left + 103, top + 31, 3, 182, 2, part, 256, 256);
                    gfx.blit(BASE, left + 111, top + 31, 3, 182, 2, part, 256, 256);
                }
            }
        }

        int energyHeight = 87 - Mth.ceil(87.0F * (float)this.menu.getEnergyStored() / (float)ExtraHostileConfig.mergerCameraPowerCap);
        gfx.blit(BASE, left + 203, top + 59, 216, 0, 7, energyHeight, 256, 256);
        gfx.blit(PLAYER, left + 20, top + 153, 0.0F, 0.0F, 176, 90, 256, 256);
    }

    public void containerTick() {
        if (this.menu.getFailState() != FailureState.NONE) {
            FailureState oState = this.lastFailState;
            this.lastFailState = this.menu.getFailState();
            if (oState != this.lastFailState) {
                this.body.clear();
                MutableComponent msg = Component.translatable(this.lastFailState.getKey());
                this.body.addLine(msg, 1);
            }

            this.runtimeTextLoaded = false;
        } else if (!this.runtimeTextLoaded) {
            int ticks = ExtraHostileConfig.mergerCameraPowerDuration - this.menu.getRuntime();
            this.body.clear();

            List<Component> textAll = new ArrayList<>();
            for(int i = 0; i < 6; ++i) {
                textAll.add(i == 0 ? Component.literal("v" + ExtraHostileNetworks.VERSION + "\n").withStyle(ChatFormatting.GOLD) : Component.translatable("extrahnn.merger_camera.run." + i));
            }
            int allChar = textAll.stream().mapToInt(x -> x.getString().length()).sum();

            float speed = EHNNUtils.textTickRate(allChar, ExtraHostileConfig.mergerCameraPowerDuration);
            for (Component text: textAll){
                this.body.continueLine(text, speed);
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
}
