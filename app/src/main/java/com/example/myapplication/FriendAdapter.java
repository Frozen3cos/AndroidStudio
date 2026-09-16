package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class FriendAdapter extends ArrayAdapter<Friend> {

    private final Context context;
    private final List<Friend> friends;

    public FriendAdapter(Context context, List<Friend> friends) {
        super(context, 0, friends);
        this.context = context;
        this.friends = friends;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_friend, parent, false);
        }

        Friend friend = friends.get(position);

        ImageView ivFriendAvatar = convertView.findViewById(R.id.ivFriendAvatar);
        TextView tvFriendName = convertView.findViewById(R.id.tvFriendName);
        TextView tvFriendSignature = convertView.findViewById(R.id.tvFriendSignature);

        ivFriendAvatar.setImageResource(friend.getAvatarRes());
        tvFriendName.setText(friend.getName());
        tvFriendSignature.setText(friend.getSignature());

        return convertView;
    }
}