package dev.tr7zw.disguiseheads.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.tr7zw.disguiseheads.PlayerModelAccess;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;

@Mixin(PlayerModel.class)
public class PlayerModelMixin implements PlayerModelAccess {

    @Shadow
    private ModelPart cloak;

    @Override
    public ModelPart getCapeModel() {
        return cloak;
    }

}
