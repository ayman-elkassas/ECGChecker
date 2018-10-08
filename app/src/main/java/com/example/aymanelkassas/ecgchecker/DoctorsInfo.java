package com.example.aymanelkassas.ecgchecker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

/**
 * Created by Ayman Elkassas on 3/17/2018.
 */

public class DoctorsInfo {

    private String id;
    private String fname;
    private String lname;
    private String mail;
    private String avatar;
    private String bio;
    private String job;
    private String location;
    private String phone;
    private String working_hours;
    private Map<String, Object> my_patients;
    private Map<String, Object> drafts;

    public DoctorsInfo()
    {

    }

    public DoctorsInfo(String id, String fname, String lname, String mail, String avatar, String bio, String job, String location, String phone, String working_hours, Map<String, Object> my_patients, Map<String, Object> drafts) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.mail = mail;
        this.avatar = avatar;
        this.bio = bio;
        this.job = job;
        this.location = location;
        this.phone = phone;
        this.working_hours = working_hours;
        this.my_patients = my_patients;
        this.drafts = drafts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWorking_hours() {
        return working_hours;
    }

    public void setWorking_hours(String working_hours) {
        this.working_hours = working_hours;
    }

    public Map<String, Object> getMy_patients() {
        return my_patients;
    }

    public void setMy_patients(Map<String, Object> my_patients) {
        this.my_patients = my_patients;
    }

    public Map<String, Object> getDrafts() {
        return drafts;
    }

    public void setDrafts(Map<String, Object> drafts) {
        this.drafts = drafts;
    }
}
