package com.example.test1.fragment;

// 导入Gson库用于JSON解析
import com.example.test1.activity.WebAcitivity;
import com.google.gson.Gson;

import android.os.Bundle;
import android.util.Log;
// 导入支持库
import androidx.annotation.NonNull;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// 导入自定义库
import com.example.test1.R;
import com.example.test1.adpter.NewsAdapter;
import com.example.test1.entity.NewsEntity;
import com.example.test1.entity.NewsResponse;
import com.scwang.smart.refresh.footer.BallPulseFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

// 导入JSON处理库
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// 导入网络请求库
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 代表新闻列表的Fragment。
 */
public class NewsFragment extends BaseFragment {
    // RecyclerView用于显示新闻列表
    private RecyclerView recyclerView;
    // RefreshLayout用于实现下拉刷新功能
    private RefreshLayout refreshLayout;
    // 垂直方向的LinearLayoutManager
    protected LinearLayoutManager mLinearLayoutManager;
    // 存储新闻数据的列表
    private List<NewsEntity> datas = new ArrayList<>();
    // 绑定新闻数据到RecyclerView的适配器
    private NewsAdapter newsAdapter;

    /**
     * 创建新的NewsFragment实例。
     *
     * @return 新的NewsFragment实例。
     */
    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    @Override
    protected int initLayout() {
        // 返回布局文件ID
        return R.layout.fragment_news;
    }

    @Override
    protected void initView() {
        // 初始化RecyclerView
        recyclerView = mRootView.findViewById(R.id.recyclerView);
        // 初始化RefreshLayout
        refreshLayout = mRootView.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new MaterialHeader(getContext()));
        refreshLayout.setRefreshFooter(new BallPulseFooter(getContext()));
    }

    @Override
    protected void initData() {
        // 设置RecyclerView的布局管理器为垂直方向的LinearLayoutManager
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManager);

        // 初始化数据
        init(true);
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // 开始刷新时的操作
            refreshLayout.finishRefresh();
            datas.clear();  // 刷新时清空数据列表
            init(true);  // 重新加载数据
        });

        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // 加载更多时的操作
            refreshLayout.finishLoadMore();

            init(true);  // 加载更多数据
        });
    }

    /**
     * 获取新闻数据并更新UI。
     *
     * @param isRefresh 是否为刷新操作。
     */
    public void init(final boolean isRefresh) {
        // 创建OkHttpClient实例
        OkHttpClient client = new OkHttpClient();
        // 构建请求
        Request request = new Request.Builder()
                .url("http://110.41.60.211:8080/news/list") // 设置URL
                .addHeader("Content-Type", "application/json;charset=UTF-8") // 添加请求头
                .get() // 设置为GET请求
                .build();
        // 发送请求
        Call call = client.newCall(request);
        call.enqueue(new Callback() { // 异步执行请求
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 请求失败时调用
                handleFailure(isRefresh, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // 请求成功时调用
                if (!response.isSuccessful()) {
                    // 如果响应不成功则记录错误日志
                    Log.e("NewsFragment", "响应失败: " + response.code());
                    return;
                }
                // 解析响应体为NewsResponse对象
                NewsResponse result = new Gson().fromJson(response.body().string(), NewsResponse.class);
                // 获取新闻数据列表
                List<NewsEntity> list = result.getData();
                if (list != null && !list.isEmpty()) {
                    // 如果是刷新操作则清空数据列表
                    if (isRefresh) {
                        datas.clear();
                        datas.addAll(list);
                    } else {
                        // 否则追加数据
                        datas.addAll(list);
                    }
                }
                // 关闭响应体
                response.body().close();
                // 在主线程中更新UI
                requireActivity().runOnUiThread(() -> {
                    if (newsAdapter == null) {
                        // 如果适配器为空则创建并设置适配器
                        newsAdapter = new NewsAdapter(getContext(), datas);
                        recyclerView.setAdapter(newsAdapter);
                        newsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Serializable obj) {
//                                NewsEntity newsEntity = (NewsEntity) obj;
                                // 跳转到新闻详情页
//                                String url = "http://baidu.com"+newsEntity.getAuthorName();
                                String url = "http://blog.csdn.net/m0_57203176/article/details/140783078?spm=1001.2014.3001.5502";
                                //这里真正实现才需要写入，目前就只是不带参的地址，模拟
                                Bundle bundle = new Bundle();
                                bundle.putString("url",url);
                               navigateToWithBundle(WebAcitivity.class,bundle);
                            }
                        });

                    } else {
                        newsAdapter.notifyDataSetChanged(); // 如果适配器已存在，则更新数据并通知适配器
                    }
                    // 结束刷新
                    refreshLayout.finishRefresh();
                });
            }
        });
    }

    /**
     * 处理请求失败的情况。
     *
     * @param isRefresh 是否为刷新操作。
     * @param throwable 抛出的异常。
     */
    private void handleFailure(boolean isRefresh, Throwable throwable) {
        if (isRefresh) {
            // 如果是刷新操作则结束刷新
            refreshLayout.finishRefresh(true);
        } else {
            // 否则结束加载更多
            refreshLayout.finishLoadMore(true);
        }
        // 显示错误提示
        showToast("错误: " + throwable.getMessage());
    }
}
