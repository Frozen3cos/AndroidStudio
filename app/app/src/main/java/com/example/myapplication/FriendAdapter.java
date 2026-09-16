package com.example.myfirstapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/** 自定义 ListView 适配器，将 Friend 对象渲染到 item_friend.xml 上 */
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
        // 复用 convertView 提升性能
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_friend, parent, false);
        }

        Friend friend = friends.get(position);

        ImageView ivFriendAvatar = convertView.findViewById(R.id.ivFriendAvatar);
        TextView tvFriendName = convertView.findViewById(R.id.tvFriendName);

        ivFriendAvatar.setImageResource(friend.getAvatarRes());
        tvFriendName.setText(friend.getName());

        return convertView;
    }
}