package dev.tr7zw.disguiseheads.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerEntity extends Player {

    public AbstractClientPlayerEntity(Level level, BlockPos blockPos, float f, GameProfile gameProfile,
            ProfilePublicKey profilePublicKey) {
        super(level, blockPos, f, gameProfile, profilePublicKey);
    }

    private ResourceLocation lastId = null;
    private String modelOverwrite = null;

    @Inject(method = "getSkinTextureLocation", at = @At("HEAD"), cancellable = true)
    public ResourceLocation getSkinTexture(CallbackInfoReturnable<ResourceLocation> info) {
        ItemStack itemStack = getItemBySlot(EquipmentSlot.HEAD);
        Item item = itemStack.getItem();
        if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof AbstractSkullBlock) {
            GameProfile gameProfile = null;
            if (itemStack.hasTag()) {
                String string;
                CompoundTag compoundTag = itemStack.getTag();
                if (compoundTag.contains("SkullOwner", 10)) {
                    gameProfile = NbtUtils.readGameProfile((CompoundTag) compoundTag.getCompound("SkullOwner"));
                }
                ResourceLocation id = getSkin(gameProfile);
                if(!id.equals(lastId)) {
                    lastId = id;
                }
                info.setReturnValue(id);
                return id;
            }
        }
        if(lastId != null) {
            lastId = null;
            modelOverwrite = null;
        }
        return null;
    }
    
    @Inject(method = "getModelName", at = @At("HEAD"), cancellable = true)
    public String getModel(CallbackInfoReturnable<String> info) {
        if(modelOverwrite != null) {
            info.setReturnValue(modelOverwrite);
        }
        return null;
    }

    private ResourceLocation getSkin(GameProfile gameProfile) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Map map = minecraftClient.getSkinManager().getInsecureSkinInformation(gameProfile);
        if (map.containsKey((Object) MinecraftProfileTexture.Type.SKIN)) {
            this.modelOverwrite = ((MinecraftProfileTexture) map.get((Object) MinecraftProfileTexture.Type.SKIN)).getMetadata("model");
            if (this.modelOverwrite == null) {
                this.modelOverwrite = "default";
            }
            return minecraftClient.getSkinManager().registerTexture(
                    (MinecraftProfileTexture) map.get((Object) MinecraftProfileTexture.Type.SKIN),
                    MinecraftProfileTexture.Type.SKIN);
        }
        return DefaultPlayerSkin
                .getDefaultSkin(UUIDUtil.getOrCreatePlayerUUID(gameProfile));
    }
    
}
