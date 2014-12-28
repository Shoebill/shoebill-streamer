package net.gtaun.shoebill.data;

import net.gtaun.shoebill.object.DynamicSampObject;

// Created by marvin on 28.12.14 in project shoebill-streamer.
// Copyright (c) 2014 Marvin Haschker. All rights reserved.
public class PlayerDynamicObjectMaterial {
    private DynamicSampObject object;
    private int materialIndex, modelId;
    private String txdName, textureName;
    private Color color;

    public PlayerDynamicObjectMaterial(DynamicSampObject object, int materialIndex, int modelId, String txdName, String textureName, Color color) {
        this.object = object;
        this.materialIndex = materialIndex;
        this.modelId = modelId;
        this.txdName = txdName;
        this.textureName = textureName;
        this.color = color;
    }

    public DynamicSampObject getObject() {
        return object;
    }

    public int getMaterialIndex() {
        return materialIndex;
    }

    public int getModelId() {
        return modelId;
    }

    public String getTxdName() {
        return txdName;
    }

    public String getTextureName() {
        return textureName;
    }

    public Color getColor() {
        return color;
    }
}
