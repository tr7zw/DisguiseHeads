package dev.tr7zw.disguiseheads.mixin;

import com.mojang.authlib.*;
import com.mojang.blaze3d.vertex.*;
import dev.tr7zw.disguiseheads.*;
import dev.tr7zw.disguiseheads.util.*;
import dev.tr7zw.transition.mc.*;
import dev.tr7zw.transition.mc.entitywrapper.*;
//? if >= 1.21.11 {
import net.minecraft.client.model.player.*;
//? }
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.player.*;
import net.minecraft.client.renderer.entity.state.*;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.*;
import org.spongepowered.asm.mixin.injection.callback.*;

//? if >= 1.21.9 {
@Mixin(value = AvatarRenderer.class)
//? } else {
/*@Mixin(value = PlayerRenderer.class)
 *///? }
public abstract class EntityRendererMixin extends LivingEntityRenderer {

    public EntityRendererMixin(Context context, PlayerModel model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    //? if >= 1.21.9 {

    @Inject(method = "extractRenderState", at = @At(value = "RETURN"))
    public void extractRenderState(Avatar avatar, AvatarRenderState avatarRenderState, float f, CallbackInfo ci) {
        if (!DisguiseHeadsShared.instance.config.enablePlayerDisguise
                || !DisguiseHeadsShared.instance.config.changeNameToDisguise) {
            return;
        }
        ItemStack itemStack = avatar.getItemBySlot(EquipmentSlot.HEAD);
        Item item = itemStack.getItem();
        if (item instanceof BlockItem blockitem && blockitem.getBlock() instanceof AbstractSkullBlock) {
            GameProfile gameProfile = SkinUtil.getGameProfile(itemStack);
            if (gameProfile != null) {
                avatarRenderState.nameTag = ComponentProvider.literal(gameProfile.name());
            }
        }
    }
    //? } else {
    /*
    @Inject(method = "renderNameTag", cancellable = true, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", shift = Shift.BY, by = -5))
    private void injected(PlayerRenderState player, Component displayName, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (!DisguiseHeadsShared.instance.config.enablePlayerDisguise
                || !DisguiseHeadsShared.instance.config.changeNameToDisguise) {
            return;
        }
        //? if >= 1.21.4 {
    
        ItemStack itemStack = player.headEquipment;
        //? } else {
    
         ItemStack itemStack = player.headItem;
        //? }
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
     *///? }

}
