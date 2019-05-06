package net.routemc.sg.player;

import lombok.Getter;

@Getter
public class PlayerHealth {

    private GamePlayer gamePlayer;
    private double health;

    public PlayerHealth(GamePlayer gamePlayer, double health){
        this.gamePlayer = gamePlayer;
        this.health = health;
    }
}
