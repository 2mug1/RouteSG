package net.hotsmc.sg.game;

import java.util.Comparator;

public class VoteMapComparator implements Comparator<VoteMap> {

    @Override
    public int compare(VoteMap o1, VoteMap o2) {
        return o1.getVotes() > o2.getVotes() ? -1 : 1;
    }
}
