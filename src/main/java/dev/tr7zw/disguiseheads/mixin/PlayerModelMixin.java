package dev.tr7zw.disguiseheads.mixin;

//? if >= 1.21.11 {
import net.minecraft.client.model.player.*;
//? }
import org.spongepowered.asm.mixin.Mixin;

import dev.tr7zw.disguiseheads.PlayerModelAccess;
import net.minecraft.client.model.*;
//? if <= 1.21.1 {

// import org.spongepowered.asm.mixin.Shadow;
// import net.minecraft.client.model.geom.ModelPart;
//? }

@Mixin(PlayerModel.class)
public class PlayerModelMixin implements PlayerModelAccess {

    //? if <= 1.21.1 {

    //    @Shadow
    //    private ModelPart cloak;
    //
    //    @Override
    //    public ModelPart getCapeModel() {
    //        return cloak;
    //    }
    //? }

}
