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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jesperbergstrom.golfgps.entities.Course;
import com.jesperbergstrom.golfgps.location.MyLocationListener;
import com.jesperbergstrom.golfgps.view.CanvasView;

import java.io.IOException;

/*
 * TODO:
 * - Displaying distances
 * - Display current player position
 */

public class MainActivity extends Activity {

    public ImageView imageView;
    public Button prevBtn;
    public Button nextBtn;
    public TextView holeText;

    public Course course;

    public AssetManager assetManager;
    public Bitmap currentHole;
    public CanvasView canvasView;
    public double imageScale = 1;
    public int imageWidth;
    public int imageHeight;
    public int x = 0;
    public int y = 0;
    public int currentHoleNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        prevBtn = findViewById(R.id.prevBtn);
        nextBtn = findViewById(R.id.nextBtn);
        holeText = findViewById(R.id.holeText);
        assetManager = getAssets();
        course = new Course("rydo", assetManager);

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
        try {
            currentHole = BitmapFactory.decodeStream(assetManager.open(course.name + "/" + currentHoleNumber + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        canvasView = new CanvasView(this, imageView);
        changeHole();

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentHoleNumber < course.holes.size()) {
                    currentHoleNumber++;
                    changeHole();
                }
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentHoleNumber > 1) {
                    currentHoleNumber--;
                    changeHole();
                }
            }
        });
    }

    private void changeHole() {
        try {
            currentHole = BitmapFactory.decodeStream(assetManager.open(course.name + "/" + currentHoleNumber + ".png"));
            imageWidth = currentHole.getWidth();
            imageHeight = currentHole.getHeight();
            if (currentHole.getWidth() >= currentHole.getHeight()) {
                imageScale = (double) imageView.getWidth() / (double) imageWidth;
            } else {
                imageScale = (double) imageView.getHeight() / (double) imageHeight;
            }
            canvasView.lowerScale = imageScale;
            canvasView.upperScale = imageScale * 2;
            canvasView.scaleImage();
            int midX = imageView.getWidth() / 2;
            int midY = imageView.getHeight() / 2;
            x = midX - canvasView.scaledImage.getWidth() / 2;
            y = midY - canvasView.scaledImage.getHeight() / 2;
            holeText.setText("Hole " + currentHoleNumber);
            canvasView.drawCurrentHole();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentHoleNumber", currentHoleNumber);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentHoleNumber = savedInstanceState.getInt("currentHoleNumber");
    }
}
