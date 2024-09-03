package dev.tr7zw.disguiseheads.armorstand;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.disguiseheads.DisguiseHeadsShared;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public interface FakePlayerHandler<T extends LivingEntity, V extends EntityModel<T>> {

    @SuppressWarnings("rawtypes")
    public default void renderFakePlayer(T livingEntity, float f, float g, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int i, PlayerSkin skin, V entityModel,
            List<RenderLayer> customLayers) {
        PlayerModel<T> model = skin.model() == Model.WIDE ? getDefaultModel() : getSlimModel();
        entityModel.copyPropertiesTo(model);
        if(entityModel instanceof HumanoidModel human) {
            model.leftArmPose = human.leftArmPose;
            model.rightArmPose = human.rightArmPose;
            ((HumanoidModel) entityModel).copyPropertiesTo(model);
        }
        if(entityModel instanceof HierarchicalModel hir) {
            hir.getAnyDescendantWithName("left_leg").ifPresent(leg -> {
                model.leftLeg.copyFrom((ModelPart) leg);
            });
            hir.getAnyDescendantWithName("right_leg").ifPresent(leg -> {
                model.rightLeg.copyFrom((ModelPart) leg);
            });
            hir.getAnyDescendantWithName("head").ifPresent(head -> {
                model.getHead().copyFrom((ModelPart) head);
            });
            hir.getAnyDescendantWithName("left_arm").ifPresent(arm -> {
                model.leftArm.copyFrom((ModelPart) arm);
            });
            hir.getAnyDescendantWithName("right_arm").ifPresent(arm -> {
                model.rightArm.copyFrom((ModelPart) arm);
            });
        }
        model.hat.copyFrom(model.head);
        model.leftPants.copyFrom(model.leftLeg);
        model.rightPants.copyFrom(model.rightLeg);
        model.leftSleeve.copyFrom(model.leftArm);
        model.rightSleeve.copyFrom(model.rightArm);
        model.jacket.copyFrom(model.body);
        VertexConsumer vertices = multiBufferSource.getBuffer(RenderType.entityTranslucent(skin.texture()));
        renderPlayerAS(livingEntity, f, g, poseStack, multiBufferSource, i, vertices, model, customLayers);
    }

    public float getAnimationProgressRedirect(T entity, float tickDelta);

    public void setupTransformsRedirect(T entity, PoseStack matrices, float animationProgress, float yBodyRot,
            float tickDelta);

    public void scaleRedirect(T entity, PoseStack matrices, float amount);

    public boolean isVisibleRedirect(T entity);

    public PlayerModel<T> getDefaultModel();

    public PlayerModel<T> getSlimModel();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public default void renderPlayerAS(T livingEntity, float f, float tick, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int packedLight, VertexConsumer vertices,
            PlayerModel<T> targetmodel, List<RenderLayer> customLayers) {
        poseStack.pushPose();
        float scale = 1;
        poseStack.scale(scale, scale, scale);
        targetmodel.attackTime = livingEntity.getAttackAnim(tick);
        targetmodel.riding = livingEntity.isPassenger();
        targetmodel.young = livingEntity.isBaby();
        targetmodel.crouching = false;
        float h = Mth.rotLerp(tick, livingEntity.yBodyRotO, livingEntity.yBodyRot);
        float j = Mth.rotLerp(tick, livingEntity.yHeadRotO, livingEntity.yHeadRot);
        if (livingEntity.isPassenger() && livingEntity.getVehicle() instanceof LivingEntity veh) {
            h = Mth.rotLerp(tick, veh.yBodyRotO, veh.yBodyRot);
        }
        float o = getAnimationProgressRedirect(livingEntity, tick);
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

        int r = LivingEntityRenderer.getOverlayCoords(livingEntity, 0);
        // spotless:off
        //#if MC >= 12100
        targetmodel.renderToBuffer(poseStack, vertices, packedLight, r);
        //#else
        //$$ Minecraft minecraft = Minecraft.getInstance();
        //$$ boolean bl = this.isVisibleRedirect(livingEntity);
        //$$ boolean bl2 = !bl && !livingEntity.isInvisibleTo(minecraft.player);
        //$$ targetmodel.renderToBuffer(poseStack, vertices, packedLight, r, 1.0f, 1.0f, 1.0f, bl2 ? 0.15f : 1.0f);
        //#endif
        //spotless:on
        ArmorstandCapeLayer.playerModel = targetmodel; // dumb workaround
        for (RenderLayer renderLayer : customLayers) {
            if (!DisguiseHeadsShared.instance.config.hideArmorstandHead || !(renderLayer instanceof CustomHeadLayer)) {
                renderLayer.render(poseStack, multiBufferSource, packedLight, livingEntity, 0, 0, tick, 0, h, j);
            }
        }
        poseStack.popPose();
        // super.render(livingEntity, f, g, PoseStack, MultiBufferSource, i);
    }

}
