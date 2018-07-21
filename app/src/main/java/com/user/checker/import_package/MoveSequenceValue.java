package com.user.checker.import_package;

import java.io.Serializable;


public class MoveSequenceValue implements Serializable {
    Index beforeMove;
    Index afterMove;
    Board.Player player;
    boolean enemyRemoved;
    boolean isUpgraded;
    Sprite.Type enemyType;


    public MoveSequenceValue(Index beforeMove, Index afterMove, Board.Player player, boolean enemyRemoved, boolean isUpgraded, Sprite.Type enemyType) {

        this.beforeMove = beforeMove;
        this.afterMove = afterMove;
        this.player = player;
        this.enemyRemoved = enemyRemoved;
        this.isUpgraded = isUpgraded;
        this.enemyType = enemyType;
    }

}
