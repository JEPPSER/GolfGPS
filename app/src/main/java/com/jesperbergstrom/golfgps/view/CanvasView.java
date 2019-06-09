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
import com.jesperbergstrom.golfgps.entities.Hole;

public class CanvasView {

    private MainActivity main;
    private Canvas canvas;
    private ImageView imageView;
    private Bitmap scaledImage;
    private int width;
    private int height;
    private double upperScale;
    private double lowerScale;
    private Bitmap b;

    private Paint red;
    private Paint black;

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
        red = new Paint();
        red.setColor(Color.RED);
        black = new Paint();
        black.setColor(Color.BLACK);

        main = (MainActivity) context;
        width = imageView.getWidth();
        height = imageView.getHeight();
        b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(b);

        if (main.currentHole.getWidth() >= main.currentHole.getHeight()) {
            main.imageScale = (double) imageView.getWidth() / (double) main.currentHole.getWidth();
        } else {
            main.imageScale = (double) imageView.getHeight() / (double) main.currentHole.getHeight();
        }

        lowerScale = main.imageScale;
        upperScale = main.imageScale * 2;

        scaleImage();
        this.imageView = imageView;
        imageView.setImageBitmap(b);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
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
                            adjustImagePosition();
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                float scale = newDist / oldDist;
                                main.imageScale *= scale;
                                if (main.imageScale < lowerScale) {
                                    main.imageScale = lowerScale;
                                } else if (main.imageScale > upperScale) {
                                    main.imageScale = upperScale;
                                }
                                scaleImage();
                                adjustImagePosition();
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
        canvas.drawRect(0, 0, width, height, black);
        canvas.drawBitmap(scaledImage, main.x, main.y, null);
        Hole h = main.course.holes.get(main.currentHoleNumber - 1);
        canvas.drawCircle((float) (h.midPixelX * main.imageScale + main.x), (float) (h.midPixelY * main.imageScale + main.y), 10, red);
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

    private void adjustImagePosition() {
        if (main.x < -scaledImage.getWidth() + 200) {
            main.x = -scaledImage.getWidth() + 200;
        } else if (main.x > width - 200) {
            main.x = width - 200;
        }
        if (main.y < -scaledImage.getHeight() + 200) {
            main.y = -scaledImage.getHeight() + 200;
        } else if (main.y > height - 200) {
            main.y = height - 200;
        }
    }

    private void scaleImage() {
        scaledImage = Bitmap.createScaledBitmap(main.currentHole, (int) (main.imageWidth * main.imageScale), (int) (main.imageHeight * main.imageScale), true);
    }
}
