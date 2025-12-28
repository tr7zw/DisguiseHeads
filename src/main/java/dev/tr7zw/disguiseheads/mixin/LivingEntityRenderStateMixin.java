package dev.tr7zw.disguiseheads.mixin;

import org.spongepowered.asm.mixin.Mixin;

import dev.tr7zw.disguiseheads.LivingEntityExtender;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.item.ItemStack;

//? if >= 1.21.3 {

@Mixin(net.minecraft.client.renderer.entity.state.LivingEntityRenderState.class)
//? } else {

// @Mixin(ItemStack.class)
//? }
public class LivingEntityRenderStateMixin implements LivingEntityExtender {

    @Getter
    @Setter
    private ItemStack headItem = null;

}
