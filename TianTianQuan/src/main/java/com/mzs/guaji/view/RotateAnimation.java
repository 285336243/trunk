package com.mzs.guaji.view;

import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.graphics.Camera;
import android.graphics.Matrix;

/**
 * Created by wlanjie on 14-2-12.
 */
public class RotateAnimation extends Animation {
    private int centerX, centerY;
    private boolean isZoomin;
    private Camera camera = new Camera();
    /**如果isRotate为true,旋转到第二个页面,*/
    private boolean isRotate;

    public RotateAnimation(boolean isZoomin, boolean isRotate) {
        this.isZoomin = isZoomin;
        this.isRotate = isRotate;
    }
    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);

        centerX = width / 2;
        centerY = height / 2;
        setDuration(400);
        setFillAfter(true);
        setInterpolator(new DecelerateInterpolator());
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final Matrix matrix = t.getMatrix();
        int direction = -1;
        camera.save();
        if (!isZoomin) {
            interpolatedTime = 1 - interpolatedTime;
            direction = 1;
        }

        camera.translate(0, 0, 200 + 200.0f * (interpolatedTime-1));
        if(isRotate) {
            camera.rotateY(90 * interpolatedTime * direction);
        }else {
            camera.rotateY(-90 * interpolatedTime * direction);
        }
        camera.getMatrix(matrix);
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
        camera.restore();
    }
}
