package dev.tr7zw.disguiseheads.util;

import java.util.*;

import dev.tr7zw.transition.mc.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.UUIDUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if >= 1.20.2 {

import net.minecraft.client.resources.*;
//? } else {

// import com.mojang.authlib.minecraft.MinecraftProfileTexture;
// import java.util.Map;
//? }
//? if <= 1.20.4 {

// import net.minecraft.Util;
// import org.apache.commons.lang3.StringUtils;
// import net.minecraft.nbt.CompoundTag;
// import net.minecraft.nbt.NbtUtils;
//? } else {

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ResolvableProfile;
//? }
import net.minecraft.world.level.block.AbstractSkullBlock;

public class SkinUtil {

    public static GameProfile getGameProfile(ItemStack itemStack) {
        //? if >= 1.20.5 {

        if (itemStack.getComponents().has(DataComponents.CUSTOM_MODEL_DATA)) {
            return null;
        }
        if (itemStack.getComponents().has(DataComponents.PROFILE)) {
            ResolvableProfile resolvableProfile = (ResolvableProfile) itemStack.get(DataComponents.PROFILE);
            return PlayerUtil.getProfile(resolvableProfile);
        }
        return null;
        //? } else {

        // if (itemStack.hasTag()) {
        //     CompoundTag compoundTag = itemStack.getTag();
        //     if (compoundTag.contains("CustomModelData")) {
        //         return null; // do not try to 3d-fy custom head models
        //     }
        //     if (compoundTag.contains("SkullOwner", 10)) {
        //         return NbtUtils.readGameProfile(compoundTag.getCompound("SkullOwner"));
        //     } else if (compoundTag.contains("SkullOwner", 8)
        //             && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
        //         return new GameProfile(Util.NIL_UUID, compoundTag.getString("SkullOwner"));
        //     }
        // }
        // return null;
        //? }
    }

    public static PlayerSkin getSkin(GameProfile gameProfile) {
        Minecraft minecraftClient = Minecraft.getInstance();
        //? if >= 1.21.9 {

        if (gameProfile.properties() == null) {
            return DefaultPlayerSkin.get(getOrCreatePlayerUUID(gameProfile));
        }
        PlayerSkin skin = minecraftClient.getSkinManager().get(gameProfile).getNow(Optional.empty())
                .orElseGet(() -> null);
        //? } else {
        /*
        if (gameProfile.getProperties() == null) {
            return DefaultPlayerSkin.get(getOrCreatePlayerUUID(gameProfile));
        }
        PlayerSkin skin = minecraftClient.getSkinManager().getInsecureSkin(gameProfile);
        *///? }
        if (skin != null) {
            return skin;
        }
        return DefaultPlayerSkin.get(getOrCreatePlayerUUID(gameProfile));
    }

    public static UUID getOrCreatePlayerUUID(GameProfile gameProfile) {
        //? if >= 1.21.9 {

        UUID uUID = gameProfile.id();
        if (uUID == null) {
            uUID = UUIDUtil.createOfflinePlayerUUID(gameProfile.name());
        }
        //? } else {
        /*
        UUID uUID = gameProfile.getId();
        if (uUID == null) {
            uUID = UUIDUtil.createOfflinePlayerUUID(gameProfile.getName());
        }
         *///? }
        return uUID;
    }

    public static PlayerSkin getHeadTextureLocation(LivingEntity entity) {
        ItemStack itemStack = entity.getItemBySlot(EquipmentSlot.HEAD);
        Item item = itemStack.getItem();
        if (item instanceof BlockItem blockitem && blockitem.getBlock() instanceof AbstractSkullBlock) {
            GameProfile gameProfile = SkinUtil.getGameProfile(itemStack);
            if (gameProfile != null) {
                return SkinUtil.getSkin(gameProfile);
            }
        }
        return null;
    }

    public static PlayerSkin getHeadTextureLocation(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item instanceof BlockItem blockitem && blockitem.getBlock() instanceof AbstractSkullBlock) {
            GameProfile gameProfile = SkinUtil.getGameProfile(itemStack);
            if (gameProfile != null) {
                return SkinUtil.getSkin(gameProfile);
            }
        }
        return null;
    }

}
