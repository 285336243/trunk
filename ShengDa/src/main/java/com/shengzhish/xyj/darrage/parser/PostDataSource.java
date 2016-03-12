package com.shengzhish.xyj.darrage.parser;

import com.shengzhish.xyj.darrage.danmaku.model.BaseDanmaku;
import com.shengzhish.xyj.darrage.danmaku.parser.BaseDanmakuParser;

import java.util.Set;

/**
 * Created by sunjian on 14-6-18.
 */
public abstract class PostDataSource {

    private BaseDanmakuParser baseDanmakuParser;

    public BaseDanmakuParser getBaseDanmakuParser() {
        return baseDanmakuParser;
    }

    public void setBaseDanmakuParser(BaseDanmakuParser baseDanmakuParser) {
        this.baseDanmakuParser = baseDanmakuParser;
    }

    public abstract Set<BaseDanmaku> next();

}
