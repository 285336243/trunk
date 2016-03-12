package com.mzs.guaji.view;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;
import android.view.WindowManager;

import com.mzs.guaji.R;
import com.nineoldandroids.animation.ValueAnimator;

public class FlakeView extends View {

    Bitmap droid;      
    int numFlakes = 0; 
    ArrayList<Flake> flakes = new ArrayList<Flake>(); 

	ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
    long prevTime;
    Matrix m = new Matrix();

	public FlakeView(Context context) {
        super(context);
        droid = BitmapFactory.decodeResource(getResources(), R.drawable.icon_poplover_tj);
	    WindowManager windowManager =  (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                long nowTime = System.currentTimeMillis();
                float secs = (float)(nowTime - prevTime) / 800f;
                prevTime = nowTime;
                for (int i = 0; i < numFlakes; ++i) {
                    Flake flake = flakes.get(i);
                    flake.y -= (flake.speed * secs);
                    if(flake.y < 0) {
                    	flake.y = getHeight();
                    }
                }
                invalidate();
            }
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(3000);
    }

    int getNumFlakes() {
        return numFlakes;
    }

    public void setNumFlakes(int quantity) {
        numFlakes = quantity;
    }

    void addFlakes(int quantity) {
        for (int i = 0; i < quantity; ++i) {
            flakes.add(Flake.createFlake(getWidth(), droid));
        }
        setNumFlakes(numFlakes + quantity);
    }

    void subtractFlakes(int quantity) {
        for (int i = 0; i < quantity; ++i) {
            int index = numFlakes - i - 1;
            flakes.remove(index);
        }
        setNumFlakes(numFlakes - quantity);
    }

	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        numFlakes = 0;
        addFlakes(24);
        animator.cancel();
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < numFlakes; ++i) {
            Flake flake = flakes.get(i);
            m.setTranslate(-flake.width/2, -flake.height/2);
            m.postRotate(flake.rotation);
            m.postTranslate(flake.width/2 + flake.x, flake.height/2 + flake.y);
            canvas.setMatrix(m);
            canvas.drawBitmap(flake.bitmap, flake.x, 10, null);
        }
    }

    public void pause() {
        animator.cancel();
    }

    public void resume() {
        animator.start();
    }

}
