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
import com.jesperbergstrom.golfgps.entities.Coordinates;
import com.jesperbergstrom.golfgps.entities.Hole;
import com.jesperbergstrom.golfgps.entities.Pixel;

public class CanvasView {

    private MainActivity main;
    private Canvas canvas;
    private ImageView imageView;
    public Bitmap scaledImage;
    private int width;
    private int height;
    public double upperScale;
    public double lowerScale;
    private Bitmap b;

    private Paint red;
    private Paint black;
    private Paint blue;
    private Paint white;

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
        blue = new Paint();
        blue.setColor(Color.BLUE);
        white = new Paint();
        white.setColor(Color.WHITE);

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
                Hole hole = main.course.holes.get(main.currentHoleNumber - 1);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        if (main.markerToggle.isChecked()) {
                            main.currentMarker = pixelToCoordinates(hole, (event.getX() - main.x) / main.imageScale, (event.getY() - main.y) / main.imageScale);
                        } else {
                            startMove.set(event.getX(), event.getY());
                            startImage.set(main.x, main.y);
                        }
                        drawCurrentHole();
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
                            if (main.markerToggle.isChecked()) {
                                main.currentMarker = pixelToCoordinates(hole, (event.getX() - main.x) / main.imageScale, (event.getY() - main.y) / main.imageScale);
                            } else {
                                main.x = (int) (startImage.x + event.getX() - startMove.x);
                                main.y = (int) (startImage.y + event.getY() - startMove.y);
                                adjustImagePosition();
                            }
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

        Hole h = main.course.holes.get(main.currentHoleNumber - 1);
        float holeX = (float) (h.midPixelX * main.imageScale + main.x);
        float holeY = (float) (h.midPixelY * main.imageScale + main.y);

        Pixel p = coordinatesToPixel(h, main.playerPosLat, main.playerPosLong);
        float playerX = (float) (p.x * main.imageScale + main.x);
        float playerY = (float) (p.y * main.imageScale + main.y);

        Pixel m = coordinatesToPixel(h, main.currentMarker.latitude, main.currentMarker.longitude);
        float markerX = (float) (m.x * main.imageScale + main.x);
        float markerY = (float) (m.y * main.imageScale + main.y);

        // Draw hole image
        canvas.drawBitmap(scaledImage, main.x, main.y, null);

        // Draw line to marker
        black.setStrokeWidth(18);
        canvas.drawLine(playerX, playerY, markerX, markerY, black);
        black.setStrokeWidth(1);
        white.setStrokeWidth(10);
        canvas.drawLine(playerX, playerY, markerX, markerY, white);
        white.setStrokeWidth(1);
        canvas.drawCircle(markerX, markerY, 20, black);
        canvas.drawCircle(markerX, markerY, 16, white);

        // Draw player
        canvas.drawCircle(playerX, playerY, 44, black);
        canvas.drawCircle(playerX, playerY, 40, white);
        canvas.drawCircle(playerX, playerY, 30, blue);

        // Draw hole/flag (mid green)
        canvas.drawCircle(holeX, holeY, 20, red);
        red.setStrokeWidth(10);
        canvas.drawLine(holeX, holeY, holeX, holeY - 80, red);
        canvas.drawLine(holeX, holeY - 80 + 5, holeX + 30, holeY - 70 + 5, red);
        canvas.drawLine(holeX, holeY - 60 + 5, holeX + 30, holeY - 70 + 5, red);
        canvas.drawLine(holeX, holeY - 70 + 5, holeX + 35, holeY - 70 + 5, red);
        red.setStrokeWidth(1);

        imageView.setImageBitmap(b);
    }

    public Pixel coordinatesToPixel(Hole hole, double latitude, double longitude) {
        int x = (int) (hole.midPixelX + (longitude - hole.midCoor.longitude) * hole.xScale);
        int y = (int) (hole.midPixelY - (latitude - hole.midCoor.latitude) * hole.yScale);
        return new Pixel(x, y);
    }

    public Coordinates pixelToCoordinates(Hole hole, double x, double y) {
        double longitude = hole.midCoor.longitude + (x - hole.midPixelX) / hole.xScale;
        double latitude = hole.midCoor.latitude - (y - hole.midPixelY) / hole.yScale;
        return new Coordinates(latitude, longitude);
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

    public void scaleImage() {
        scaledImage = Bitmap.createScaledBitmap(main.currentHole, (int) (main.imageWidth * main.imageScale), (int) (main.imageHeight * main.imageScale), true);
    }
}
