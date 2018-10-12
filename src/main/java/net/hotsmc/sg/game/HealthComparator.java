package net.hotsmc.sg.game;

import java.util.Comparator;

/**
 * 体力ゲージを比較させる
 */
public class HealthComparator implements Comparator<PlayerHealth> {

    @Override
    public int compare(PlayerHealth o1, PlayerHealth o2) {
        return o1.getHealth() > o1.getHealth() ? -1 : 1;
    }
}