package dev.tr7zw.disguiseheads.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.disguiseheads.DisguiseHeadsShared;
import dev.tr7zw.disguiseheads.armorstand.ArmorStandElytraLayer;
import dev.tr7zw.disguiseheads.armorstand.ArmorstandCapeLayer;
import dev.tr7zw.disguiseheads.armorstand.FakePlayerHandler;
import dev.tr7zw.disguiseheads.util.SkinUtil;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@SuppressWarnings("rawtypes")
@Mixin(LivingEntityRenderer.class)
public abstract class ArmorStandRenderMixin<T extends LivingEntity, V extends EntityModel<T>> extends EntityRenderer
        implements FakePlayerHandler<T, V> {

    protected ArmorStandRenderMixin(Context context) {
        super(context);
    }

    private PlayerModel<T> defaultModel;
    private PlayerModel<T> thinModel;
    private RenderLayerParent<T, EntityModel<T>> fakeParent;
    private RenderLayerParent<T, EntityModel<T>> fakeParentSlim;
    private List<RenderLayer> customLayers = new ArrayList<>();
    private List<RenderLayer> customLayersSlim = new ArrayList<>();

    @SuppressWarnings("unchecked")
    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(Context context, EntityModel model, float shadowRadius, CallbackInfo info) {
        defaultModel = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false);
        thinModel = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
        fakeParent = new RenderLayerParent<T, EntityModel<T>>() {

            @Override
            public ResourceLocation getTextureLocation(T entity) {
                return SkinUtil.getHeadTextureLocation(entity).texture();
            }

            @Override
            public EntityModel<T> getModel() {
                return defaultModel;
            }
        };
        fakeParentSlim = new RenderLayerParent<T, EntityModel<T>>() {

            @Override
            public ResourceLocation getTextureLocation(T entity) {
                return SkinUtil.getHeadTextureLocation(entity).texture();
            }

            @Override
            public EntityModel<T> getModel() {
                return thinModel;
            }
        };
        customLayers.add(new HumanoidArmorLayer(fakeParent,
                new ArmorStandArmorModel(context.bakeLayer(ModelLayers.ARMOR_STAND_INNER_ARMOR)),
                new ArmorStandArmorModel(context.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR)),
                context.getModelManager()));
        customLayers.add(new ItemInHandLayer(fakeParent, context.getItemInHandRenderer()));
        customLayers.add(new ArmorStandElytraLayer(fakeParent, context.getModelSet()));
        customLayers.add(new ArmorstandCapeLayer(fakeParent));
        customLayers.add(new CustomHeadLayer(fakeParent, context.getModelSet(), context.getItemInHandRenderer()));
        // duplicate for slim
        customLayersSlim.add(new HumanoidArmorLayer(fakeParentSlim,
                new ArmorStandArmorModel(context.bakeLayer(ModelLayers.ARMOR_STAND_INNER_ARMOR)),
                new ArmorStandArmorModel(context.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR)),
                context.getModelManager()));
        customLayersSlim.add(new ItemInHandLayer(fakeParentSlim, context.getItemInHandRenderer()));
        customLayersSlim.add(new ArmorStandElytraLayer(fakeParentSlim, context.getModelSet()));
        customLayersSlim.add(new ArmorstandCapeLayer(fakeParentSlim));
        customLayersSlim
                .add(new CustomHeadLayer(fakeParentSlim, context.getModelSet(), context.getItemInHandRenderer()));
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(LivingEntity livingEntity, float entityYaw, float partialTicks, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, CallbackInfo info) {
        if (!(livingEntity instanceof Player) && !livingEntity.isInvisible()
                && DisguiseHeadsShared.instance.config.enableArmorstandDisguise) {
            PlayerSkin skin = SkinUtil.getHeadTextureLocation(livingEntity);
            if (skin != null) {
                V asm = (V) getModel();
                float f = Mth.rotLerp(partialTicks, livingEntity.yBodyRotO, livingEntity.yBodyRot);
                float g = Mth.rotLerp(partialTicks, livingEntity.yHeadRotO, livingEntity.yHeadRot);
                float h = g - f;
                float j = Mth.lerp(partialTicks, livingEntity.xRotO, livingEntity.getXRot());
                float limbSwing = 0.0F;
                float limbSwingAmount = 0.0F;
                if (!livingEntity.isPassenger() && livingEntity.isAlive()) {
                    limbSwing = livingEntity.walkAnimation.speed(partialTicks);
                    limbSwingAmount = livingEntity.walkAnimation.position(partialTicks);
                    if (livingEntity.isBaby()) {
                        limbSwingAmount *= 3.0F;
                    }

                    if (limbSwing > 1.0F) {
                        limbSwing = 1.0F;
                    }
                }
                asm.prepareMobModel((T) livingEntity, limbSwingAmount, limbSwing, partialTicks);
                asm.setupAnim((T) livingEntity, limbSwingAmount, limbSwing, getBob(livingEntity, partialTicks), h, j);
                renderFakePlayer((T) livingEntity, entityYaw, partialTicks, poseStack, buffer, packedLight, skin, asm,
                        skin.model() == Model.WIDE ? customLayers : customLayersSlim);
                info.cancel();
            }
        }
    }

    @Override
    public float getAnimationProgressRedirect(T entity, float tickDelta) {
        return getBob(entity, tickDelta);
    }

    @Override
    public void setupTransformsRedirect(T entity, PoseStack matrices, float animationProgress, float bodyYaw,
            float tickDelta) {
        //#if MC <= 12004
        //$$ setupRotations(entity, matrices, animationProgress, bodyYaw, tickDelta);
        //#else
        setupRotations(entity, matrices, animationProgress, bodyYaw, tickDelta, entity.getScale());
        //#endif
    }

    @Override
    public void scaleRedirect(T entity, PoseStack matrices, float amount) {
        scale(entity, matrices, amount);
    }

    @Override
    public boolean isVisibleRedirect(T entity) {
        return isBodyVisible(entity);
    }

    @Override
    public PlayerModel<T> getDefaultModel() {
        return defaultModel;
    }

    @Override
    public PlayerModel<T> getSlimModel() {
        return thinModel;
    }

    @Shadow
    abstract float getBob(LivingEntity livingBase, float partialTick);

    //#if MC <= 12004
    //$$  @Shadow
    //$$  abstract void setupRotations(LivingEntity entityLiving, PoseStack poseStack, float ageInTicks, float rotationYaw,
    //$$          float partialTicks);
    //#else
    @Shadow
    abstract void setupRotations(LivingEntity livingEntity, PoseStack poseStack, float f, float g, float h, float i);
    //#endif

    @Shadow
    abstract void scale(LivingEntity livingEntity, PoseStack poseStack, float partialTickTime);

    @Shadow
    abstract boolean isBodyVisible(LivingEntity livingEntity);

    @Shadow
    abstract EntityModel getModel();

}
