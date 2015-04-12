package net.gtaun.shoebill.object;

import net.gtaun.shoebill.DynamicObjectPool;
import net.gtaun.shoebill.Streamer;
import net.gtaun.shoebill.constant.ObjectMaterialSize;
import net.gtaun.shoebill.constant.ObjectMaterialTextAlign;
import net.gtaun.shoebill.data.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;

// Created by marvin on 27.12.14 in project shoebill-streamer.
// Copyright (c) 2014 Marvin Haschker. All rights reserved.
public interface DynamicSampObject extends Destroyable, Updateable {

    int INVALID_ID = -1;
    DynamicObjectPool<DynamicSampObject> objectPool = new DynamicObjectPool<>();

    /**
     * Creates an dynamic samp object.
     * @param modelId The modelid of the object
     * @param x X-Cord
     * @param y Y-Cord
     * @param z Z-Cord
     * @param rX Rotation-X
     * @param rY Rotation-Y
     * @param rZ Rotation-Z
     * @param worldId In which world the object should be visible
     * @param interiorId In which interior the object should be visible
     * @param streamDistance When the object should be created for the player
     * @param drawDistance From how far the object should be visible (viewDistance)
     * @return The created object
     */
    static DynamicSampObject create(int modelId, float x, float y, float z, float rX, float rY, float rZ,
                                    int worldId, int interiorId, float streamDistance, float drawDistance) {
        if(!Streamer.isInitialized) {
            System.err.println("Please insert the Streamer into your plugins section in resources.yml");
            return null;
        } else {
            return new DynamicSampObjectImpl(modelId, x, y, z, rX, rY, rZ, (worldId == -1) ? 0 : worldId, (interiorId == -1) ? 0 : interiorId,
                    (streamDistance == 0.0) ? 200f : streamDistance, drawDistance);
        }
    }

    static DynamicSampObject create(int modelId, float x, float y, float z, float rX, float rY, float rZ) {
        return create(modelId, x, y, z, rX, rY, rZ, 0, 0, 210, 200);
    }

    static DynamicSampObject create(int modelId, double x, double y, double z, double rX, double rY, double rZ) {
        return create(modelId, (float)x, (float)y, (float)z, (float)rX, (float)rY, (float)rZ);
    }

    static DynamicSampObject create(int modelId, double x, double y, double z, double rX, double rY, double rZ,
                                    int worldId, int interiorId, double streamDistance, double drawDistance) {
        return create(modelId, (float)x, (float)y, (float)z, (float)rX, (float)rY, (float)rZ, (worldId == -1) ? 0 : worldId, (interiorId == -1) ? 0 : interiorId,
                (streamDistance == 0.0) ? 200f : (float)streamDistance, (float)drawDistance);
    }

    /**
     * Creates an dynamic samp object.
     * @param modelId The modelid of the object
     * @param location The location of the object (x, y, z, interior, world)
     * @param rotation The rotation of the object (x, y, z)
     * @param streamDistance When the object should be created for the player
     * @param drawDistance From how far the object should be visible (viewDistance)
     * @return The created object
     */
    static DynamicSampObject create(int modelId, @Nonnull Location location, @Nonnull Vector3D rotation, float streamDistance, float drawDistance) {
        return new DynamicSampObjectImpl(modelId, location.x, location.y, location.z, rotation.x, rotation.y, rotation.z,
                location.worldId, location.interiorId, streamDistance, drawDistance);
    }

    static DynamicSampObject get(int id) {
        return DynamicSampObjectImpl.get(id);
    }

    static Collection<DynamicSampObject> get() { return new ArrayList<>(objectPool.getAllObjects()); }

    static void destroyAll() {
        DynamicSampObjectImpl.destroyAll();
    }

    int getId();

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
}
