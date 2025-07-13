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
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.scores.PlayerTeam;

@Mixin(PlayerRenderer.class)
public abstract class EntityRendererMixin
        extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public EntityRendererMixin(Context context, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "renderNameTag", cancellable = true, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", shift = Shift.BY, by = -5))
    private void injected(AbstractClientPlayer player, Component displayName, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight,
            //#if MC >= 12005
            float f,
            //#endif
            CallbackInfo ci) {
        if (!DisguiseHeadsShared.instance.config.enablePlayerDisguise
                || !DisguiseHeadsShared.instance.config.changeNameToDisguise) {
            return;
        }
        ItemStack itemStack = player.getItemBySlot(EquipmentSlot.HEAD);
        Item item = itemStack.getItem();
        if (item instanceof BlockItem blockitem && blockitem.getBlock() instanceof AbstractSkullBlock) {
            GameProfile gameProfile = SkinUtil.getGameProfile(itemStack);
            if (gameProfile != null) {
                MutableComponent mutableComponent = PlayerTeam.formatNameForTeam(player.getTeam(),
                        ComponentProvider.literal(gameProfile.getName()));
                super.renderNameTag(player, mutableComponent, poseStack, buffer, packedLight
                //#if MC >= 12005
                        , f);
                //#else
                //$$ );
                //#endif
                poseStack.popPose();
                ci.cancel();
                return;
            }
        }
    }

}
