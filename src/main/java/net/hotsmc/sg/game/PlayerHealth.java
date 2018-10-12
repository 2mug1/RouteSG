package net.hotsmc.sg.game;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PlayerHealth {

    private GamePlayer gamePlayer;
    private double health;

    public PlayerHealth(GamePlayer gamePlayer, double health){
        this.gamePlayer = gamePlayer;
        this.health = health;
    }
}
