package dev.tr7zw.disguiseheads.config;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.disguiseheads.DisguiseHeadsShared;
import dev.tr7zw.transition.mc.ComponentProvider;
import dev.tr7zw.trender.gui.client.AbstractConfigScreen;
import dev.tr7zw.trender.gui.client.BackgroundPainter;
import dev.tr7zw.trender.gui.widget.WButton;
import dev.tr7zw.trender.gui.widget.WGridPanel;
import dev.tr7zw.trender.gui.widget.WPlayerPreview;
import dev.tr7zw.trender.gui.widget.WTabPanel;
import dev.tr7zw.trender.gui.widget.data.Insets;
import dev.tr7zw.trender.gui.widget.icon.ItemIcon;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.Items;
import net.minecraft.client.Minecraft;


public class ConfigScreenProvider {

    public static Screen createConfigScreen(Screen parent) {
        return new CustomConfigScreen(parent).createScreen();
    }

    private static class CustomConfigScreen extends AbstractConfigScreen {

        public CustomConfigScreen(Screen previous) {
            super(ComponentProvider.translatable("text.disguiseheads.title"), previous);

            WGridPanel root = new WGridPanel(8);
            root.setInsets(Insets.ROOT_PANEL);
            setRootPanel(root);

            WTabPanel wTabPanel = new WTabPanel();

            WGridPanel playerSettings = new WGridPanel();
            playerSettings.setInsets(new Insets(2, 4));

            // Player Settings
            List<OptionInstance> options = new ArrayList<>();
            options.add(getOnOffOption("text.disguiseheads.enable.enablePlayerDisguise",
                    () -> DisguiseHeadsShared.instance.config.enablePlayerDisguise,
                    b -> DisguiseHeadsShared.instance.config.enablePlayerDisguise = b));
            options.add(getOnOffOption("text.disguiseheads.enable.changeNameToDisguise",
                    () -> DisguiseHeadsShared.instance.config.changeNameToDisguise,
                    b -> DisguiseHeadsShared.instance.config.changeNameToDisguise = b));

            var optionList = createOptionList(options);
            optionList.setGap(-1);
            //            optionList.setSize(14 * 20, 9 * 20);

            playerSettings.add(optionList, 0, 0, 12, 9);

            var playerPreview = new WPlayerPreview();
            playerPreview.setShowBackground(true);
            playerSettings.add(playerPreview, 13, 2);

            wTabPanel.add(playerSettings, b -> b.title(ComponentProvider.translatable("text.disguiseheads.tab.player"))
                    .icon(new ItemIcon(Items.PLAYER_HEAD)));

            // Armorstand and Mob Settings
            options = new ArrayList<>();
            options.add(getOnOffOption("text.disguiseheads.enable.enableArmorstandDisguise",
                    () -> DisguiseHeadsShared.instance.config.enableArmorstandDisguise,
                    b -> DisguiseHeadsShared.instance.config.enableArmorstandDisguise = b));
            options.add(getOnOffOption("text.disguiseheads.enable.hideArmorstandHead",
                    () -> DisguiseHeadsShared.instance.config.hideArmorstandHead,
                    b -> DisguiseHeadsShared.instance.config.hideArmorstandHead = b));
            options.add(getOnOffOption("text.disguiseheads.enable.enableArmorstandCapes",
                    () -> DisguiseHeadsShared.instance.config.enableArmorstandCapes,
                    b -> DisguiseHeadsShared.instance.config.enableArmorstandCapes = b));
            options.add(getOnOffOption("text.disguiseheads.enable.enableEverythingDisguise",
                    () -> DisguiseHeadsShared.instance.config.enableEverythingDisguise,
                    b -> DisguiseHeadsShared.instance.config.enableEverythingDisguise = b));
            optionList = createOptionList(options);
            optionList.setGap(-1);

            wTabPanel.add(optionList, b -> b.title(ComponentProvider.translatable("text.disguiseheads.tab.mobs"))
                    .icon(new ItemIcon(Items.ARMOR_STAND)));

            WButton doneButton = new WButton(CommonComponents.GUI_DONE);
            doneButton.setOnClick(() -> {
                save();
                Minecraft.getInstance().setScreen(previous);
            });
            root.add(doneButton, 0, 26, 6, 2);

            wTabPanel.layout();
            root.add(wTabPanel, 0, 1);

            WButton resetButton = new WButton(ComponentProvider.translatable("controls.reset"));
            resetButton.setOnClick(() -> {
                reset();
                root.layout();
            });
            root.add(resetButton, 23, 26, 6, 2);

            root.setBackgroundPainter(BackgroundPainter.VANILLA);

            root.validate(this);
            root.setHost(this);
        }

        @Override
        public void reset() {
            DisguiseHeadsShared.instance.config = new Config();
        }

        @Override
        public void save() {
            DisguiseHeadsShared.instance.writeConfig();
        }

    }
}
