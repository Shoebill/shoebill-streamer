package net.gtaun.shoebill;

import net.gtaun.shoebill.constant.ObjectMaterialSize;
import net.gtaun.shoebill.constant.ObjectMaterialTextAlign;
import net.gtaun.shoebill.data.*;
import net.gtaun.shoebill.event.destroyable.DestroyEvent;
import net.gtaun.shoebill.event.object.PlayerObjectMovedEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.player.PlayerUpdateEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerObject;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.Attentions;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;

import java.util.*;

// Created by marvin on 27.12.14 in project shoebill-streamer.
// Copyright (c) 2014 Marvin Haschker. All rights reserved.
class DynamicSampObjectImpl implements DynamicSampObject {

    static Queue<Integer> availableIds;
    static List<DynamicSampObjectImpl> dynamicSampObjects;
    static EventManager eventManager;
    private static int allocatedObjects = -1;

    private int modelId, id = INVALID_ID;
    private Location location;
    private Vector3D rotation;
    private float streamDistance, drawDistance;
    private EventManagerNode eventManagerNode;
    private WeakHashMap<Player, PlayerObject> playerObjects;
    private List<Player> movingObjectsForPlayer;
    private EventManagerNode stopEventNode;
    private WeakHashMap<Integer, PlayerDynamicObjectMaterial> objectMaterial;
    private WeakHashMap<Integer, PlayerDynamicObjectMaterialText> objectMaterialText;

    public DynamicSampObjectImpl(int modelId, float x, float y, float z, float rX, float rY, float rZ,
                                 int worldId, int interiorId, float streamDistance, float drawDistance) {
        this.modelId = modelId;
        this.streamDistance = streamDistance;
        this.drawDistance = drawDistance;
        this.rotation = new Vector3D(rX, rY, rZ);
        this.location = new Location(x, y, z, interiorId, worldId);
        this.movingObjectsForPlayer = new ArrayList<>();
        if(availableIds.size() > 0)
            this.id = availableIds.poll();
        else
            this.id = ++allocatedObjects;
        this.playerObjects = new WeakHashMap<>();
        DynamicSampObjectImpl.dynamicSampObjects.add(this);
        this.eventManagerNode = DynamicSampObjectImpl.eventManager.createChildNode();
        this.stopEventNode = eventManagerNode.createChildNode();
        this.objectMaterial = new WeakHashMap<>();
        this.objectMaterialText = new WeakHashMap<>();
        eventManager.registerHandler(PlayerUpdateEvent.class, playerUpdateEvent -> {
            Player player = playerUpdateEvent.getPlayer();
            Location playerLocation = player.getLocation();
            createAndDeleteObject(player, playerLocation);
        });
        eventManager.registerHandler(PlayerDisconnectEvent.class, playerDisconnectEvent -> destroyAllPlayerObjects(playerDisconnectEvent.getPlayer()));
    }

    private void destroyAllPlayerObjects(Player player) {
        playerObjects.entrySet().stream().filter(entry -> entry.getKey() == player && !entry.getValue().isDestroyed()).forEach(entry -> entry.getValue().destroy());
        playerObjects.remove(player);
    }

    private void destroyAllVisibleObjects() {
        playerObjects.entrySet().stream().filter(entry -> !entry.getValue().isDestroyed()).forEach(entry -> entry.getValue().destroy());
        playerObjects.clear();
    }

    private void createAndDeleteObject(Player player, Location playerLocation) {
        Iterator<Map.Entry<Player, PlayerObject>> playerObjectIterator = playerObjects.entrySet().iterator();
        while(playerObjectIterator.hasNext()) {
            Map.Entry<Player, PlayerObject> entry = playerObjectIterator.next();
            if(streamDistance < location.distance(playerLocation) && entry.getKey() == player) {
                entry.getValue().destroy();
                playerObjectIterator.remove();
                return;
            }
        }
        if(playerObjects.containsKey(player))
            return;
        if(location.distance(playerLocation) <= streamDistance)
            createPlayerObject(player);
    }

    private void createPlayerObject(Player player) {
        PlayerObject pObject = PlayerObject.create(player, modelId, location, rotation, drawDistance);
        playerObjects.put(player, pObject);
        objectMaterial.entrySet().forEach(set -> {
            PlayerDynamicObjectMaterial material = set.getValue();
            pObject.setMaterial(set.getKey(), material.getModelId(), material.getTxdName(), material.getTextureName(), material.getColor());
        });
        objectMaterialText.entrySet().forEach(set -> {
            PlayerDynamicObjectMaterialText text = set.getValue();
            pObject.setMaterialText(text.getText(), set.getKey(), text.getMaterialSize(), text.getFontFace(), text.getFontSize(), text.isBold(), text.getFontColor(), text.getBackColor(),
                                    text.getTextAlignment());
        });
        System.out.println("Created obj " + id + " for " + player.getName());
    }

    public static DynamicSampObjectImpl get(int id) {
        if(id >= dynamicSampObjects.size())
            return null;
        return dynamicSampObjects.get(id);
    }

    static void destroyAll() {
        dynamicSampObjects.stream().filter(obj -> !obj.isDestroyed()).forEach(DynamicSampObjectImpl::destroy);
        dynamicSampObjects.clear();
    }

    @Override
    public float getDrawDistance() {
        return drawDistance;
    }

