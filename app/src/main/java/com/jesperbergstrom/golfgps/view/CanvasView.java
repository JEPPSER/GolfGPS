package com.jesperbergstrom.golfgps.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.jesperbergstrom.golfgps.MainActivity;

public class CanvasView {

    private MainActivity main;
    private Canvas canvas;
    private ImageView imageView;
    private int width;
    private int height;
    private Bitmap b;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private PointF startMove = new PointF();
    private PointF startImage = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;

    @SuppressLint("ClickableViewAccessibility")
    public CanvasView(Context context, ImageView imageView) {
        main = (MainActivity) context;
        width = imageView.getWidth();
        height = imageView.getHeight();
        b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(b);
        this.imageView = imageView;
        imageView.setImageBitmap(b);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        System.out.println(event.getX() + ", " + event.getY());
                        startMove.set(event.getX(), event.getY());
                        startImage.set(main.x, main.y);
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            midPoint(mid, event);
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            main.x = (int) (startImage.x + event.getX() - startMove.x);
                            main.y = (int) (startImage.y + event.getY() - startMove.y);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                float scale = newDist / oldDist;
                                main.imageScale *= scale;
                                if (main.imageScale < 0.2) {
                                    main.imageScale = 0.2;
                                } else if (main.imageScale > 0.8) {
                                    main.imageScale = 0.8;
                                }
                            }
                        }
                        drawCurrentHole();
                        break;
                }
                return true;
            }
        });
    }

    public void drawCurrentHole() {
        canvas.drawRect(0, 0, width, height, new Paint(Color.BLACK));
        int scaledWidth = (int) (main.currentHole.getWidth() * main.imageScale);
        int scaledHeight = (int) (main.currentHole.getHeight() * main.imageScale);
        Bitmap bitmap = Bitmap.createScaledBitmap(main.currentHole, scaledWidth, scaledHeight, true);
        canvas.drawBitmap(bitmap, main.x, main.y, null);
        imageView.setImageBitmap(b);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}
