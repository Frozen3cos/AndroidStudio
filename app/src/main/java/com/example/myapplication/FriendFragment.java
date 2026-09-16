package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment {

    private ImageView ivUserAvatar;
    private TextView tvWelcome;
    private ListView lvFriends;
    private List<Friend> friendList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        loadUserInfo();
        setupFriendList();
    }

    private void initViews(View view) {
        ivUserAvatar = view.findViewById(R.id.ivUserAvatar);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        lvFriends = view.findViewById(R.id.lvFriends);
    }

    private void loadUserInfo() {
        SharedPreferences sp = requireContext()
                .getSharedPreferences("login_prefs", 0);
        String username = sp.getString("username", "游客");
        int avatarIndex = sp.getInt("avatar_index", 0);
        int[] avatarRes = {R.drawable.avatar1, R.drawable.avatar2,
                R.drawable.avatar3, R.drawable.avatar4};
        int avatar = avatarRes[Math.max(0, Math.min(avatarIndex, 3))];

        tvWelcome.setText("欢迎回来，" + username);
        ivUserAvatar.setImageResource(avatar);
    }

    private void setupFriendList() {
        friendList = new ArrayList<>();
        friendList.add(new Friend("小明", R.drawable.avatar1, "热爱编程的男孩",
                "13800000001", "xiaoming@mail.com", "男"));
        friendList.add(new Friend("小红", R.drawable.avatar2, "每天都要开心哦",
                "13800000002", "xiaohong@mail.com", "女"));
        friendList.add(new Friend("小刚", R.drawable.avatar3, "运动使我快乐",
                "13800000003", "xiaogang@mail.com", "男"));
        friendList.add(new Friend("小美", R.drawable.avatar4, "美食探店达人",
                "13800000004", "xiaomei@mail.com", "女"));
        friendList.add(new Friend("阿杰", R.drawable.avatar1, "音乐是生命的一部分",
                "13800000005", "ajie@mail.com", "男"));
        friendList.add(new Friend("莉莉", R.drawable.avatar2, "旅行者，摄影爱好者",
                "13800000006", "lili@mail.com", "女"));
        friendList.add(new Friend("大壮", R.drawable.avatar3, "健身教练",
                "13800000007", "dazhuang@mail.com", "男"));
        friendList.add(new Friend("悠悠", R.drawable.avatar4, "插画师",
                "13800000008", "youyou@mail.com", "女"));

        FriendAdapter adapter = new FriendAdapter(requireContext(), friendList);
        lvFriends.setAdapter(adapter);
        // C5: 空状态（数据为空时显示提示）
        lvFriends.setEmptyView(requireView().findViewById(R.id.tvFriendEmpty));

        lvFriends.setOnItemClickListener((parent, v, position, id) -> {
            Friend friend = friendList.get(position);
            Intent intent = new Intent(requireContext(), FriendDetailActivity.class);
            intent.putExtra("friend", friend);
            startActivity(intent);
            requireActivity().overridePendingTransition(
                    android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}