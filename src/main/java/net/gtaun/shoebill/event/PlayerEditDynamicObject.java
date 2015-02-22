package net.gtaun.shoebill.event;

import net.gtaun.shoebill.constant.ObjectEditResponse;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.DynamicSampObject;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.Interruptable;

/**
 * Created by marvin on 07.02.15 in project shoebill-streamer.
 * Copyright (c) 2015 Marvin Haschker. All rights reserved.
 */
public class PlayerEditDynamicObject extends PlayerEvent implements Interruptable {

    private DynamicSampObject object;
    private ObjectEditResponse editResponse;
    private boolean save;
    private Location newLocation;
    private Vector3D newRotation;

    public PlayerEditDynamicObject(Player player, DynamicSampObject object, ObjectEditResponse response, Location newLocation, Vector3D newRotation) {
        super(player);
        this.object = object;
        this.editResponse = response;
        this.newLocation = newLocation;
        this.newRotation = newRotation;
    }

    public Vector3D getNewRotation() {
        return newRotation;
    }

    public Location getNewLocation() {
        return newLocation;
    }

    public void interrupt() {
        super.interrupt();
    }

    public DynamicSampObject getObject() {
        return this.object;
    }

    public ObjectEditResponse getEditResponse() {
        return this.editResponse;
    }

    public void savePosition() {
        this.save = true;
    }

    public boolean isSave() {
        return save;
    }
}
