package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.db.FriendDao;
import com.example.myapplication.db.MessageDao;
import com.example.myapplication.db.UserDao;
import com.example.myapplication.view.BarChartView;
import com.example.myapplication.view.PieChartView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChartFragment extends Fragment {

    private PieChartView pieChart;
    private BarChartView barChart;
    private TextView tvSummary;

    private MessageDao messageDao;
    private FriendDao friendDao;
    private UserDao userDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        messageDao = new MessageDao(requireContext());
        friendDao = new FriendDao(requireContext());
        userDao = new UserDao(requireContext());

        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);
        tvSummary = view.findViewById(R.id.tvSummary);

        loadCharts();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pieChart != null) loadCharts();
    }

    private void loadCharts() {
        int sent = messageDao.countSentByMe();
        int received = messageDao.countReceived();
        int total = sent + received;
        int friendTotal = friendDao.getTotalCount();
        int userTotal = userDao.getTotalCount();

        // ===== 饼图：消息收发比例 =====
        List<PieChartView.Slice> slices = new ArrayList<>();
        if (total == 0) {
            slices.add(new PieChartView.Slice("无", 1, Color.parseColor("#E0E0E0")));
        } else {
            if (sent > 0) {
                slices.add(new PieChartView.Slice("发出", sent, Color.parseColor("#4CAF50")));
            }
            if (received > 0) {
                slices.add(new PieChartView.Slice("收到", received, Color.parseColor("#2196F3")));
            }
        }
        pieChart.setData(slices);

        // ===== 柱状图：最近 7 天每日消息数 =====
        int[] daily = messageDao.getDailyMessageCount(7);
        String[] labels = getLast7DayLabels();

        List<BarChartView.Bar> bars = new ArrayList<>();
        int[] colors = {
                Color.parseColor("#90A4AE"), Color.parseColor("#78909C"),
                Color.parseColor("#607D8B"), Color.parseColor("#546E7A"),
                Color.parseColor("#4CAF50"), Color.parseColor("#FFA500"),
                Color.parseColor("#FF6B6B")
        };
        for (int i = 0; i < 7; i++) {
            bars.add(new BarChartView.Bar(labels[i], daily[i], colors[i]));
        }
        barChart.setData(bars);

        // ===== 顶部摘要 =====
        tvSummary.setText(
                "消息 " + total +
                        "  |  好友 " + friendTotal +
                        "  |  用户 " + userTotal);
    }

    private String[] getLast7DayLabels() {
        String[] week = {"日", "一", "二", "三", "四", "五", "六"};
        String[] result = new String[7];
        for (int i = 0; i < 7; i++) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_YEAR, -(6 - i));
            result[i] = "周" + week[c.get(Calendar.DAY_OF_WEEK) - 1];
        }
        result[6] = "今天";
        return result;
    }
}