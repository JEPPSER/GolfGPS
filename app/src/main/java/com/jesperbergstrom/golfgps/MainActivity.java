package com.jesperbergstrom.golfgps;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jesperbergstrom.golfgps.input.ScaleListener;
import com.jesperbergstrom.golfgps.location.MyLocationListener;
import com.jesperbergstrom.golfgps.view.CanvasView;

/*
 * TODO:
 * - Zooming and panning image
 * - Load all holes and their info
 * - Display mid-green location on hole
 * - Display current player position
 */

public class MainActivity extends Activity {

    public ImageView imageView;

    public Bitmap currentHole;
    public CanvasView canvasView;
    public double imageScale = 0.2;
    public double x = 0;
    public double y = 0;
    public ScaleGestureDetector sgd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        currentHole = BitmapFactory.decodeResource(getResources(), R.drawable.nine);

        sgd = new ScaleGestureDetector(this, new ScaleListener(this));
        canvasView = new CanvasView(this, imageView);
        canvasView.drawCurrentHole();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener locationListener = new MyLocationListener();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                return;
            }
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        sgd.onTouchEvent(event);
        return true;
    }
}
