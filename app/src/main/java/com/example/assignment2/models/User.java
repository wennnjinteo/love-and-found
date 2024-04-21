package com.example.assignment2.models;

import java.io.Serializable;


public class User implements Serializable {
    public String name, image, email, gender, university, token, id;

    // Corrected the return type to String
    public String getName() {
        return name;
    }

    // Consider adding setters if you need to modify these fields outside the class
    public void setName(String name) {
        this.name = name;
    }

    // Adding getters for the other fields might be useful too
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }
}