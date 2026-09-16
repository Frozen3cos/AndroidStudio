package com.example.myapplication;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // 缓存三个 Fragment 实例（只创建一次）
    private FriendFragment friendFragment;
    private MessageFragment messageFragment;
    private MineFragment mineFragment;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#4CAF50"));
        }
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottomNav);

        // 初始化三个 Fragment 实例
        friendFragment = new FriendFragment();
        messageFragment = new MessageFragment();
        mineFragment = new MineFragment();

        // 默认显示好友
        if (savedInstanceState == null) {
            currentFragment = friendFragment;
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, friendFragment)
                    .commit();
            nav.setSelectedItemId(R.id.nav_friend);
        }

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment target;
            if (id == R.id.nav_friend) {
                target = friendFragment;
            } else if (id == R.id.nav_message) {
                target = messageFragment;
            } else {
                target = mineFragment;
            }
            switchFragment(target);
            return true;
        });
    }

    /**
     * 优化后的切换：用 hide/show 而不是 replace，不销毁 Fragment
     */
    private void switchFragment(Fragment target) {
        if (target == currentFragment) return;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // 隐藏当前 Fragment（不销毁）
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }
        // 若目标 Fragment 已经添加过就 show，否则 add
        if (target.isAdded()) {
            transaction.show(target);
        } else {
            transaction.add(R.id.fragmentContainer, target);
        }
        transaction.commit();
        currentFragment = target;
    }
}