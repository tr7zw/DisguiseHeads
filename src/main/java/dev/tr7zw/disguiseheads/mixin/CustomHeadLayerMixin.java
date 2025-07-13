package dev.tr7zw.disguiseheads.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.disguiseheads.DisguiseHeadsShared;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(CustomHeadLayer.class)
public abstract class CustomHeadLayerMixin extends RenderLayer {

    CustomHeadLayerMixin(RenderLayerParent renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i,
            LivingEntityRenderState livingEntity, float f, float g, CallbackInfo info) {
        //#if MC >= 12104
        if (livingEntity instanceof PlayerRenderState ps && DisguiseHeadsShared.instance.config.enablePlayerDisguise
                && ps.wornHeadProfile != null) {
            //#else
            //$$ if (shouldHide(livingEntity.headItem) && livingEntity instanceof PlayerRenderState) {
            //#endif
            info.cancel();
        }

    }

    private boolean shouldHide(ItemStack item) {
        if (item.isEmpty() || !DisguiseHeadsShared.instance.config.enablePlayerDisguise)
            return false;
        return item.getItem() == Items.PLAYER_HEAD;
    }

}
