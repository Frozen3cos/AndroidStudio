package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.db.FriendDao;
import com.example.myapplication.db.UserDao;

import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment {

    private ImageView ivUserAvatar;
    private TextView tvWelcome;
    private ListView lvFriends;
    private EditText etSearch;

    private List<Friend> friendList;
    private FriendDao friendDao;

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
        friendDao = new FriendDao(requireContext());
        initViews(view);
        loadUserInfo();
        loadFriends();
        setupSearch(view);
    }

    private void initViews(View view) {
        ivUserAvatar = view.findViewById(R.id.ivUserAvatar);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        lvFriends = view.findViewById(R.id.lvFriends);
        etSearch = view.findViewById(R.id.etSearch);
    }

    private void loadUserInfo() {
        SharedPreferences sp = requireContext().getSharedPreferences("login_prefs", 0);
        String username = sp.getString("username", "游客");
        int avatarIndex = sp.getInt("avatar_index", 0);
        tvWelcome.setText("欢迎回来，" + username);
        ivUserAvatar.setImageResource(FriendAdapter.avatarResOf(avatarIndex));
    }

    /** 从数据库读取全部好友 */
    private void loadFriends() {
        friendList = friendDao.queryAll();
        FriendAdapter adapter = new FriendAdapter(requireContext(), friendList);
        lvFriends.setAdapter(adapter);
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

    /** 搜索：按名字模糊查询数据库 */
    private void setupSearch(View view) {
        if (etSearch == null) return;
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                String keyword = s.toString().trim();
                List<Friend> result;
                if (keyword.isEmpty()) {
                    result = friendDao.queryAll();
                } else {
                    result = friendDao.searchByName(keyword);
                }
                friendList = result;
                lvFriends.setAdapter(new FriendAdapter(requireContext(), result));
            }
        });
    }
}