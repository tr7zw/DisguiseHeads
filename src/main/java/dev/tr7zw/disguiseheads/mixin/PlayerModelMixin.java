package dev.tr7zw.disguiseheads.mixin;

import org.spongepowered.asm.mixin.Mixin;

import dev.tr7zw.disguiseheads.PlayerModelAccess;
import net.minecraft.client.model.PlayerModel;
//#if MC <= 12101
//$$import org.spongepowered.asm.mixin.Shadow;
//$$import net.minecraft.client.model.geom.ModelPart;
//#endif

@Mixin(PlayerModel.class)
public class PlayerModelMixin implements PlayerModelAccess {

    //#if MC <= 12101
    //$$    @Shadow
    //$$    private ModelPart cloak;
    //$$
    //$$    @Override
    //$$    public ModelPart getCapeModel() {
    //$$        return cloak;
    //$$    }
    //#endif

}
