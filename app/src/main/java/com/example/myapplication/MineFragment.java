package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class MineFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView ivMyAvatar = view.findViewById(R.id.ivMyAvatar);
        TextView tvMyName = view.findViewById(R.id.tvMyName);
        TextView tvMyEmail = view.findViewById(R.id.tvMyEmail);
        CustomButton btnLogout = view.findViewById(R.id.btnLogoutMine);

        SharedPreferences sp = requireContext()
                .getSharedPreferences("login_prefs", 0);
        String username = sp.getString("username", "游客");
        int avatarIndex = sp.getInt("avatar_index", 0);
        int[] avatarRes = {R.drawable.avatar1, R.drawable.avatar2,
                R.drawable.avatar3, R.drawable.avatar4};
        ivMyAvatar.setImageResource(avatarRes[Math.max(0, Math.min(avatarIndex, 3))]);
        tvMyName.setText(username);
        tvMyEmail.setText(username + "@mail.com");

        // C7: 退出登录二次确认
        btnLogout.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确定", (d, w) -> {
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().overridePendingTransition(
                            android.R.anim.fade_in, android.R.anim.fade_out);
                })
                .setNegativeButton("取消", null)
                .show());
    }
}