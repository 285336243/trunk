/*
 * Copyright (C) 2013 Chen Hui <calmer91@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shengzhish.xyj.darrage.danmaku.renderer;

import com.shengzhish.xyj.darrage.danmaku.model.IDanmakus;
import com.shengzhish.xyj.darrage.danmaku.model.IDisplayer;
import com.shengzhish.xyj.darrage.danmaku.model.android.Danmakus;

public interface IRenderer {

    public class Area {

        public final int[] mRefreshRect = new int[4];
        private int mMaxHeight;
        private int mMaxWidth;

        public void setEdge(int maxWidth, int maxHeight) {
            mMaxWidth = maxWidth;
            mMaxHeight = maxHeight;
        }

        public void reset() {
            set(mMaxWidth, mMaxHeight, 0, 0);
        }

        public void resizeToMax() {
            set(0, 0, mMaxWidth, mMaxHeight);
        }

        public void set(int left, int top, int right, int bottom) {
            mRefreshRect[0] = left;
            mRefreshRect[1] = top;
            mRefreshRect[2] = right;
            mRefreshRect[3] = bottom;
        }

    }

    public void draw(IDisplayer disp, IDanmakus danmakus, long startRenderTime);

    public void draw(IDisplayer disp, Danmakus danmakus, long startRenderTime);

    public void clear();

    public void release();

    public Area getRefreshArea();

}
