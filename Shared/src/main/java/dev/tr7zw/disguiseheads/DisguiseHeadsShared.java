package dev.tr7zw.disguiseheads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DisguiseHeadsShared {

    public static final Logger LOGGER = LogManager.getLogger("DisguiseHeads");
    public static DisguiseHeadsShared instance;

    public void init() {
        instance = this;
        LOGGER.info("Loading DisguiseHeads!");
    }

}
