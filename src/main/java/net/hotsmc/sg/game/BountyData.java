package net.hotsmc.sg.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BountyData {

    private GamePlayer sender;
    private GamePlayer target;
    private int points;
}
