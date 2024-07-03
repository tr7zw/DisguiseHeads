package dev.tr7zw.disguiseheads.armorstand;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.disguiseheads.DisguiseHeadsShared;
import dev.tr7zw.disguiseheads.util.SkinUtil;
import dev.tr7zw.util.NMSHelper;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ArmorStandElytraLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation WINGS_LOCATION = NMSHelper.getResourceLocation("textures/entity/elytra.png");
    private final ElytraModel<T> elytraModel;

    public ArmorStandElytraLayer(RenderLayerParent<T, M> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.elytraModel = new ElytraModel(modelSet.bakeLayer(ModelLayers.ELYTRA));
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing,
            float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemStack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
        if (itemStack.is(Items.ELYTRA)) {
            ResourceLocation resourceLocation;
            if (livingEntity instanceof ArmorStand armorstand
                    && DisguiseHeadsShared.instance.config.enableArmorstandDisguise) {
                PlayerSkin playerSkin = SkinUtil.getHeadTextureLocation(armorstand);
                if (playerSkin.elytraTexture() != null) {
                    resourceLocation = playerSkin.elytraTexture();
                } else if (playerSkin.capeTexture() != null) {
                    resourceLocation = playerSkin.capeTexture();
                } else {
                    resourceLocation = WINGS_LOCATION;
                }
            } else {
                resourceLocation = WINGS_LOCATION;
            }

            poseStack.pushPose();
            poseStack.translate(0.0F, 0.0F, 0.125F);
            this.getParentModel().copyPropertiesTo(this.elytraModel);
            this.elytraModel.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            // spotless:off
            //#if MC >= 12100
            VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(buffer,
                    RenderType.armorCutoutNoCull(resourceLocation), itemStack.hasFoil());
            this.elytraModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            //#else
            //$$ VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(buffer,
            //$$         RenderType.armorCutoutNoCull(resourceLocation), false, itemStack.hasFoil());
            //$$ this.elytraModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            //#endif
            //spotless:on
            poseStack.popPose();
        }
    }
}