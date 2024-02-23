package dev.tr7zw.disguiseheads.armorstand;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;

public interface FakePlayerHandler<T extends ArmorStand> {

    @SuppressWarnings("rawtypes")
    public default void renderFakePlayer(T livingEntity, float f, float g, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int i, PlayerSkin skin, ArmorStandModel entityModel,
            List<RenderLayer> customLayers) {
        PlayerModel<ArmorStand> model = skin.model() == Model.WIDE ? getDefaultModel() : getSlimModel();
        entityModel.copyPropertiesTo(model);
        VertexConsumer vertices = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(skin.texture()));
        renderPlayerAS(livingEntity, f, g, poseStack, multiBufferSource, i, vertices, model, customLayers);
    }

    public float getAnimationProgressRedirect(T entity, float tickDelta);

    public void setupTransformsRedirect(T entity, PoseStack matrices, float animationProgress, float yBodyRot,
            float tickDelta);

    public void scaleRedirect(T entity, PoseStack matrices, float amount);

    public boolean isVisibleRedirect(T entity);

    public PlayerSkin getHeadTextureLocation(ArmorStand entity);

    public PlayerModel<ArmorStand> getDefaultModel();

    public PlayerModel<ArmorStand> getSlimModel();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public default void renderPlayerAS(T livingEntity, float f, float tick, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int packedLight, VertexConsumer vertices,
            PlayerModel<ArmorStand> targetmodel, List<RenderLayer> customLayers) {
        poseStack.pushPose();
        float scale = livingEntity.isBaby() ? 0.9375F / 2 : 0.9375f;
        poseStack.scale(scale, scale, scale);
        targetmodel.attackTime = 0;
        targetmodel.riding = livingEntity.isPassenger();
        targetmodel.young = false;
        targetmodel.crouching = false;
        float h = Mth.rotLerp(tick, livingEntity.yBodyRotO, livingEntity.yBodyRot);
        float j = Mth.rotLerp(tick, livingEntity.yHeadRotO, livingEntity.yHeadRot);
        if (livingEntity.isPassenger() && livingEntity.getVehicle() instanceof LivingEntity veh) {
            h = Mth.rotLerp(tick, veh.yBodyRotO, veh.yBodyRot);
        }
        float o = 0;//getAnimationProgressRedirect(livingEntity, tick);
        this.setupTransformsRedirect(livingEntity, poseStack, o, h, tick);
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        this.scaleRedirect(livingEntity, poseStack, tick);
        poseStack.translate(0.0, -1.5010000467300415, 0.0);
        if (targetmodel.riding) {
            if (targetmodel.young) {
                poseStack.translate(0.0, 0.3, 0.0);
            } else {
                poseStack.translate(0.0, 0.5, 0.0);
            }
        }

        Minecraft minecraft = Minecraft.getInstance();
        boolean bl = this.isVisibleRedirect(livingEntity);
        boolean bl2 = !bl && !livingEntity.isInvisibleTo(minecraft.player);
        int r = LivingEntityRenderer.getOverlayCoords(livingEntity, 0);
        targetmodel.renderToBuffer(poseStack, vertices, packedLight, r, 1.0f, 1.0f, 1.0f, bl2 ? 0.15f : 1.0f);
        for (RenderLayer renderLayer : customLayers) {
            renderLayer.render(poseStack, multiBufferSource, packedLight, livingEntity, 0, 0, tick, 0, h, j);
        }
        poseStack.popPose();
        //super.render(livingEntity, f, g, PoseStack, MultiBufferSource, i);
    }

}
