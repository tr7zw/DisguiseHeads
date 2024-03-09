package dev.tr7zw.disguiseheads.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.disguiseheads.DisguiseHeadsShared;
import dev.tr7zw.disguiseheads.armorstand.FakePlayerHandler;
import dev.tr7zw.disguiseheads.util.SkinUtil;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;

@SuppressWarnings("rawtypes")
@Mixin(LivingEntityRenderer.class)
public abstract class ArmorStandRenderMixin extends EntityRenderer implements FakePlayerHandler<ArmorStand> {

    protected ArmorStandRenderMixin(Context context) {
        super(context);
    }

    private PlayerModel<ArmorStand> defaultModel;
    private PlayerModel<ArmorStand> thinModel;
    private RenderLayerParent<ArmorStand, EntityModel<ArmorStand>> fakeParent;
    private List<RenderLayer> customLayers = new ArrayList<>();

    @SuppressWarnings("unchecked")
    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(Context context, EntityModel model, float shadowRadius, CallbackInfo info) {
        defaultModel = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false);
        thinModel = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
        fakeParent = new RenderLayerParent<ArmorStand, EntityModel<ArmorStand>>() {

            @Override
            public ResourceLocation getTextureLocation(ArmorStand entity) {
                return getHeadTextureLocation(entity).texture();
            }

            @Override
            public EntityModel<ArmorStand> getModel() {
                return defaultModel;
            }
        };
        customLayers.add(new HumanoidArmorLayer(fakeParent,
                new ArmorStandArmorModel(context.bakeLayer(ModelLayers.ARMOR_STAND_INNER_ARMOR)),
                new ArmorStandArmorModel(context.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR)),
                context.getModelManager()));
        customLayers.add(new ItemInHandLayer(fakeParent, context.getItemInHandRenderer()));
        customLayers.add(new ElytraLayer(fakeParent, context.getModelSet()));
        customLayers.add(new CustomHeadLayer(fakeParent, context.getModelSet(), context.getItemInHandRenderer()));
    }

    @Override
    public PlayerSkin getHeadTextureLocation(ArmorStand entity) {
        ItemStack itemStack = entity.getItemBySlot(EquipmentSlot.HEAD);
        Item item = itemStack.getItem();
        if (item instanceof BlockItem blockitem && blockitem.getBlock() instanceof AbstractSkullBlock) {
            GameProfile gameProfile = null;
            if (itemStack.hasTag()) {
                CompoundTag compoundTag = itemStack.getTag();
                if (compoundTag.contains("SkullOwner", 10)) {
                    gameProfile = NbtUtils.readGameProfile(compoundTag.getCompound("SkullOwner"));
                }
                if (gameProfile != null) {
                    return SkinUtil.getSkin(gameProfile);
                }
            }
        }
        return null;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(LivingEntity livingEntity, float entityYaw, float partialTicks, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, CallbackInfo info) {
        if (livingEntity instanceof ArmorStand armorStand && !armorStand.isMarker() && !armorStand.isInvisible()
                && DisguiseHeadsShared.instance.config.enableArmorstandDisguise) {
            PlayerSkin skin = getHeadTextureLocation(armorStand);
            if (skin != null && getModel() instanceof ArmorStandModel asm) {
                float f = Mth.rotLerp(partialTicks, livingEntity.yBodyRotO, livingEntity.yBodyRot);
                float g = Mth.rotLerp(partialTicks, livingEntity.yHeadRotO, livingEntity.yHeadRot);
                float h = g - f;
                float j = Mth.lerp(partialTicks, livingEntity.xRotO, livingEntity.getXRot());
                asm.setupAnim(armorStand, 0, 0, getBob(livingEntity, partialTicks), h, j);
                renderFakePlayer(armorStand, entityYaw, partialTicks, poseStack, buffer, packedLight, skin, asm,
                        customLayers);
                info.cancel();
            }
        }
    }

    @Override
    public float getAnimationProgressRedirect(ArmorStand entity, float tickDelta) {
        return getBob(entity, tickDelta);
    }

    @Override
    public void setupTransformsRedirect(ArmorStand entity, PoseStack matrices, float animationProgress, float bodyYaw,
            float tickDelta) {
        setupRotations(entity, matrices, animationProgress, bodyYaw, tickDelta);
    }

    @Override
    public void scaleRedirect(ArmorStand entity, PoseStack matrices, float amount) {
        scale(entity, matrices, amount);
    }

    @Override
    public boolean isVisibleRedirect(ArmorStand entity) {
        return isBodyVisible(entity);
    }

    @Override
    public PlayerModel<ArmorStand> getDefaultModel() {
        return defaultModel;
    }

    @Override
    public PlayerModel<ArmorStand> getSlimModel() {
        return thinModel;
    }

    @Shadow
    abstract float getBob(LivingEntity livingBase, float partialTick);

    @Shadow
    abstract void setupRotations(LivingEntity entityLiving, PoseStack poseStack, float ageInTicks, float rotationYaw,
            float partialTicks);

    @Shadow
    abstract void scale(LivingEntity livingEntity, PoseStack poseStack, float partialTickTime);

    @Shadow
    abstract boolean isBodyVisible(LivingEntity livingEntity);

    @Shadow
    abstract EntityModel getModel();

}
