package com.jesperbergstrom.golfgps.entities;

public class Scorecard {

    public Course course;
    public PlayerScore[] players;

    public Scorecard(Course course) {
        players = new PlayerScore[]{new PlayerScore(course.holes.size()), new PlayerScore(course.holes.size()), new PlayerScore(course.holes.size()), new PlayerScore(course.holes.size())};
        this.course = course;
    }
}
