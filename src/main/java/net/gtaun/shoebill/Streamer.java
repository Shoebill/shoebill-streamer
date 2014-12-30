package net.gtaun.shoebill;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder;
import net.gtaun.shoebill.data.PlayerData;
import net.gtaun.shoebill.label.DynamicLabel;
import net.gtaun.shoebill.object.DynamicSampObject;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.pickup.DynamicPickup;
import net.gtaun.shoebill.resource.Plugin;
import org.slf4j.Logger;

// Created by marvin on 27.12.14 in project shoebill-streamer.
// Copyright (c) 2014 Marvin Haschker. All rights reserved.
public class Streamer extends Plugin {

    private static Streamer instance;

    public static Streamer getInstance() {
        return instance;
    }

    private Logger logger;
    private PlayerLifecycleHolder playerLifecycleHolder;

    @Override
    protected void onEnable() throws Throwable {
        instance = this;
        this.logger = getLogger();
        this.playerLifecycleHolder = new PlayerLifecycleHolder(getEventManager());
        this.playerLifecycleHolder.registerClass(PlayerData.class);
        logger.info("Streamer is ready!");
    }

    @Override
    protected void onDisable() throws Throwable {
        logger.debug("Streamer shutting down! Everything will be destroyed.");
        DynamicSampObject.destroyAll();
        DynamicLabel.destroyAll();
        DynamicPickup.destroyAll();
        playerLifecycleHolder.destroy();
    }

    public void updatePlayer(Player player) {
        DynamicSampObject.objectPool.update(player);
        DynamicLabel.objectPool.update(player);
        DynamicPickup.objectPool.update(player);
    }
}
