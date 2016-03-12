package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by wlanjie on 14-2-21.
 */
public class Game extends GuaJiResponse {

    @Expose
    private List<GameList> games;

    public List<GameList> getGames() {
        return games;
    }

    public void setGames(List<GameList> games) {
        this.games = games;
    }
}
