package com.jesperbergstrom.golfgps.entities;

public class Hole {

    public double xScale;
    public double yScale;

    public Coordinates frontCoor;
    public Coordinates midCoor;
    public Coordinates backCoor;

    public double midPixelX;
    public double midPixelY;

    public int holeNumber;
    public int par;

    public Hole(int holeNumber) {
        this.holeNumber = holeNumber;
    }
}
