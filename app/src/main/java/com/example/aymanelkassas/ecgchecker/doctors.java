package com.example.aymanelkassas.ecgchecker;

/**
 * Created by Ayman Elkassas on 4/9/2018.
 */

public class doctors {

    private String name,image,job;

    public doctors()
    {

    }

    public doctors(String name, String image, String job) {
        this.name = name;
        this.image = image;
        this.job = job;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
