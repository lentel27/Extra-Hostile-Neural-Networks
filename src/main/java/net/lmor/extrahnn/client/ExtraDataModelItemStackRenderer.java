package net.lmor.extrahnn.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.shadowsoffire.hostilenetworks.client.WeirdRenderThings;
import dev.shadowsoffire.hostilenetworks.client.WrappedRTBuffer;
import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.util.ClientEntityCache;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.lmor.extrahnn.ExtraHostileNetworks;
import net.lmor.extrahnn.common.item.ExtraDataModelItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExtraDataModelItemStackRenderer extends BlockEntityWithoutLevelRenderer {
    private static final MultiBufferSource.BufferSource GHOST_ENTITY_BUF = MultiBufferSource.immediate(new  ByteBufferBuilder(256));
    private static final ModelResourceLocation DATA_MODEL_BASE = ModelResourceLocation.standalone(ExtraHostileNetworks.local("item/extra_data_model_base"));

    public ExtraDataModelItemStackRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext type, @NotNull PoseStack matrix, @NotNull MultiBufferSource buf, int light, int overlay) {
        ItemRenderer iRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel base = iRenderer.getItemModelShaper().getModelManager().getModel(DATA_MODEL_BASE);
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

        //noinspection deprecation
        iRenderer.renderModelLists(base, stack, light, overlay, matrix, ItemRenderer.getFoilBufferDirect(GHOST_ENTITY_BUF, ItemBlockRenderTypes.getRenderType(stack, true), true, false));
        GHOST_ENTITY_BUF.endBatch();
        matrix.popPose();

        List<DynamicHolder<DataModel>> models = ExtraDataModelItem.getStoredModels(stack);
        int count = 0;
        for (DynamicHolder<DataModel> model: models){
            if (model.isBound()) {
                Entity ent = ClientEntityCache.computeIfAbsent(model.get().entity(), Minecraft.getInstance().level, model.get().display().nbt());
                if (Minecraft.getInstance().player != null) ent.tickCount = Minecraft.getInstance().player.tickCount;
                if (ent != null) {
                    this.renderEntityInInventory(matrix, type, ent, model.get(), count);
                }
                count++;
            }
        }
    }

    public void renderEntityInInventory(PoseStack matrix, ItemDisplayContext type, Entity entity, DataModel model, int count) {
        matrix.pushPose();
        matrix.translate(count == 0 || count == 3 ? 0.4 : 0.6, 0.5, count == 1 || count == 3? 0.4 : 0.6);
        float rotation;
        float scale = model.display().scale();
        if (type == ItemDisplayContext.FIXED) {
            matrix.translate(0.0, -0.5, 0.0);
            scale *= 0.3F;
            matrix.scale(scale, scale, scale);
            matrix.translate(0.0, 1.45, 0.0);
            matrix.mulPose(Axis.XN.rotationDegrees(90.0F));
            matrix.mulPose(Axis.YN.rotationDegrees(180.0F));
        } else if (type == ItemDisplayContext.GUI) {
            matrix.translate(0.0, -0.5, 0.0);
            scale *= 0.3F;
            matrix.scale(scale, scale, scale);
            matrix.translate(0.0, 0.45, 0.0);
        } else {
            scale *= 0.15F;
            matrix.scale(scale, scale, scale);
            matrix.translate(0.0, 0.12 + 0.05 * Math.sin(((float)entity.tickCount + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true)) / 12.0F), 0.0);
        }

        rotation = -30.0F;
        if (type == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || type == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) rotation = 30;
        if (type == ItemDisplayContext.FIXED) rotation = 180;
        matrix.mulPose(Axis.YP.rotationDegrees(rotation));
        entity.setYRot(0);

        if (entity instanceof LivingEntity living) {
            living.yBodyRot = entity.getYRot();
            living.yHeadRot = entity.getYRot();
            living.yHeadRotO = entity.getYRot();
        }

        EntityRenderDispatcher entityRendererManager = Minecraft.getInstance().getEntityRenderDispatcher();
        entityRendererManager.setRenderShadow(false);
        MultiBufferSource.BufferSource rtBuffer = GHOST_ENTITY_BUF;
        WeirdRenderThings.translucent = true;
        //noinspection deprecation
        RenderSystem.runAsFancy(() -> entityRendererManager.render(entity, model.display().xOffset(), model.display().yOffset(), model.display().zOffset(), 0.0F, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true), matrix,
                new WrappedRTBuffer(rtBuffer), 15728880)
        );
        rtBuffer.endBatch();
        WeirdRenderThings.translucent = false;
        entityRendererManager.setRenderShadow(true);
        matrix.popPose();
    }
}
