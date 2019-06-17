package com.jesperbergstrom.golfgps.entities;

public class PlayerScore {

    public String name;
    public int[] scores;

    public PlayerScore(int numOfHoles) {
        scores = new int[numOfHoles];
    }
}
