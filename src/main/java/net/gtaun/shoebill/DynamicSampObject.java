package net.gtaun.shoebill;
import net.gtaun.shoebill.constant.ObjectMaterialSize;
import net.gtaun.shoebill.constant.ObjectMaterialTextAlign;
import net.gtaun.shoebill.data.*;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;

import java.util.Collection;

// Created by marvin on 27.12.14 in project shoebill-streamer.
// Copyright (c) 2014 Marvin Haschker. All rights reserved.
public interface DynamicSampObject extends Destroyable {

    public static int INVALID_ID = -1;

    public static DynamicSampObject create(int modelId, float x, float y, float z, float rX, float rY, float rZ,
                                                int worldId, int interiorId, float streamDistance, float drawDistance) {
        return new DynamicSampObjectImpl(modelId, x, y, z, rX, rY, rZ, (worldId == -1) ? 0 : worldId, (interiorId == -1) ? 0 : interiorId,
                (streamDistance == 0.0) ? 200f : streamDistance, drawDistance);
    }

    public static DynamicSampObject create(int modelId, Location location, Vector3D rotation, float streamDistance, float drawDistance) {
        return new DynamicSampObjectImpl(modelId, location.x, location.y, location.z, rotation.x, rotation.y, rotation.z,
                location.worldId, location.interiorId, streamDistance, drawDistance);
    }

    public static DynamicSampObject get(int id) {
        return DynamicSampObjectImpl.get(id);
    }

    public static void destroyAll() {
        DynamicSampObjectImpl.destroyAll();
    }

    float getDrawDistance();
    void setDrawDistance(float drawDistance);

    int getModelId();
    void setModelId(int modelId);

    float getStreamDistance();
    void setStreamDistance(float streamDistance);

    Vector3D getRotation();
    void setRotation(Vector3D rotation);

    Location getLocation();
    void setLocation(Location location);

    void moveObject(float x, float y, float z, float speed, float rX, float rY, float rZ);
    void moveObject(Vector3D position, float speed, Vector3D rotation);
    void stopObject();

    boolean isMoving();
    void attachCameraToObject(Player player);

    void attachObjectToVehicle(Vehicle vehicle, float offsetX, float offsetY, float offsetZ, float rX, float rY, float rZ);
    void attachObjectToVehicle(Vehicle vehicle, Vector3D offset, Vector3D rotation);

    void editObject(Player player);

    void setObjectMaterial(int materialIndex, int modelId, String txdName, String textureName, Color color);
    void setObjectMaterialText(String text, int materialIndex, ObjectMaterialSize materialSize, String fontFace, int fontSize, boolean bold, Color fontColor,
                               Color backColor, ObjectMaterialTextAlign textAlign);

    Collection<PlayerDynamicObjectMaterial> getObjectMaterial();
    Collection<PlayerDynamicObjectMaterialText> getObjectMaterialText();

    void update();
}
