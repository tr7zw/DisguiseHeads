//#if FABRIC
package dev.tr7zw.disguiseheads;

import net.fabricmc.api.ClientModInitializer;

public class DisguiseHeadsMod extends DisguiseHeadsShared implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        init();
    }
}
//#endif