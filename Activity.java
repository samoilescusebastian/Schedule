package com.example.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Activity {
    private static final String EMPTY_STRING = "";

    private String name;

    private String location;

    private String description;

    private int startHour;

    private int endHour;

    private int type;

    private int code;

    Activity(Activity activity) {
        this.name = activity.getName();
        this.location = activity.getLocation();
        this.description = activity.getDescription();
        this.startHour = activity.getStartHour();
        this.endHour = activity.getEndHour();
        this.type = activity.getType();
        this.code = activity.code;
    }
    Activity(String name, String location, String description, int startHour,
             int endHour, int type, int code) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.startHour = startHour;
        this.endHour = endHour;
        this.type = type;
        this.code = code;
    }
    Activity() { }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescprition(String descprition) {
        this.description = descprition;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public  String toString() {
        return "Activity [Name =" + name
                + ", location =" + location
                + ", description = " + description
                + ", start hour = " + startHour
                + ", end hour = " + endHour
                + ", type = " + type
                + ", code = " + code + "]";
    }
    public  String ActivityToJson() {
        ObjectMapper object = new ObjectMapper();
        String jsonStr = EMPTY_STRING;
        try{
            jsonStr = object.writeValueAsString(this);
            return jsonStr;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return jsonStr;
    }

}
