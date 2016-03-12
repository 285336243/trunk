/*
 * Copyright (C) 2008 ZXing authors
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


package com.shengzhish.xyj.activity.precioushouse.zxing.view;

import java.util.Collection;
import java.util.HashSet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.precioushouse.zxing.camera.CameraManager;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points. 锟皆讹拷锟斤拷锟絍iew锟斤拷锟斤拷锟斤拷时锟叫硷拷锟斤拷示锟斤拷
 */
public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 10L;
    private static final int OPAQUE = 0xFF;

    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int frameColor;
    private final int laserColor;
    private final int resultPointColor;
    private int scannerAlpha;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;
    private Bitmap bmp;
    private int location;
    private boolean initialization = true;
    private int dis;
    private int bitmapLeft;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every
        // time in onDraw().
        paint = new Paint();
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        frameColor = resources.getColor(R.color.viewfinder_frame);
        laserColor = resources.getColor(R.color.viewfinder_laser);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        bmp = BitmapFactory.decodeResource(resources, R.drawable.scanner_line);
        scannerAlpha = 0;
        possibleResultPoints = new HashSet<ResultPoint>(5);

    }

    @Override
    public void onDraw(Canvas canvas) {
        // 取二维码的矩形
        Rect frame = CameraManager.get().getFramingRect();
        if (initialization) {
            location = frame.top;
            dis = 2;
//            dis = frame.height() / 30;
            bitmapLeft = (frame.width() - bmp.getWidth()) / 2;
            initialization = false;
        }
        if (frame == null) {
            return;
        }
        int width = canvas.getWidth();// 720
        int height = canvas.getHeight();// 1184
        // 绘制四周半透明黑色
        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);// 上面矩形
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);// 左边矩形
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {

            // Draw a two pixel solid black border inside the framing rect
            paint.setColor(frameColor);
            // 扫描框上边线
            canvas.drawRect(frame.left - 4, frame.top - 4, frame.left + 32, frame.top + 2, paint);
            canvas.drawRect(frame.right - 32, frame.top - 4, frame.right + 5, frame.top + 2, paint);
            // 扫描框左边线
            canvas.drawRect(frame.left - 4, frame.top + 2, frame.left + 2, frame.top + 34, paint);
            canvas.drawRect(frame.left - 4, frame.bottom - 33, frame.left + 2, frame.bottom - 1, paint);
            // 扫描框右边线
            canvas.drawRect(frame.right - 1, frame.top, frame.right + 5, frame.top + 32, paint);
            canvas.drawRect(frame.right - 1, frame.bottom - 33, frame.right + 5, frame.bottom - 1, paint);
            // 扫描框下边线
            canvas.drawRect(frame.left - 4, frame.bottom - 1, frame.left + 32, frame.bottom + 5, paint);
            canvas.drawRect(frame.right - 32, frame.bottom - 1, frame.right + 5, frame.bottom + 5, paint);
            // Draw a red "laser scanner" line through the middle to show
            // decoding is active
//			paint.setColor(laserColor);
            // paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
            // scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
//			int middle = frame.height() / 2 + frame.top;
//			canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);
            location = location + dis;
            if ((location)> ( frame.bottom-5) || location < frame.top) {
                location = frame.top;
            }

            canvas.drawBitmap(bmp, frame.left + 2 + bitmapLeft, location , null);
        /*    location = location + dis;
            if (location > frame.bottom || location < frame.top) {
                location = frame.top;
            }*/
            /*
			 * Collection<ResultPoint> currentPossible = possibleResultPoints;
			 * Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			 * if (currentPossible.isEmpty()) { lastPossibleResultPoints = null;
			 * } else { possibleResultPoints = new HashSet<ResultPoint>(5);
			 * lastPossibleResultPoints = currentPossible;
			 * paint.setAlpha(OPAQUE); paint.setColor(resultPointColor); for
			 * (ResultPoint point : currentPossible) {
			 * canvas.drawCircle(frame.left + point.getX(), frame.top +
			 * point.getY(), 6.0f, paint); } } if (currentLast != null) {
			 * paint.setAlpha(OPAQUE / 2); paint.setColor(resultPointColor); for
			 * (ResultPoint point : currentLast) { canvas.drawCircle(frame.left
			 * + point.getX(), frame.top + point.getY(), 3.0f, paint); } }
			 */

            // Request another update at the animation interval, but only
            // repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live
     * scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

}
