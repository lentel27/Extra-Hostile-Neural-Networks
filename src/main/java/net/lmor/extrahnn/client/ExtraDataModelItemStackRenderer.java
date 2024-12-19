package net.lmor.extrahnn.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.shadowsoffire.hostilenetworks.client.WeirdRenderThings;
import dev.shadowsoffire.hostilenetworks.client.WrappedRTBuffer;
import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.util.ClientEntityCache;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.lmor.extrahnn.ExtraHostileNetworks;
import net.lmor.extrahnn.item.ExtraDataModelItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.FORGE, value = {Dist.CLIENT}, modid = "extrahnn")
public class ExtraDataModelItemStackRenderer extends BlockEntityWithoutLevelRenderer {
    private static final MultiBufferSource.BufferSource GHOST_ENTITY_BUF = MultiBufferSource.immediate(new BufferBuilder(256));
    private static final ResourceLocation DATA_MODEL_BASE = ExtraHostileNetworks.local("item/extra_data_model_base");

    public ExtraDataModelItemStackRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public void renderByItem(ItemStack stack, ItemDisplayContext type, PoseStack matrix, MultiBufferSource buf, int light, int overlay) {
        ItemRenderer irenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel base = irenderer.getItemModelShaper().getModelManager().getModel(DATA_MODEL_BASE);
        matrix.pushPose();
        float scale;
        if (type == ItemDisplayContext.FIXED) {
            matrix.translate(1.0F, 1.0F, 0.0F);
            scale = 0.5F;
            matrix.scale(scale, scale, scale);
            matrix.translate(-1.5F, -0.5F, 0.5F);
            matrix.mulPose(Axis.XP.rotationDegrees(90.0F));
            matrix.mulPose(Axis.XP.rotationDegrees(90.0F));
            matrix.translate(0.0F, 0.0F, -1.0F);
        } else if (type != ItemDisplayContext.GUI) {
            matrix.translate(1.0F, 1.0F, 0.0F);
            scale = 0.5F;
            matrix.scale(scale, scale, scale);
            matrix.translate(-1.5F, -0.5F, 0.5F);
            matrix.mulPose(Axis.XP.rotationDegrees(90.0F));
        } else {
            matrix.translate(0.0F, -0.5F, -0.5F);
            matrix.mulPose(Axis.XN.rotationDegrees(75.0F));
            matrix.mulPose(Axis.ZP.rotationDegrees(45.0F));
            scale = 0.9F;
            matrix.scale(scale, scale, scale);
            matrix.translate(0.775, 0.0, -0.0825);
        }

        irenderer.renderModelLists(base, stack, light, overlay, matrix, ItemRenderer.getFoilBufferDirect(GHOST_ENTITY_BUF, ItemBlockRenderTypes.getRenderType(stack, true), true, false));
        GHOST_ENTITY_BUF.endBatch();
        matrix.popPose();

        List<DynamicHolder<DataModel>> models = ExtraDataModelItem.getStoredModels(stack);
        int count = 0;
        for (DynamicHolder<DataModel> model: models){
            if (model.isBound()) {
                LivingEntity ent = ClientEntityCache.computeIfAbsent((model.get()).type(), Minecraft.getInstance().level, (model.get()).displayNbt());
                if (Minecraft.getInstance().player != null) {
                    ent.tickCount = Minecraft.getInstance().player.tickCount;
                }

                if (ent != null) {
                    this.renderEntityInInventory(matrix, type, ent, model.get(), count);
                }
                count++;
//                matrix.pushPose();
//                scale = 0.5F;
//                matrix.scale(scale, scale, scale);
//                matrix.popPose();
            }
        }
    }

    public void renderEntityInInventory(PoseStack matrix, ItemDisplayContext type, LivingEntity pLivingEntity, DataModel model, int count) {
        matrix.pushPose();
        matrix.translate(count == 0 || count == 3 ? 0.4 : 0.6, 0.5, count == 1 || count == 3? 0.4 : 0.6);
        float rotation;
        if (type == ItemDisplayContext.FIXED) {
            matrix.translate(0.0, -0.5, 0.0);
            rotation = 0.3F;
            rotation *= model.guiScale();
            matrix.scale(rotation, rotation, rotation);
            matrix.translate(0.0, 1.45, 0.0);
            matrix.mulPose(Axis.XN.rotationDegrees(90.0F));
            matrix.mulPose(Axis.YN.rotationDegrees(180.0F));
        } else if (type == ItemDisplayContext.GUI) {
            matrix.translate(0.0, -0.5, 0.0);
            rotation = 0.3F;
            rotation *= model.guiScale();
            matrix.scale(rotation, rotation, rotation);
            matrix.translate(0.0, 0.45, 0.0);
        } else {
            rotation = 0.15F;
            rotation *= model.guiScale();
            matrix.scale(rotation, rotation, rotation);
            matrix.translate(0.0, 0.12 + 0.05 * Math.sin((double)(((float)pLivingEntity.tickCount + Minecraft.getInstance().getDeltaFrameTime()) / 12.0F)), 0.0);
        }

        rotation = -30.0F;
        if (type == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || type == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            rotation = 30.0F;
        }

        if (type == ItemDisplayContext.FIXED) {
            rotation = 180.0F;
        }

        matrix.mulPose(Axis.YP.rotationDegrees(rotation));
        pLivingEntity.setYRot(0.0F);
        pLivingEntity.yBodyRot = pLivingEntity.getYRot();
        pLivingEntity.yHeadRot = pLivingEntity.getYRot();
        pLivingEntity.yHeadRotO = pLivingEntity.getYRot();
        EntityRenderDispatcher entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
        entityrenderermanager.setRenderShadow(false);
        MultiBufferSource.BufferSource rtBuffer = GHOST_ENTITY_BUF;
        WeirdRenderThings.translucent = true;
        RenderSystem.runAsFancy(() -> {
            entityrenderermanager.render(pLivingEntity, (double)model.guiXOff(), (double)model.guiYOff(), (double)model.guiZOff(), 0.0F, Minecraft.getInstance().getDeltaFrameTime(), matrix, new WrappedRTBuffer(rtBuffer), 15728880);
        });
        rtBuffer.endBatch();
        WeirdRenderThings.translucent = false;
        entityrenderermanager.setRenderShadow(true);
        matrix.popPose();
    }
}
