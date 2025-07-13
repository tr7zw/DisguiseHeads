package dev.tr7zw.disguiseheads.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.disguiseheads.DisguiseHeadsShared;
import dev.tr7zw.disguiseheads.util.SkinUtil;
import dev.tr7zw.transition.mc.ComponentProvider;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;

@Mixin(PlayerRenderer.class)
public abstract class EntityRendererMixin extends LivingEntityRenderer {

    public EntityRendererMixin(Context context, PlayerModel model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "renderNameTag", cancellable = true, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", shift = Shift.BY, by = -5))
    private void injected(PlayerRenderState player, Component displayName, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (!DisguiseHeadsShared.instance.config.enablePlayerDisguise
                || !DisguiseHeadsShared.instance.config.changeNameToDisguise) {
            return;
        }
        //#if MC >= 12104
        ItemStack itemStack = player.headEquipment;
        //#else
        //$$ItemStack itemStack = player.headItem;
        //#endif
        Item item = itemStack.getItem();
        if (item instanceof BlockItem blockitem && blockitem.getBlock() instanceof AbstractSkullBlock) {
            GameProfile gameProfile = SkinUtil.getGameProfile(itemStack);
            if (gameProfile != null) {
                super.renderNameTag(player, ComponentProvider.literal(gameProfile.getName()), poseStack, buffer,
                        packedLight);
                poseStack.popPose();
                ci.cancel();
                return;
            }
        }
    }

}
