package com.example.myfirstapp;

/** 好友实体，包含昵称和头像资源ID */
public class Friend {
    private final String name;
    private final int avatarRes;

    public Friend(String name, int avatarRes) {
        this.name = name;
        this.avatarRes = avatarRes;
    }

    public String getName() { return name; }
    public int getAvatarRes() { return avatarRes; }
}