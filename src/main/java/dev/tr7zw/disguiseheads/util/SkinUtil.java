package dev.tr7zw.disguiseheads.util;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.UUIDUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//spotless:off 
//#if MC >= 12002
import net.minecraft.client.resources.PlayerSkin;
//#else
//$$ import com.mojang.authlib.minecraft.MinecraftProfileTexture;
//$$ import java.util.Map;
//#endif
//#if MC <= 12004
//$$ import net.minecraft.Util;
//$$ import org.apache.commons.lang3.StringUtils;
//$$ import net.minecraft.nbt.CompoundTag;
//$$ import net.minecraft.nbt.NbtUtils;
//#else
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ResolvableProfile;
//#endif
//spotless:on
import net.minecraft.world.level.block.AbstractSkullBlock;

public class SkinUtil {

    public static GameProfile getGameProfile(ItemStack itemStack) {
        // spotless:off 
        //#if MC >= 12005
        if(itemStack.getComponents().has(DataComponents.CUSTOM_MODEL_DATA)) {
            return null;
        }
        if (itemStack.getComponents().has(DataComponents.PROFILE)) {
            ResolvableProfile resolvableProfile = (ResolvableProfile) itemStack.get(DataComponents.PROFILE);
            if (resolvableProfile != null && !resolvableProfile.isResolved()) {
                    itemStack.remove(DataComponents.PROFILE);
                    resolvableProfile.resolve().thenAcceptAsync(
                                    resolvableProfile2 -> itemStack.set(DataComponents.PROFILE, resolvableProfile2),
                                    Minecraft.getInstance());
                    resolvableProfile = null;
            }
            if(resolvableProfile != null) {
                return resolvableProfile.gameProfile();
            }
        }
        return null;
        //#else
        //$$ if (itemStack.hasTag()) {
        //$$     CompoundTag compoundTag = itemStack.getTag();
        //$$     if (compoundTag.contains("CustomModelData")) {
        //$$         return null; // do not try to 3d-fy custom head models
        //$$     }
        //$$     if (compoundTag.contains("SkullOwner", 10)) {
        //$$         return NbtUtils.readGameProfile(compoundTag.getCompound("SkullOwner"));
        //$$     } else if (compoundTag.contains("SkullOwner", 8)
        //$$             && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
        //$$         return new GameProfile(Util.NIL_UUID, compoundTag.getString("SkullOwner"));
        //$$     }
        //$$ }
        //$$ return null;
        //#endif
        //spotless:on
    }

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
