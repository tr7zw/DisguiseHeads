package dev.tr7zw.disguiseheads;

import net.minecraft.world.item.ItemStack;

public interface LivingEntityExtender {

    ItemStack getHeadItem();

    void setHeadItem(ItemStack item);

}
