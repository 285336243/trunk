package com.mzs.guaji.view;

import java.util.HashMap;

import android.graphics.Bitmap;

public class Flake {

   public float x, y;
   public  float rotation;
   public  float speed;
   public float rotationSpeed;
    public int width, height;
    public Bitmap bitmap;

    static HashMap<Integer, Bitmap> bitmapMap = new HashMap<Integer, Bitmap>();

   public static Flake createFlake(float xRange, Bitmap originalBitmap) {
        Flake flake = new Flake();
        flake.width = originalBitmap.getWidth();
        flake.height = originalBitmap.getHeight();

        
        flake.x = (float)Math.random() * (xRange - flake.width);
        flake.y = (flake.height + (float)Math.random() * flake.height);

        flake.y = 0 - (flake.height + (float)Math.random() * flake.height);

        flake.speed = 50 + (float) Math.random() * 150;

        flake.rotation = (float) Math.random() * 180 - 90;
        flake.rotationSpeed = (float) Math.random() * 90 - 45;

        flake.bitmap = bitmapMap.get(flake.width);
        if (flake.bitmap == null) {
            flake.bitmap = Bitmap.createScaledBitmap(originalBitmap,
                    (int)flake.width, (int)flake.height, true);
            bitmapMap.put(flake.width, flake.bitmap);
        }
        return flake;
    }
}
