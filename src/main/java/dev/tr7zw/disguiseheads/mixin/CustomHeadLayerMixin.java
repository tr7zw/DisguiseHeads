package dev.tr7zw.disguiseheads.mixin;

import com.mojang.blaze3d.vertex.*;
import dev.tr7zw.disguiseheads.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.client.renderer.entity.state.*;

@Mixin(CustomHeadLayer.class)
public abstract class CustomHeadLayerMixin extends RenderLayer {

    CustomHeadLayerMixin(RenderLayerParent renderLayerParent) {
        super(renderLayerParent);
    }

    //? if >= 1.21.9 {

    @Inject(at = @At("HEAD"), method = "submit", cancellable = true)
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i,
            LivingEntityRenderState livingEntity, float f, float g, CallbackInfo info) {
        //? } else {
        /*
        @Inject(at = @At("HEAD"), method = "render", cancellable = true)
        public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i,
            LivingEntityRenderState livingEntity, float f, float g, CallbackInfo info) {
         *///? }
            //? if >= 1.21.4 {

        if (livingEntity instanceof /*? if >=1.21.9 {*/ AvatarRenderState /*?} else {*//*PlayerRenderState*//*?}*/ ps
                && DisguiseHeadsShared.instance.config.enablePlayerDisguise && ps.wornHeadProfile != null) {
            //? } else {
            /*
             if (shouldHide(livingEntity.headItem) && livingEntity instanceof PlayerRenderState) {
            *///? }
            info.cancel();
        }

    }

    private boolean shouldHide(ItemStack item) {
        if (item.isEmpty() || !DisguiseHeadsShared.instance.config.enablePlayerDisguise)
            return false;
        return item.getItem() == Items.PLAYER_HEAD;
    }

}
