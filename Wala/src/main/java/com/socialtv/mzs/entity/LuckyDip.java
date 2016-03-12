package com.socialtv.mzs.entity;

import com.socialtv.Response;

/**
 * Created by wlanjie on 14-9-9.
 */
public class LuckyDip extends Response {
    private Game game;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
