package com.example.betateacher;

public class Teacher {
    private String name, phone, Experience, About, uid;

    public Teacher() {
    }

    public Teacher(String FullName, String Phone, String Experience, String About, String uid) {
        this.name = FullName;
        this.phone = Phone;
        this.Experience = Experience;
        this.uid = uid;
        this.About = About;

    }

    public String getName() {
        return name;
    }

    public void setName(String FullName) {
        this.name = FullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setAbout(String About) {
        this.About = About;
    }

    public String getAbout() {
        return About;
    }

    public void setExperience(String Experience) {
        this.Experience = Experience;
    }

    public String getExperience() {
        return Experience;
    }
}



