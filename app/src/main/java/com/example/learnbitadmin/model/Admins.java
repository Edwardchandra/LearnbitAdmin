package com.example.learnbitadmin.model;

public class Admins {

    private String name, email;

    public Admins(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Admins() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
