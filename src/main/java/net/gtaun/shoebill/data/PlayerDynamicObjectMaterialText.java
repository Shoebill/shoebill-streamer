package net.gtaun.shoebill.data;

import net.gtaun.shoebill.DynamicSampObject;
import net.gtaun.shoebill.constant.ObjectMaterialSize;
import net.gtaun.shoebill.constant.ObjectMaterialTextAlign;

// Created by marvin on 28.12.14 in project shoebill-streamer.
// Copyright (c) 2014 Marvin Haschker. All rights reserved.
public class PlayerDynamicObjectMaterialText {
    private DynamicSampObject object;
    private String text, fontFace;
    private int materialIndex, fontSize;
    private ObjectMaterialTextAlign textAlignment;
    private boolean bold;
    private ObjectMaterialSize materialSize;
    private Color fontColor, backColor;

    public PlayerDynamicObjectMaterialText(DynamicSampObject object, String text, String fontFace, int materialIndex, ObjectMaterialSize materialSize,
                                           int fontSize, ObjectMaterialTextAlign textAlignment, boolean bold, Color fontColor, Color backColor) {
        this.object = object;
        this.text = text;
        this.fontFace = fontFace;
        this.materialIndex = materialIndex;
        this.materialSize = materialSize;
        this.fontSize = fontSize;
        this.textAlignment = textAlignment;
        this.bold = bold;
        this.fontColor = fontColor;
        this.backColor = backColor;
    }

    public DynamicSampObject getObject() {
        return object;
    }

    public String getText() {
        return text;
    }

    public String getFontFace() {
        return fontFace;
    }

    public int getMaterialIndex() {
        return materialIndex;
    }

    public ObjectMaterialSize getMaterialSize() {
        return materialSize;
    }

    public int getFontSize() {
        return fontSize;
    }

    public ObjectMaterialTextAlign getTextAlignment() {
        return textAlignment;
    }

    public boolean isBold() {
        return bold;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public Color getBackColor() {
        return backColor;
    }
}
