package com.example.appointmentplanner;

public class PatientInfo  {

    String name, age, phone, uid;
    String path;
    public String getUid() {
        return uid;
    }
    public void setPath(String path)
    {
        this.path = path;
    }
    public String getPath()
    {
        return path;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public PatientInfo() {
    }

    public PatientInfo(String name, String age, String phone) {
        this.name = name;
        this.age = age;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