    @Override
    public void setDrawDistance(float drawDistance) {
        this.drawDistance = drawDistance;
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
    public Vector3D getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(Vector3D rotation) {
        this.rotation = rotation;
        update();
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
    public void moveObject(float x, float y, float z, float speed, float rX, float rY, float rZ) {
        stopEventNode.cancelAll();
        movingObjectsForPlayer.clear();
        playerObjects.entrySet().stream().filter(set -> !set.getValue().isDestroyed()).forEach(set -> {
            set.getValue().move(x, y, z, speed, rX, rY, rZ);
            movingObjectsForPlayer.add(set.getKey());
            stopEventNode.registerHandler(PlayerObjectMovedEvent.class, HandlerPriority.HIGH, Attentions.create().object(set.getValue()), (e) -> {
                if(movingObjectsForPlayer.contains(e.getPlayer()))
                    movingObjectsForPlayer.remove(e.getPlayer());
                checkIfAllObjectsMoved(stopEventNode, x, y, z, rX, rY, rZ);
            });
            stopEventNode.registerHandler(PlayerDisconnectEvent.class, HandlerPriority.HIGH, Attentions.create().object(set.getKey()), (e) -> {
                if(movingObjectsForPlayer.contains(e.getPlayer()))
                    movingObjectsForPlayer.remove(e.getPlayer());
                checkIfAllObjectsMoved(stopEventNode, x, y, z, rX, rY, rZ);
            });
        });
    }

    private void checkIfAllObjectsMoved(EventManagerNode stopEventNode, float x, float y, float z, float rX, float rY, float rZ) {
        if(movingObjectsForPlayer.size() == 0) {
            stopEventNode.cancelAll();
            this.location = this.location.clone();
            this.location.set(x, y, z);
            this.rotation = new Vector3D(rX, rY, rZ);
        }
    }

    @Override
    public void moveObject(Vector3D position, float speed, Vector3D rotation) {
        moveObject(position.x, position.y, position.z, speed, rotation.x, rotation.y, rotation.z);
    }

    @Override
    public void stopObject() {
        stopEventNode.cancelAll();
        movingObjectsForPlayer.clear();
    }

    @Override
    public boolean isMoving() {
        return movingObjectsForPlayer.size() != 0;
    }

    @Override
    public void attachCameraToObject(Player player) {
        playerObjects.entrySet().stream().filter(set -> !set.getValue().isDestroyed() && set.getKey() == player)
                .forEach(set -> set.getValue().attachCamera(player));
    }

    @Override
    public void attachObjectToVehicle(Vehicle vehicle, float offsetX, float offsetY, float offsetZ, float rX, float rY, float rZ) {
        playerObjects.entrySet().stream().filter(set -> !set.getValue().isDestroyed())
                .forEach(set -> set.getValue().attach(vehicle, offsetX, offsetY, offsetZ, rX, rY, rZ));
    }

    @Override
    public void attachObjectToVehicle(Vehicle vehicle, Vector3D offset, Vector3D rotation) {
        attachObjectToVehicle(vehicle, offset.x, offset.y, offset.z, rotation.x, rotation.y, rotation.z);
    }

    @Override
    public void editObject(Player player) {
        playerObjects.entrySet().stream().filter(set -> !set.getValue().isDestroyed() && set.getKey() == player)
                .forEach(set -> set.getKey().editPlayerObject(set.getValue()));
    }

    @Override
    public void setObjectMaterial(int materialIndex, int modelId, String txdName, String textureName, Color color) {
        objectMaterial.put(materialIndex, new PlayerDynamicObjectMaterial(this, materialIndex, modelId, txdName, textureName, color));
        update();
    }

    @Override
    public void setObjectMaterialText(String text, int materialIndex, ObjectMaterialSize materialSize, String fontFace, int fontSize, boolean bold,
                                      Color fontColor, Color backColor, ObjectMaterialTextAlign textAlign) {
        objectMaterialText.put(materialIndex, new PlayerDynamicObjectMaterialText(this, text, fontFace, materialIndex, materialSize, fontSize, textAlign, bold, fontColor, backColor));
        update();
    }

    @Override
    public Collection<PlayerDynamicObjectMaterial> getObjectMaterial() {
        Collection<PlayerDynamicObjectMaterial> objectMaterialCollection = new ArrayList<>();
        objectMaterial.entrySet().forEach(set -> objectMaterialCollection.add(set.getValue()));
        return objectMaterialCollection;
    }

    @Override
    public Collection<PlayerDynamicObjectMaterialText> getObjectMaterialText() {
        Collection<PlayerDynamicObjectMaterialText> objectMaterialTextCollection = new ArrayList<>();
        objectMaterialText.entrySet().forEach(set -> objectMaterialTextCollection.add(set.getValue()));
        return objectMaterialTextCollection;
    }

    @Override
    public void update() {
        Collection<Player> oldPlayers = new ArrayList<>();
        playerObjects.entrySet().stream().filter(obj -> !obj.getValue().isDestroyed()).forEach(obj -> oldPlayers.add(obj.getKey()));
        destroyAllVisibleObjects();
        oldPlayers.stream().forEach(player -> createAndDeleteObject(player, player.getLocation()));
        oldPlayers.clear();
    }

    @Override
    public void destroy() {
        destroyAllVisibleObjects();
        availableIds.offer(this.id);
        dynamicSampObjects.remove(this);
        objectMaterial.clear();
        objectMaterialText.clear();
        stopEventNode.destroy();

        DestroyEvent event = new DestroyEvent(this);
        eventManager.dispatchEvent(event, this);

        eventManagerNode.destroy();
        this.id = INVALID_ID;
    }

    @Override
    public boolean isDestroyed() {
        return this.id == INVALID_ID;
    }
}
