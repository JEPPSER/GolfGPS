package com.jesperbergstrom.golfgps.entities;

public class Hole {

    public double imageScale;

    public Coordinates frontCoor;
    public Coordinates midCoor;
    public Coordinates backCoor;

    public double midPixelX;
    public double midPixelY;

    public int holeNumber;

    public Hole(int holeNumber) {
        this.holeNumber = holeNumber;
    }
}
