package net.gtaun.shoebill.label;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.Streamer;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.destroyable.DestroyEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.player.PlayerUpdateEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerLabel;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

import java.util.*;

// Created by marvin on 28.12.14 in project shoebill-streamer.
// Copyright (c) 2014 Marvin Haschker. All rights reserved.
class DynamicLabelImpl implements DynamicLabel {

    public static EventManager eventManager = Shoebill.get().getResourceManager().getPlugin(Streamer.class).getEventManager();

    private String text;
    private Color color;
    private Player attachedPlayer;
    private Vehicle attachedVehicle;
    private Location location;
    private float drawDistance, streamDistance;
    private boolean testLOS;
    private int id;
    private EventManagerNode eventManagerNode;
    private WeakHashMap<Player, PlayerLabel> visibleLabels;


    public DynamicLabelImpl(String text, Color color, float x, float y, float z, float drawDistance, float streamDistance, int worldId, int interiorId, boolean testLOS) {
        this(text, color, x, y, z, drawDistance, null, null, streamDistance, worldId, interiorId, testLOS);
    }

    public DynamicLabelImpl(String text, Color color, float x, float y, float z, float drawDistance, Player attachedPlayer, Vehicle attachedVehicle,
                            float streamDistance, int worldId, int interiorId, boolean testLOS) {
        this(text, color, attachedPlayer, attachedVehicle, new Location(x, y, z, interiorId, worldId), drawDistance, streamDistance, testLOS);
    }

    public DynamicLabelImpl(String text, Color color, Player attachedPlayer, Vehicle attachedVehicle, Location location,
                            float drawDistance, float streamDistance, boolean testLOS) {
        this.text = text;
        this.color = color;
        this.attachedPlayer = attachedPlayer;
        this.attachedVehicle = attachedVehicle;
        this.location = location;
        this.drawDistance = drawDistance;
        this.streamDistance = streamDistance;
        this.testLOS = testLOS;
        this.eventManagerNode = eventManager.createChildNode();
        this.id = objectPool.pullId();
        this.visibleLabels = new WeakHashMap<>();
        eventManagerNode.registerHandler(PlayerUpdateEvent.class, (e) -> {
            Player player = e.getPlayer();
            Location playerLocation = player.getLocation();
            createAndDeleteLabel(player, playerLocation);
        });
        eventManagerNode.registerHandler(PlayerDisconnectEvent.class, (e) -> removeLabelForPlayer(e.getPlayer()));
    }

    private void createAndDeleteLabel(Player player, Location playerLocation) {
        Iterator<Map.Entry<Player, PlayerLabel>> playerLabelIterator = visibleLabels.entrySet().iterator();
        while(playerLabelIterator.hasNext()) {
            Map.Entry<Player, PlayerLabel> entry = playerLabelIterator.next();
            if(streamDistance < location.distance(playerLocation) && entry.getKey() == player) {
                entry.getValue().destroy();
                playerLabelIterator.remove();
                return;
            }
        }
        if(visibleLabels.containsKey(player))
            return;
        if(location.distance(playerLocation) <= streamDistance)
            createPlayerLabel(player);
    }

    private void createPlayerLabel(Player player) {
        PlayerLabel pObject = null;
        if(attachedPlayer == null && attachedVehicle == null) pObject = PlayerLabel.create(player, text, color, location, drawDistance, testLOS);
        else if(attachedPlayer != null && attachedVehicle == null) pObject = PlayerLabel.create(player, text, color, location, drawDistance, testLOS, attachedPlayer);
        else //noinspection ConstantConditions
            if(attachedPlayer == null && attachedVehicle != null) pObject = PlayerLabel.create(player, text, color, location, drawDistance, testLOS, attachedVehicle);
        visibleLabels.put(player, pObject);
    }

    private void removeLabelForPlayer(Player player) {
        if(visibleLabels.containsKey(player)) {
            PlayerLabel label = visibleLabels.get(player);
            if(!label.isDestroyed())
                label.destroy();
            visibleLabels.remove(player);
        }
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
        update();
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
        update();
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
    public float getStreamDistance() {
        return streamDistance;
    }

    @Override
    public void setStreamDistance(float streamDistance) {
        this.streamDistance = streamDistance;
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
    public Player getAttachedPlayer() {
        return attachedPlayer;
    }

    @Override
    public Vehicle getAttachedVehicle() {
        return attachedVehicle;
    }

    @Override
    public boolean isTestLOS() {
        return testLOS;
    }

    @Override
    public void setTestLOS(boolean testLOS) {
        this.testLOS = testLOS;
        update();
    }

    @Override
    public void destroy() {
        destroyAllVisibleLabels();
        objectPool.recycleId(this.id);
        objectPool.removeObject(this);

        DestroyEvent event = new DestroyEvent(this);
        eventManager.dispatchEvent(event, this);

        eventManagerNode.destroy();
        this.id = INVALID_ID;
    }

    private void destroyAllVisibleLabels() {
        visibleLabels.entrySet().stream().filter(set -> set.getValue() != null && !set.getValue().isDestroyed()).forEach(set -> set.getValue().destroy());
        visibleLabels.clear();
    }

    @Override
    public boolean isDestroyed() {
        return this.id == INVALID_ID;
    }

    @Override
    public void update() {
        Collection<Player> oldPlayers = new ArrayList<>();
        visibleLabels.entrySet().stream().filter(obj -> !obj.getValue().isDestroyed()).forEach(obj -> oldPlayers.add(obj.getKey()));
        destroyAllVisibleLabels();
        oldPlayers.stream().forEach(player -> createAndDeleteLabel(player, player.getLocation()));
        oldPlayers.clear();
    }

    static DynamicLabel get(int id) {
        return objectPool.getObject(id);
    }

    static void destroyAll() {
        List<DynamicLabel> copyOfLabels = new ArrayList<>(objectPool.getAllObjects());
        Collections.copy(copyOfLabels, objectPool.getAllObjects());
        copyOfLabels.stream().filter(obj -> !obj.isDestroyed()).forEach(DynamicLabel::destroy);
        objectPool.clearAllObjects();
        copyOfLabels.clear();
    }
}
