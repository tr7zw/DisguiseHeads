package dev.tr7zw.disguiseheads.config;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.disguiseheads.DisguiseHeadsShared;
import net.minecraft.client.gui.screens.Screen;
//spotless:off 
//#if MC >= 11900
import net.minecraft.client.OptionInstance;
//#else
//$$ import net.minecraft.client.Option;
//#endif
//spotless:on

public class ConfigScreenProvider {

    public static Screen createConfigScreen(Screen parent) {
        return new CustomConfigScreen(parent, "text.disguiseheads.title") {

            @Override
            public void initialize() {
                List<Object> options = new ArrayList<>();
                options.add(getOnOffOption("text.disguiseheads.enable.enablePlayerDisguise",
                        () -> DisguiseHeadsShared.instance.config.enablePlayerDisguise,
                        b -> DisguiseHeadsShared.instance.config.enablePlayerDisguise = b));
                options.add(getOnOffOption("text.disguiseheads.enable.enableArmorstandDisguise",
                        () -> DisguiseHeadsShared.instance.config.enableArmorstandDisguise,
                        b -> DisguiseHeadsShared.instance.config.enableArmorstandDisguise = b));

                // spotless:off
                //#if MC >= 11900
                getOptions().addSmall(options.toArray(new OptionInstance[0]));
                //#else
                //$$getOptions().addSmall(options.toArray(new Option[0]));
                //#endif
                // spotless:on

            }

            @Override
            public void save() {
                DisguiseHeadsShared.instance.writeConfig();
            }

            @Override
            public void reset() {
                DisguiseHeadsShared.instance.config = new Config();
            }

        };
    }

}
