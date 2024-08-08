package com.example.test1.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.test1.R;
import com.example.test1.adpter.HomeAdapter;
import com.example.test1.adpter.VideoAdapter;
import com.example.test1.entity.Category;
import com.example.test1.entity.VideoEntity;
import com.flyco.tablayout.SlidingTabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment{
HashMap<Integer,String> mTitles = new HashMap<>();

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    public HomeFragment() {
        // Required empty public constructor
    }

    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        viewPager = v.findViewById(R.id.viewPager);
        slidingTabLayout = v.findViewById(R.id.slidingTabLayout);
        init();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    public void init() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url("http://110.41.60.211:8080/video/categoryList")
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("VideoFragment", "网络请求失败", e);
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "网络请求失败", Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("VideoFragment", "响应失败: " + response.code());
                    return;
                }
                String result = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray dataArray = jsonObject.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++){
                        JSONObject category = dataArray.getJSONObject(i);
                        Integer categoryId = Integer.valueOf(category.getString("categoryId"));
                        String categoryName = category.getString("categoryName");
                        mTitles.put(categoryId, categoryName);
                        mFragments.add(VideoFragment.newInstance(categoryId));
                    }

                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setOffscreenPageLimit(mFragments.size());
                            viewPager.setAdapter(new HomeAdapter(getChildFragmentManager(), mTitles, mFragments));
                            slidingTabLayout.setViewPager(viewPager);
                        }
                    });
                    }catch (JSONException e) {
                    Log.e("VideoFragment", "JSON解析错误", e);
                }
                    response.body().close();
                }
        });
    }
}