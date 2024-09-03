package dev.tr7zw.disguiseheads.armorstand;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import dev.tr7zw.disguiseheads.DisguiseHeadsShared;
import dev.tr7zw.disguiseheads.PlayerModelAccess;
import dev.tr7zw.disguiseheads.util.SkinUtil;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ArmorstandCapeLayer<T extends LivingEntity> extends RenderLayer<T, EntityModel<T>> {

    public static PlayerModel<?> playerModel = null;

    public ArmorstandCapeLayer(RenderLayerParent<T, EntityModel<T>> fakeParent) {
        super(fakeParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity,
            float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
            float headPitch) {
        if (!livingEntity.isInvisible()
                && DisguiseHeadsShared.instance.config.enableArmorstandDisguise
                && DisguiseHeadsShared.instance.config.enableArmorstandCapes) {
            PlayerSkin playerSkin = SkinUtil.getHeadTextureLocation(livingEntity);
            if (playerSkin != null && playerSkin.capeTexture() != null) {
                ItemStack itemStack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
                if (!itemStack.is(Items.ELYTRA)) {
                    poseStack.pushPose();
                    poseStack.translate(0.0F, 0.0F, 0.125F);
                    double d = 0;
                    double e = 0;
                    double f = 0;
                    float g = Mth.rotLerp(partialTicks, livingEntity.yBodyRotO, livingEntity.yBodyRot);
                    double h = (double) Mth.sin(g * (float) (Math.PI / 180.0));
                    double i = (double) (-Mth.cos(g * (float) (Math.PI / 180.0)));
                    float j = (float) e * 10.0F;
                    j = Mth.clamp(j, -6.0F, 32.0F);
                    float k = (float) (d * h + f * i) * 100.0F;
                    k = Mth.clamp(k, 0.0F, 150.0F);
                    float l = (float) (d * i - f * h) * 100.0F;
                    l = Mth.clamp(l, -20.0F, 20.0F);
                    if (k < 0.0F) {
                        k = 0.0F;
                    }

                    float m = 1;
                    j += Mth.sin(Mth.lerp(partialTicks, livingEntity.walkDistO, livingEntity.walkDist) * 6.0F) * 32.0F
                            * m;
                    if (livingEntity.isCrouching()) {
                        j += 25.0F;
                    }

                    poseStack.mulPose(Axis.XP.rotationDegrees(6.0F + k / 2.0F + j));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(l / 2.0F));
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - l / 2.0F));
                    VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entitySolid(playerSkin.capeTexture()));
                    // never crash because of this dumb hack
                    if (playerModel != null) {
                        playerModel.body.translateAndRotate(poseStack);
                        if (livingEntity.isBaby()) {
                            float scale = 1.0F / 2;
                            poseStack.scale(scale, scale, scale);
                            poseStack.translate(0, 1.5, 0.3F);
                        }
                        ((PlayerModelAccess) playerModel).getCapeModel().render(poseStack, vertexConsumer, packedLight,
                                OverlayTexture.NO_OVERLAY);
                    }
                    poseStack.popPose();
                }
            }
        }
    }
}