package com.shengzhish.xyj.darrage.parser;

import android.os.AsyncTask;
import android.os.Handler;

import com.shengzhish.xyj.darrage.danmaku.model.BaseDanmaku;
import com.shengzhish.xyj.darrage.danmaku.model.android.Danmakus;
import com.shengzhish.xyj.darrage.danmaku.parser.BaseDanmakuParser;
import com.shengzhish.xyj.darrage.ui.widget.DanmakuSurfaceView;

import java.util.Set;

/**
 * Created by sunjian on 14-6-18.
 */
public class PostDanmakuParser extends BaseDanmakuParser {
    private final Danmakus danmakus = new Danmakus();

    private int index = 0;

    private Handler handler = new Handler();

    private long delayTime;

    final private PostDataSource dataSource;

    final private DanmakuSurfaceView danmakuSurfaceView;


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    Set<BaseDanmaku> set=null;
                    if (dataSource != null) {
                        set = dataSource.next();
                    }
                    if (set != null && set.size() > 0) {
                        for (BaseDanmaku baseDanmaku : set) {
                            danmakuSurfaceView.addDanmaku(baseDanmaku);
                        }
                    }
                    return null;
                }
            }.execute();
            handler.postDelayed(runnable, delayTime);
        }
    };

    public PostDanmakuParser(final PostDataSource dataSource, long delayTime, DanmakuSurfaceView danmakuSurfaceView) {
        this.dataSource = dataSource;
        this.delayTime = delayTime;
        this.danmakuSurfaceView = danmakuSurfaceView;
        this.dataSource.setBaseDanmakuParser(this);
        handler.postDelayed(runnable, 0);
    }

    @Override
    protected Danmakus parse() {
        return danmakus;
    }


    public void release() {
        handler.removeCallbacks(runnable);
    }
}
