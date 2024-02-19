package dev.tr7zw.disguiseheads.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerEntity extends Player {

    public AbstractClientPlayerEntity(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    private PlayerSkin skinOverwrite = null;

    @Inject(method = "getSkin", at = @At("HEAD"), cancellable = true)
    public void getSkin(CallbackInfoReturnable<PlayerSkin> info) {
        ItemStack itemStack = getItemBySlot(EquipmentSlot.HEAD);
        Item item = itemStack.getItem();
        if (item instanceof BlockItem blockitem && blockitem.getBlock() instanceof AbstractSkullBlock) {
            GameProfile gameProfile = null;
            if (itemStack.hasTag()) {
                CompoundTag compoundTag = itemStack.getTag();
                if (compoundTag.contains("SkullOwner", 10)) {
                    gameProfile = NbtUtils.readGameProfile(compoundTag.getCompound("SkullOwner"));
                }
                if (gameProfile != null) {
                    skinOverwrite = getSkin(gameProfile);
                    info.setReturnValue(skinOverwrite);
                }
            }
        }
    }

    private PlayerSkin getSkin(GameProfile gameProfile) {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (gameProfile.getProperties() == null) {
            return DefaultPlayerSkin.get(getOrCreatePlayerUUID(gameProfile));
        }
        PlayerSkin skin = minecraftClient.getSkinManager().getInsecureSkin(gameProfile);
        if (skin != null) {
            return skin;
        }
        return DefaultPlayerSkin.get(getOrCreatePlayerUUID(gameProfile));
    }

    private static UUID getOrCreatePlayerUUID(GameProfile gameProfile) {
        UUID uUID = gameProfile.getId();
        if (uUID == null) {
            uUID = UUIDUtil.createOfflinePlayerUUID(gameProfile.getName());
        }

        return uUID;
    }

}
