package net.gtaun.shoebill.pickup;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.Streamer;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.PlayerPickupDynamicPickup;
import net.gtaun.shoebill.event.destroyable.DestroyEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.player.PlayerPickupEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Pickup;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Created by marvin on 28.12.14 in project shoebill-streamer.
// Copyright (c) 2014 Marvin Haschker. All rights reserved.
@SuppressWarnings("UnusedDeclaration")
public class DynamicPickupImpl implements DynamicPickup {

    public static EventManager eventManager = Shoebill.get().getResourceManager().getPlugin(Streamer.class).getEventManager();

    private int modelId, type;
    private float streamDistance;
    private int id;
    private EventManagerNode eventManagerNode;
    private Pickup pickup;
    private Location location;
    private List<Player> playersInRange;

    public DynamicPickupImpl(int modelId, int type, Location location, float streamDistance) {
        this.modelId = modelId;
        this.type = type;
        this.streamDistance = streamDistance;
        this.location = location;
        this.id = objectPool.pullId();
        objectPool.addObject(this);
        this.playersInRange = new ArrayList<>();
        this.eventManagerNode = eventManager.createChildNode();
        eventManagerNode.registerHandler(PlayerDisconnectEvent.class, (e) -> {
            if(playersInRange.contains(e.getPlayer())) {
                playersInRange.remove(e.getPlayer());
                if(!isSomebodyInRange())
                    destroyPickup();
            }
        });
        eventManagerNode.registerHandler(PlayerPickupEvent.class, (e) -> {
            if(pickup != null && !pickup.isDestroyed()) {
                if(e.getPickup() == pickup) {
                    PlayerPickupDynamicPickup event = new PlayerPickupDynamicPickup(e.getPlayer(), this);
                    eventManager.dispatchEvent(event, e.getPlayer(), this);
                }
            }
        });
    }

    private boolean isSomebodyInRange() {
        return playersInRange.size() > 0;
    }

    private void createPickup() {
        if(pickup != null) {
            if(!pickup.isDestroyed())
                return;
        }
        pickup = Pickup.create(modelId, type, location);
    }

    private void destroyPickup() {
        if(pickup != null) {
            if(!pickup.isDestroyed()) {
                pickup.destroy();
                pickup = null;
            }
        }
    }

    public DynamicPickupImpl(int modelid, int type, float x, float y, float z, int worldId, int interiorId, float streamDistance) {
        this(modelid, type, new Location(x, y, z, interiorId, worldId), streamDistance);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
        update();
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
        update();
    }

    @Override
    public int getModelId() {
        return modelId;
    }

    @Override
    public void setModelId(int modelId) {
        this.modelId = modelId;
        update();
    }

    @Override
    public float getStreamDistance() {
        return streamDistance;
    }

    @Override
    public void setStreamDistance(float streamDistance) {
        this.streamDistance = streamDistance;
        update();
    }

    @Override
    public void updatePlayer(Player player) {
        float distance = player.getLocation().distance(location);
        if(distance <= streamDistance && !playersInRange.contains(player)) {
            playersInRange.add(player);
            createPickup();
        } else if(distance > streamDistance && playersInRange.contains(player)) {
            playersInRange.remove(player);
            if(!isSomebodyInRange())
                destroyPickup();
        }
    }

    @Override
    public void destroy() {
        destroyPickup();
        objectPool.recycleId(this.id);
        objectPool.removeObject(this);

        DestroyEvent event = new DestroyEvent(this);
        eventManager.dispatchEvent(event, this);

        eventManagerNode.destroy();
        this.id = INVALID_ID;
    }

    @Override
    public boolean isDestroyed() {
        return this.id == INVALID_ID;
    }

    @Override
    public void update() {
        destroyPickup();
        if(isSomebodyInRange())
            createPickup();
    }

    static DynamicPickup get(int id) {
        return objectPool.getObject(id);
    }

    static void destroyAll() {
        List<DynamicPickup> pickupList = new ArrayList<>(objectPool.getAllObjects());
        Collections.copy(pickupList, objectPool.getAllObjects());
        pickupList.stream().filter(obj -> obj != null && !obj.isDestroyed()).forEach(Destroyable::destroy);
        objectPool.clearAllObjects();
        pickupList.clear();
    }

    @Override
    public int getId() {
        return this.id;
    }
}
