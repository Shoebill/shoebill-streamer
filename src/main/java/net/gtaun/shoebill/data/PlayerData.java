package net.gtaun.shoebill.data;

import net.gtaun.shoebill.Streamer;
import net.gtaun.shoebill.common.player.PlayerLifecycleObject;
import net.gtaun.shoebill.event.player.PlayerUpdateEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

// Created by marvin on 30.12.14 in project shoebill-streamer.
// Copyright (c) 2014 Marvin Haschker. All rights reserved.
public class PlayerData extends PlayerLifecycleObject {

    private EventManagerNode eventManagerNode;
    private long lastTick;
    private static Streamer streamer = Streamer.getInstance();

    public PlayerData(EventManager eventManager, Player player) {
        super(eventManager, player);
        this.eventManagerNode = eventManager.createChildNode();
    }

    @Override
    protected void onInit() {
        lastTick = System.currentTimeMillis();
        streamer.updatePlayer(player);
        eventManagerNode.registerHandler(PlayerUpdateEvent.class, (e) -> {
            if(System.currentTimeMillis() - lastTick >= 100) {
                streamer.updatePlayer(player);
                lastTick = System.currentTimeMillis();
            }
        });
    }

    @Override
    protected void onDestroy() {
        eventManagerNode.destroy();
        eventManagerNode = null;
    }
}
