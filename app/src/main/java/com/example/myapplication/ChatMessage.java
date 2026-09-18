package com.example.myapplication;

public class ChatMessage {
    private final int id;
    private final String friendName;
    private final String content;
    private final boolean isFromMe;
    private final long timestamp;

    public ChatMessage(int id, String friendName, String content,
                       boolean isFromMe, long timestamp) {
        this.id = id;
        this.friendName = friendName;
        this.content = content;
        this.isFromMe = isFromMe;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public String getFriendName() { return friendName; }
    public String getContent() { return content; }
    public boolean isFromMe() { return isFromMe; }
    public long getTimestamp() { return timestamp; }
}