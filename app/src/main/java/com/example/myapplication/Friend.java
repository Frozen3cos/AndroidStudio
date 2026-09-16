package com.example.myapplication;

import java.io.Serializable;

public class Friend implements Serializable {

    private int id;                 // 数据库主键
    private String name;
    private int avatarIndex;        // 头像下标（0~3）
    private String signature;
    private String phone;
    private String email;
    private String gender;

    public Friend(String name, int avatarIndex, String signature,
                  String phone, String email, String gender) {
        this.name = name;
        this.avatarIndex = avatarIndex;
        this.signature = signature;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public int getAvatarIndex() { return avatarIndex; }
    public String getSignature() { return signature; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getGender() { return gender; }
}