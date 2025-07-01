package com.example.test_api.model;

import java.io.Serializable;

public class Sighting implements Serializable {
    public String id;
    public int owner;
    public String birdName;
    public String dateTime;
    public String location;
    public String notes;
}
