package com.mzs.guaji.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.mzs.guaji.R;

public class GifView extends View implements Runnable {
	private GifOpenHelper gHelper;
	private boolean isStop = true;
	private int delta;
	private Bitmap bmp;

	public GifView(Context context) {
		this(context, null);

	}

	public GifView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.gifView);
		int n = ta.getIndexCount();

		for (int i = 0; i < n; i++) {
			int attr = ta.getIndex(i);

			switch (attr) {
			case R.styleable.gifView_src:
				int id = ta.getResourceId(R.styleable.gifView_src, 0);
				setSrc(id);
				break;

			case R.styleable.gifView_delay:
				int idelta = ta.getInteger(R.styleable.gifView_delay, 1);
				setDelta(idelta);
				break;

			case R.styleable.gifView_stop:
				boolean sp = ta.getBoolean(R.styleable.gifView_stop, false);
				if (!sp) {
					setStop();
				}
				break;
			}

		}

		ta.recycle();
	}


	public void setStop() {
		isStop = false;
	}


	public void setStart() {
		isStop = true;

		Thread updateTimer = new Thread(this);
		updateTimer.start();
	}


	public void setSrc(int id) {

		gHelper = new GifOpenHelper();
		gHelper.read(GifView.this.getResources().openRawResource(id));
		bmp = gHelper.getImage();
	}

	public void setDelta(int is) {
		delta = is;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		return gHelper.getWidth();
	}

	private int measureHeight(int measureSpec) {
		return gHelper.getHeigh();
	}

	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(bmp, 0, 0, new Paint());
		bmp = gHelper.nextBitmap();

	}

	public void run() {
		while (isStop) {
			try {
				this.postInvalidate();
				Thread.sleep(gHelper.nextDelay() / delta);
			} catch (Exception ex) {

			}
		}
	}

}
