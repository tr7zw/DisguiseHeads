package dev.tr7zw.disguiseheads.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import dev.tr7zw.disguiseheads.DisguiseHeadsShared;
import dev.tr7zw.disguiseheads.util.SkinUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerEntity extends Player {

    AbstractClientPlayerEntity(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(method = "getSkin", at = @At("HEAD"), cancellable = true)
    public void getSkin(CallbackInfoReturnable<PlayerSkin> info) {
        if (!DisguiseHeadsShared.instance.config.enablePlayerDisguise) {
            return;
        }
        ItemStack itemStack = getInventory().armor.get(EquipmentSlot.HEAD.getIndex());
        Item item = itemStack.getItem();
        if (item instanceof BlockItem blockitem && blockitem.getBlock() instanceof AbstractSkullBlock) {
            GameProfile gameProfile = SkinUtil.getGameProfile(itemStack);
            if (gameProfile != null) {
                info.setReturnValue(SkinUtil.getSkin(gameProfile));
            }
        }
    }

}
