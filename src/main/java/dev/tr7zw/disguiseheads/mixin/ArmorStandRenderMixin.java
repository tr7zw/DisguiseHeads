package dev.tr7zw.disguiseheads.mixin;

import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.disguiseheads.DisguiseHeadsShared;
import dev.tr7zw.disguiseheads.util.SkinUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("rawtypes")
@Mixin(LivingEntityRenderer.class)
public abstract class ArmorStandRenderMixin<T extends LivingEntity, V extends EntityModel> extends EntityRenderer {

    protected ArmorStandRenderMixin(Context context) {
        super(context);
    }

    boolean rendering = false;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(LivingEntityRenderState livingEntity, PoseStack poseStack, MultiBufferSource multiBufferSource,
            int packedLight, CallbackInfo info) {
        if (rendering) {
            return;
        }
        if (!(livingEntity instanceof PlayerRenderState) && !livingEntity.isInvisible
                && DisguiseHeadsShared.instance.config.enableArmorstandDisguise) {
            PlayerSkin skin = SkinUtil.getHeadTextureLocation(livingEntity.headItem);
            if (skin != null) {
                EntityRenderer playerRenderer = Minecraft.getInstance().getEntityRenderDispatcher()
                        .getRenderer(Minecraft.getInstance().player);
                rendering = true;
                PlayerRenderState fakePlayer = (PlayerRenderState) playerRenderer.createRenderState();
                remapRenderState(livingEntity, fakePlayer);
                fakePlayer.skin = skin;
                if (fakePlayer.isBaby) {
                    fakePlayer.scale = 0.5f;
                }
                fakePlayer.isBaby = false;
                if (DisguiseHeadsShared.instance.config.hideArmorstandHead) {
                    fakePlayer.headItem = ItemStack.EMPTY;
                }
                playerRenderer.render(fakePlayer, poseStack, multiBufferSource, packedLight);
                rendering = false;
                info.cancel();
            }
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
