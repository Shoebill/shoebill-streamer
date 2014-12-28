package net.gtaun.shoebill;
import net.gtaun.shoebill.resource.Plugin;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;

// Created by marvin on 27.12.14 in project shoebill-streamer.
// Copyright (c) 2014 Marvin Haschker. All rights reserved.
public class Streamer extends Plugin {

    private Logger logger;
    @Override
    protected void onEnable() throws Throwable {
        this.logger = getLogger();
        DynamicSampObjectImpl.availableIds = new LinkedList<>();
        DynamicSampObjectImpl.dynamicSampObjects = new ArrayList<>();
        DynamicSampObjectImpl.eventManager = getEventManager();
        logger.info("Streamer is ready!");
    }

    @Override
    protected void onDisable() throws Throwable {
        logger.debug("Streamer shutting down! All Objects will be destroyed.");
        DynamicSampObject.destroyAll();
    }
}
