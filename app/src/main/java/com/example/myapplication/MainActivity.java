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

    private FriendFragment friendFragment;
    private MessageFragment messageFragment;
    private ChartFragment chartFragment;
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

        friendFragment = new FriendFragment();
        messageFragment = new MessageFragment();
        chartFragment = new ChartFragment();
        mineFragment = new MineFragment();

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
            } else if (id == R.id.nav_chart) {
                target = chartFragment;
            } else {
                target = mineFragment;
            }
            switchFragment(target);
            return true;
        });
    }

    private void switchFragment(Fragment target) {
        if (target == currentFragment) return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) transaction.hide(currentFragment);
        if (target.isAdded()) {
            transaction.show(target);
        } else {
            transaction.add(R.id.fragmentContainer, target);
        }
        transaction.commit();
        currentFragment = target;
    }
}