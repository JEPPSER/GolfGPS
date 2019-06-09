package com.jesperbergstrom.golfgps;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import com.jesperbergstrom.golfgps.entities.Course;
import com.jesperbergstrom.golfgps.location.MyLocationListener;
import com.jesperbergstrom.golfgps.view.CanvasView;

import java.io.IOException;

/*
 * TODO:
 * - Display mid-green location on hole
 * - Display current player position
 */

public class MainActivity extends Activity {

    public ImageView imageView;

    public Bitmap currentHole;
    public CanvasView canvasView;
    public double imageScale = 1;
    public int imageWidth;
    public int imageHeight;
    public int x = 0;
    public int y = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        AssetManager assetManager = getAssets();

        try {
            Course course = new Course("rydo", assetManager);
            currentHole = BitmapFactory.decodeStream(assetManager.open("rydo/16.png"));
            imageWidth = currentHole.getWidth();
            imageHeight = currentHole.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        canvasView = new CanvasView(this, imageView);
        canvasView.drawCurrentHole();
    }
}
