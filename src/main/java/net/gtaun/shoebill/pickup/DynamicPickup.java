package net.gtaun.shoebill.pickup;

import net.gtaun.shoebill.DynamicObjectPool;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Updateable;
import net.gtaun.shoebill.object.Destroyable;

// Created by marvin on 28.12.14 in project shoebill-streamer.
// Copyright (c) 2014 Marvin Haschker. All rights reserved.
@SuppressWarnings("UnusedDeclaration")
public interface DynamicPickup extends Destroyable, Updateable {
    public static int INVALID_ID = -1;
    static DynamicObjectPool<DynamicPickup> objectPool = new DynamicObjectPool<>();

    /**
     * Creates a dynamic samp pickup.
     * @param modelid The modelid of the pickup (see: http://wiki.sa-mp.com/wiki/Pickup_IDs)
     * @param type The type of the pickup (see: http://wiki.sa-mp.com/wiki/PickupTypes)
     * @param location The location where the pickup should be (x, y, z, worldid, interiorid)
     * @param streamDistance The distance when the pickup should be visible (viewDistance)
     * @return The created pickup
     */
    public static DynamicPickup create(int modelid, int type, Location location, float streamDistance) {
        return new DynamicPickupImpl(modelid, type, location, streamDistance);
    }

    /**
     * Creates a dynamic samp pickup.
     * @param modelid The modelid of the pickup (see: http://wiki.sa-mp.com/wiki/Pickup_IDs)
     * @param type The type of the pickup (see: http://wiki.sa-mp.com/wiki/PickupTypes)
     * @param x X-Cord
     * @param y Y-Cord
     * @param z Z-Cord
     * @param worldId The world id where the pickup should be
     * @param interiorId The interior where the pickup should be
     * @param streamDistance The distance when the pickup should be visible (viewDistance)
     * @return The created pickup
     */
    public static DynamicPickup create(int modelid, int type, float x, float y, float z, int worldId, int interiorId, float streamDistance) {
        return create(modelid, type, new Location(x, y, z, interiorId, worldId), streamDistance);
    }

    public static DynamicPickup get(int id) {
        return DynamicPickupImpl.get(id);
    }

    public static void destroyAll() {
        DynamicPickupImpl.destroyAll();
    }

    Location getLocation();
    void setLocation(Location location);

    int getType();
    void setType(int type);

    int getModelId();
    void setModelId(int modelId);

    float getStreamDistance();
    void setStreamDistance(float streamDistance);
}
