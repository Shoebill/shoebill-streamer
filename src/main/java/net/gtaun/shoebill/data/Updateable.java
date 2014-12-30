package net.gtaun.shoebill.data;

import net.gtaun.shoebill.object.Player;

// Created by marvin on 28.12.14 in project shoebill-streamer.
// Copyright (c) 2014 Marvin Haschker. All rights reserved.
public interface Updateable {
    void update();
    void updatePlayer(Player player);
}
