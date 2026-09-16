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
        ImageView ivAvatar = convertView.findViewById(R.id.ivFriendAvatar);
        TextView tvName = convertView.findViewById(R.id.tvFriendName);
        TextView tvSignature = convertView.findViewById(R.id.tvFriendSignature);

        ivAvatar.setImageResource(avatarResOf(friend.getAvatarIndex()));
        tvName.setText(friend.getName());
        tvSignature.setText(friend.getSignature());
        return convertView;
    }

    /** 头像下标 → drawable 资源ID */
    public static int avatarResOf(int index) {
        int[] res = {R.drawable.avatar1, R.drawable.avatar2,
                R.drawable.avatar3, R.drawable.avatar4};
        if (index < 0 || index >= res.length) index = 0;
        return res[index];
    }
}