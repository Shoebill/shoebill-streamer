package net.gtaun.shoebill;

import java.util.*;

// Created by marvin on 28.12.14 in project shoebill-streamer.
// Copyright (c) 2014 Marvin Haschker. All rights reserved.
public class DynamicObjectPool<E> {
    private Queue<Integer> availableIds;

    private List<E> objects;
    private int allocatedObjects = -1;

    public DynamicObjectPool() {
        availableIds = new LinkedList<>();
        objects = new ArrayList<>();
    }

    public int pullId() {
        if(availableIds.size() > 0)
            return availableIds.poll();
        else
            return ++allocatedObjects;
    }

    public void recycleId(int id) {
        availableIds.offer(id);
    }

    public void addObject(E object) {
        objects.add(object);
    }

    public void removeObject(E object) {
        objects.remove(object);
    }

    public List<E> getAllObjects() {
        return objects;
    }

    public E getObject(int index) {
        return objects.get(index);
    }

    public int getObjectAmount() {
        return objects.size();
    }

    public void clearAllObjects() {
        objects.clear();
    }

    public Iterator<E> getIterator() { return objects.iterator(); }
}
