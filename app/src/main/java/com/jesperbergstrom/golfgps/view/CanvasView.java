package com.jesperbergstrom.golfgps.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.widget.ImageView;

import com.jesperbergstrom.golfgps.MainActivity;

public class CanvasView {

    private MainActivity main;
    private Canvas canvas;
    private ImageView imageView;
    private Bitmap b;

    public CanvasView(Context context, ImageView imageView) {
        main = (MainActivity) context;
        b = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(b);
        this.imageView = imageView;
        imageView.setImageBitmap(b);
    }

    public void drawCurrentHole() {
        imageView.setImageBitmap(b);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), new Paint(Color.BLACK));
        Paint p = new Paint(Color.RED);
        Bitmap b = scaleBitmap(main.currentHole, main.currentHole.getWidth() * main.imageScale, main.currentHole.getHeight() * main.imageScale);
        canvas.drawBitmap(b, (int) main.x, (int) main.y, p);
    }

    private Bitmap scaleBitmap(Bitmap bitmap, double newWidth, double newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
}
