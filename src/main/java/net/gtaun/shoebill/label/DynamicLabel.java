package net.gtaun.shoebill.label;

import net.gtaun.shoebill.DynamicObjectPool;
import net.gtaun.shoebill.Streamer;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Updateable;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

// Created by marvin on 28.12.14 in project shoebill-streamer.
// Copyright (c) 2014 Marvin Haschker. All rights reserved.
@SuppressWarnings("UnusedDeclaration")
public interface DynamicLabel extends Destroyable, Updateable {

    int INVALID_ID = -1;
    DynamicObjectPool<DynamicLabel> objectPool = new DynamicObjectPool<>();

    /**
     * Creates a dynamic label
     * @param text The visible text
     * @param color The color of the text
     * @param x X-cord
     * @param y Y-cord
     * @param z Z-cord
     * @param drawDistance The drawdistance (viewdistance)
     * @param attachedPlayer The attached player (null if none)
     * @param attachedVehicle The attached vehicle (null if none)
     * @param testLOS If the Label should be seen through walls
     * @param worldId The worldid where the label should be visible
     * @param interiorId The interior where the label should be visible
     * @param streamDistance The distance when the label should be created
     * @return The created Dynamic label
     */
     static DynamicLabel create(@Nonnull String text, @Nonnull Color color, float x, float y, float z, float drawDistance, @Nullable Player attachedPlayer,
                                @Nullable Vehicle attachedVehicle, boolean testLOS, int worldId, int interiorId, float streamDistance) {
        return create(text, color, new Location(x, y, z, interiorId, worldId), drawDistance, attachedPlayer, attachedVehicle, testLOS, streamDistance);
     }

    /**
     * Creates a dynamic label
     * @param text The visible text
     * @param color The color of the text
     * @param location The location where the label should be. (interior, world, x, y, z)
     * @param drawDistance The drawdistance (viewdistance)
     * @param attachedPlayer The attached player (null if none)
     * @param attachedVehicle The attached vehicle (null if none)
     * @param testLOS If the Label should be seen through walls
     * @return The created Dynamic label
     */
    static DynamicLabel create(@Nonnull String text, @Nonnull Color color, @Nonnull Location location, float drawDistance, @Nullable Player attachedPlayer,
                               @Nullable Vehicle attachedVehicle, boolean testLOS, float streamdistance) {
        if(!Streamer.isInitialized) {
            System.err.println("Please insert the Streamer into your plugins section in resources.yml");
            return null;
        } else {
            return new DynamicLabelImpl(text, color, attachedPlayer, attachedVehicle, location, drawDistance, streamdistance, testLOS);
        }
    }

    static DynamicLabel get(int id) {
        return DynamicLabelImpl.get(id);
    }

    static Collection<DynamicLabel> get() { return new ArrayList<>(objectPool.getAllObjects()); }

    static void destroyAll() {
        DynamicLabelImpl.destroyAll();
    }

    int getId();

    String getText();
    void setText(String text);

    Color getColor();
    void setColor(Color color);

    float getDrawDistance();
    void setDrawDistance(float drawDistance);

    float getStreamDistance();
    void setStreamDistance(float streamDistance);

    Location getLocation();
    void setLocation(Location location);

    Player getAttachedPlayer();
    Vehicle getAttachedVehicle();

    boolean isTestLOS();
    void setTestLOS(boolean testLOS);

}
