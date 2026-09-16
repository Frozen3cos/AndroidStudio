package com.example.myapplication;

import java.io.Serializable;

public class Friend implements Serializable {

    private final String name;
    private final int avatarRes;
    private final String signature;
    private final String phone;
    private final String email;
    private final String gender;

    public Friend(String name, int avatarRes, String signature,
                  String phone, String email, String gender) {
        this.name = name;
        this.avatarRes = avatarRes;
        this.signature = signature;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
    }

    public String getName() { return name; }
    public int getAvatarRes() { return avatarRes; }
    public String getSignature() { return signature; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getGender() { return gender; }
}