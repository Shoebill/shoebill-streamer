package net.gtaun.shoebill.event; // Created by marvin on 28.12.14 in project shoebill-streamer.

import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.pickup.DynamicPickup;

// Copyright (c) 2014 Marvin Haschker. All rights reserved.
public class PlayerPickupDynamicPickup extends PlayerEvent {

    private DynamicPickup pickup;

    public PlayerPickupDynamicPickup(Player player, DynamicPickup pickup) {
        super(player);
        this.pickup = pickup;
    }

    public DynamicPickup getPickup() {
        return pickup;
    }
}
