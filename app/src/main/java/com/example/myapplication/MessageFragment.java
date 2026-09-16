package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView lvMessages = view.findViewById(R.id.lvMessages);
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("小明", R.drawable.avatar1, "晚上一起吃饭吗？", "10:25"));
        messages.add(new Message("小红", R.drawable.avatar2, "作业写完了吗？", "09:48"));
        messages.add(new Message("小刚", R.drawable.avatar3, "明早七点操场见", "昨天"));
        messages.add(new Message("小美", R.drawable.avatar4, "新店打卡推荐给你~", "昨天"));
        messages.add(new Message("阿杰", R.drawable.avatar1, "歌单已发你邮箱", "周一"));
        messages.add(new Message("莉莉", R.drawable.avatar2, "照片我修好了", "周日"));

        MessageAdapter adapter = new MessageAdapter(requireContext(), messages);
        lvMessages.setAdapter(adapter);
        lvMessages.setEmptyView(view.findViewById(R.id.tvMsgEmpty));
    }
}