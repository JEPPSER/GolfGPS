package com.jesperbergstrom.golfgps.input;

import android.view.ScaleGestureDetector;

import com.jesperbergstrom.golfgps.MainActivity;

public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

    private MainActivity main;

    public ScaleListener(MainActivity context) {
        main = context;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        main.imageScale *= detector.getScaleFactor();
        if (main.imageScale < 0.2) {
            main.imageScale = 0.2;
        } else if (main.imageScale > 0.8) {
            main.imageScale = 0.8;
        }
        main.canvasView.drawCurrentHole();
        return true;
    }
}
