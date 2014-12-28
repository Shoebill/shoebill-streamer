package net.gtaun.shoebill;
import net.gtaun.shoebill.label.DynamicLabel;
import net.gtaun.shoebill.object.DynamicSampObject;
import net.gtaun.shoebill.pickup.DynamicPickup;
import net.gtaun.shoebill.resource.Plugin;
import org.slf4j.Logger;

// Created by marvin on 27.12.14 in project shoebill-streamer.
// Copyright (c) 2014 Marvin Haschker. All rights reserved.
public class Streamer extends Plugin {

    private Logger logger;
    @Override
    protected void onEnable() throws Throwable {
        this.logger = getLogger();
        logger.info("Streamer is ready!");
    }

    @Override
    protected void onDisable() throws Throwable {
        logger.debug("Streamer shutting down! All Objects will be destroyed.");
        DynamicSampObject.destroyAll();
        DynamicLabel.destroyAll();
        DynamicPickup.destroyAll();
    }
}
