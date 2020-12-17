package com.example.appointmentplanner;

import java.util.ArrayList;
import java.util.HashMap;

public class DoctorInfo {
    public static final String Queue = "queue";
    public static final String Availability = "availability";
    public static final String ImagePath = "imagePath";
    public static final String Name = "name";
    public static final String Proficiency = "proficiency";
    public static final String Uid = "uid";
    public static final String Path = "path";
    private String name, proficiency, imagePath;
    private boolean availability;
    private ArrayList<PatientInfo> queue;
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private HashMap<String,PatientInfo>patientQueue;


    public HashMap<String, PatientInfo> getPatientQueue() {
        return patientQueue;
    }

    public void setPatientQueue(HashMap<String, PatientInfo> patientQueue) {
        this.patientQueue = patientQueue;
    }

    //constructor
    public DoctorInfo(){
        queue = new ArrayList<>();
    }


    public boolean isAvailability() {
        return availability;
    }

    public ArrayList<PatientInfo> getQueue() {
        return queue;
    }

    public void setQueue(ArrayList<PatientInfo> queue) {
        this.queue = queue;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getProficiency() {
        return proficiency;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setProficiency(String proficiency) {
        this.proficiency = proficiency;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }
}
