package com.jesperbergstrom.golfgps.entities;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Course {

    public String name;
    public ArrayList<Hole> holes;
    AssetManager assetManager;

    public Course(String courseDirectory, AssetManager assetManager) {
        this.assetManager = assetManager;
        holes = new ArrayList<Hole>();
        name = courseDirectory;
        loadHoles(courseDirectory);
    }

    private void loadHoles(String dir) {
        try {

            // Read coordinates.
            InputStream inputStream = assetManager.open(name + "/" + "coordinates.txt");
            Scanner scan = new Scanner(inputStream, "UTF-8");

            while(scan.hasNext()) {
                String line = scan.nextLine();
                Hole h = new Hole(Integer.parseInt(line.split(":")[0]));
                line = line.split(":")[1];
                String[] coordinates = line.split(";");

                Coordinates front = new Coordinates();
                front.latitude = Double.parseDouble(coordinates[0].split(",")[0]);
                front.longitude = Double.parseDouble(coordinates[0].split(",")[1]);
                h.frontCoor = front;

                Coordinates mid = new Coordinates();
                mid.latitude = Double.parseDouble(coordinates[1].split(",")[0]);
                mid.longitude = Double.parseDouble(coordinates[1].split(",")[1]);
                h.midCoor = mid;

                Coordinates back = new Coordinates();
                back.latitude = Double.parseDouble(coordinates[2].split(",")[0]);
                back.longitude = Double.parseDouble(coordinates[2].split(",")[1]);
                h.backCoor = back;

                holes.add(h);
            }

            // Scan hole image info.
            inputStream = assetManager.open(name + "/" + "info.txt");
            scan = new Scanner(inputStream, "UTF-8");
            double xScale = Double.parseDouble(scan.nextLine().replace("xScale=", ""));
            double yScale = Double.parseDouble(scan.nextLine().replace("yScale=", ""));
            for(int i = 0; scan.hasNext(); i++) {
                holes.get(i).xScale = xScale;
                holes.get(i).yScale = yScale;
                String[] pixel = scan.nextLine().split(":")[1].split(",");
                holes.get(i).midPixelX = Double.parseDouble(pixel[0]);
                holes.get(i).midPixelY = Double.parseDouble(pixel[1]);
            }
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
