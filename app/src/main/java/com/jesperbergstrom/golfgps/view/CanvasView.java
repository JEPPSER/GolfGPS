package com.jesperbergstrom.golfgps.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.ImageView;

import com.jesperbergstrom.golfgps.MainActivity;

public class CanvasView {

    private MainActivity main;
    private Canvas canvas;
    private ImageView imageView;
    private int width;
    private int height;
    private Bitmap b;

    public CanvasView(Context context, ImageView imageView) {
        main = (MainActivity) context;
        width = imageView.getWidth();
        height = imageView.getHeight();
        b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(b);
        this.imageView = imageView;
        imageView.setImageBitmap(b);
    }

    public void drawCurrentHole() {
        canvas.drawRect(0, 0, width, height, new Paint(Color.BLACK));
        Rect src = new Rect(0, 0, main.currentHole.getWidth() - 1, main.currentHole.getHeight() - 1);
        Rect dest = new Rect(0, 0, (int) (main.currentHole.getWidth() * main.imageScale - 1), (int) (main.currentHole.getHeight() * main.imageScale - 1));
        canvas.drawBitmap(main.currentHole, src, dest, null);
        imageView.setImageBitmap(b);
    }
}
