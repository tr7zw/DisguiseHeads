package dev.tr7zw.disguiseheads.mixin;

import com.mojang.blaze3d.vertex.*;
import dev.tr7zw.disguiseheads.*;
import dev.tr7zw.disguiseheads.util.*;
import net.minecraft.client.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider.*;
import net.minecraft.client.renderer.entity.state.*;
import net.minecraft.client.renderer.state.*;
import net.minecraft.client.resources.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.lang.reflect.*;

@SuppressWarnings("rawtypes")
@Mixin(LivingEntityRenderer.class)
public abstract class ArmorStandRenderMixin<T extends LivingEntity, V extends EntityModel> extends EntityRenderer {

    protected ArmorStandRenderMixin(Context context) {
        super(context);
    }

    boolean rendering = false;

    //? if >= 1.21.9 {

    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    public void submit(LivingEntityRenderState livingEntity, PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, CallbackInfo info) {
        //? } else {
        /*
        @Inject(method = "render", at = @At("HEAD"), cancellable = true)
        public void render(LivingEntityRenderState livingEntity, PoseStack poseStack, MultiBufferSource multiBufferSource,
            int packedLight, CallbackInfo info) {
        *///? }
        if (rendering) {
            return;
        }
        if (!(livingEntity instanceof /*? if >=1.21.9 {*/ AvatarRenderState /*?} else {*//*PlayerRenderState*//*?}*/)
                && livingEntity instanceof LivingEntityExtender hs && !livingEntity.isInvisible) {
            if (!(DisguiseHeadsShared.instance.config.enableEverythingDisguise
                    || (livingEntity instanceof ArmorStandRenderState
                            && DisguiseHeadsShared.instance.config.enableArmorstandDisguise))) {
                return;
            }
            //? if >= 1.21.4 {

            PlayerSkin skin = SkinUtil.getHeadTextureLocation(hs.getHeadItem());
            //? } else {
            /*
             PlayerSkin skin = SkinUtil.getHeadTextureLocation(livingEntity.headItem);
            *///? }
            if (skin != null) {
                EntityRenderer playerRenderer = Minecraft.getInstance().getEntityRenderDispatcher()
                        .getRenderer(Minecraft.getInstance().player);
                rendering = true;
                var fakePlayer = (/*? if >=1.21.9 {*/ AvatarRenderState /*?} else {*//*PlayerRenderState*//*?}*/) playerRenderer
                        .createRenderState();
                remapRenderState(livingEntity, fakePlayer);
                fakePlayer.skin = skin;
                if (fakePlayer.isBaby) {
                    fakePlayer.scale = 0.5f;
                }
                fakePlayer.isBaby = false;
                if (DisguiseHeadsShared.instance.config.hideArmorstandHead) {
                    //? if >= 1.21.4 {

                    hs.setHeadItem(null);
                    //? } else {
                    /*
                     fakePlayer.headItem = net.minecraft.world.item.ItemStack.EMPTY;
                    *///? }
                }
                //? if >= 1.21.9 {

                playerRenderer.submit(fakePlayer, poseStack, submitNodeCollector, cameraRenderState);
                //? } else {
                /*
                playerRenderer.render(fakePlayer, poseStack, multiBufferSource, packedLight);
                *///? }
                rendering = false;
                info.cancel();
            }
        }
    }

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    public void extractRenderState(LivingEntity livingEntity, LivingEntityRenderState livingEntityRenderState, float f,
            CallbackInfo ci) {
        if (livingEntityRenderState instanceof LivingEntityExtender ext) {
            ext.setHeadItem(livingEntity.getItemBySlot(EquipmentSlot.HEAD));
        }
    }

    private static void remapRenderState(EntityRenderState from, EntityRenderState to) {
        for (Field src : from.getClass().getFields()) {
            try {
                Field target = to.getClass().getField(src.getName());
                if (target != null) {
                    target.set(to, src.get(from));
                }
            } catch (Exception ex) {
            }
        }
    }

}
