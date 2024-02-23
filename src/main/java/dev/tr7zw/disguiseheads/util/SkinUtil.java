package dev.tr7zw.disguiseheads.util;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.UUIDUtil;

public class SkinUtil {

    public static PlayerSkin getSkin(GameProfile gameProfile) {
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

    public static UUID getOrCreatePlayerUUID(GameProfile gameProfile) {
        UUID uUID = gameProfile.getId();
        if (uUID == null) {
            uUID = UUIDUtil.createOfflinePlayerUUID(gameProfile.getName());
        }

        return uUID;
    }

}
