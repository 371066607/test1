package com.example.test1.fragment;
import android.content.SharedPreferences;

import com.example.test1.R;
import com.example.test1.activity.CollectActivity;
import com.example.test1.activity.LoginActivity;
import com.example.test1.databinding.FragmentMyBinding;

public class MyFragment extends BaseFragment {

    private FragmentMyBinding binding;

    public MyFragment() {
    }

    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        return fragment;
    }
    @Override
    protected int initLayout() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initView() {
        binding = FragmentMyBinding.bind(mRootView);
        //退出登录
        binding.rlLogout.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo", 0);
            // 创建 Editor 对象
            SharedPreferences.Editor editor = sharedPreferences.edit();
            // 移除 userId 键值对
            editor.remove("userId");
            // 应用更改
            editor.apply();
            navigateTo(LoginActivity.class);
        });
        binding.rlCollect.setOnClickListener(v -> {
            navigateTo(CollectActivity.class);
        });
    }

    @Override
    protected void initData() {

    }

}