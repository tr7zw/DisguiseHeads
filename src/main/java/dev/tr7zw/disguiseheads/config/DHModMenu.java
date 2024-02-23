//#if FABRIC
package dev.tr7zw.disguiseheads.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class DHModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            return ConfigScreenProvider.createConfigScreen(parent);
        };
    }

}
//#endif