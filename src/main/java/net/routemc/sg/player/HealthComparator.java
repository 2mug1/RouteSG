package net.routemc.sg.player;

import java.util.Comparator;

/**
 * 体力ゲージを比較させる
 */
public class HealthComparator implements Comparator<PlayerHealth> {

    @Override
    public int compare(PlayerHealth o1, PlayerHealth o2) {
        return o1.getHealth() > o2.getHealth() ? -1 : 1;
    }
}