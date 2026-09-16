package com.example.myapplication;

public class Message {
    private final String senderName;
    private final int avatarRes;
    private final String lastMessage;
    private final String time;

    public Message(String senderName, int avatarRes, String lastMessage, String time) {
        this.senderName = senderName;
        this.avatarRes = avatarRes;
        this.lastMessage = lastMessage;
        this.time = time;
    }

    public String getSenderName() { return senderName; }
    public int getAvatarRes() { return avatarRes; }
    public String getLastMessage() { return lastMessage; }
    public String getTime() { return time; }
}