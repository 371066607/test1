package com.example.test1.activity;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.example.test1.adpter.CollectAdapter;
import com.example.test1.adpter.VideoAdapter;
import com.example.test1.databinding.ActivityCollectBinding;
import com.example.test1.entity.VideoEntity;
import com.example.test1.listener.OnItemChildClickListener;
import com.example.test1.util.Tag;
import com.example.test1.util.Utils;
import com.scwang.smart.refresh.footer.BallPulseFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videocontroller.component.CompleteView;
import xyz.doikki.videocontroller.component.ErrorView;
import xyz.doikki.videocontroller.component.GestureView;
import xyz.doikki.videocontroller.component.TitleView;
import xyz.doikki.videocontroller.component.VodControlView;
import xyz.doikki.videoplayer.controller.IControlComponent;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.player.VideoViewManager;

public class CollectActivity extends BaseActivity  implements OnItemChildClickListener {

    private ActivityCollectBinding binding;
    private List<VideoEntity> videoList = new ArrayList<>();
    protected CollectAdapter collectAdapter;
    protected VideoView mVideoView;
    protected StandardVideoController mController;
    protected ErrorView mErrorView;
    protected CompleteView mCompleteView;
    protected TitleView mTitleView;
    protected LinearLayoutManager mLinearLayoutManager;
    private int pageNum;
    private int pageSize;
    protected int mCurPos = -1;
    /**
     * 上次播放的位置，用于页面切回来之后恢复播放
     */
    protected int mLastPos = mCurPos;
    /**
     * 上次播放的位置，用于页面切回来之后恢复播放
     */
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    @Override
    protected void initView() {

        initVideoView();
        recyclerView = findViewById(R.id.recyclerView);  // 初始化RecyclerView
        refreshLayout = findViewById(R.id.refreshLayout);
        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        refreshLayout.setRefreshHeader(new MaterialHeader(this));
        refreshLayout.setRefreshFooter(new BallPulseFooter(this));
    }

    @Override
    protected void initData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

// 从 SharedPreferences 中读取数据
        String userId = sharedPreferences.getString("userId", "默认值");
        init(userId);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_collect;
    }

    protected void initVideoView() {
        mVideoView = new VideoView(this);
        mVideoView.setOnStateChangeListener(new VideoView.SimpleOnStateChangeListener() {
            @Override
            public void onPlayStateChanged(int playState) {
                //监听VideoViewManager释放，重置状态
                if (playState == VideoView.STATE_IDLE) {
                    Utils.removeViewFormParent(mVideoView);
                    mLastPos = mCurPos;
                    mCurPos = -1;
                }
            }
        });
        mController = new StandardVideoController(this);
        mErrorView = new ErrorView(this);
        mController.addControlComponent(mErrorView);
        mCompleteView = new CompleteView(this);
        mController.addControlComponent(mCompleteView);
        mTitleView = new TitleView(this);
        mController.addControlComponent(mTitleView);
        mController.addControlComponent(new VodControlView(this));
        mController.addControlComponent(new GestureView(this));
        mController.setEnableOrientation(true);
        mVideoView.setVideoController(mController);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState, int position) {

    }

    @Override
    public void onItemChildClick(int position) {
        startPlay(position);
    }

    /**
     * 开始播放
     * @param position 列表位置
     */
    protected void startPlay(int position) {

        if (mCurPos == position) return;
        if (mCurPos != -1) {
            releaseVideoView();
        }
        VideoEntity videoEntity = videoList.get(position);
        mVideoView.setUrl(videoEntity.getPlayUrl());
        mTitleView.setTitle(videoEntity.getTitle());
        View itemView = mLinearLayoutManager.findViewByPosition(position);
        if (itemView == null) return;
        CollectAdapter.ViewHoler viewHolder = (CollectAdapter.ViewHoler) itemView.getTag();
        //把列表中预置的PrepareView添加到控制器中，注意isDissociate此处只能为true, 请点进去看isDissociate的解释
        mController.addControlComponent((IControlComponent) viewHolder.imgCover, true);
        Utils.removeViewFormParent(mVideoView);
        viewHolder.mPlayerContainer.addView(mVideoView, 0);
        //播放之前将VideoView添加到VideoViewManager以便在别的页面也能操作它
        getVideoViewManager().add(mVideoView, Tag.LIST);
        mVideoView.start();
        mCurPos = position;

    }
    private void releaseVideoView() {
        mVideoView.release();
        if (mVideoView.isFullScreen()) {
            mVideoView.stopFullScreen();
        }
        if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mCurPos = -1;
    }
    @Override
    public void onPause() {
        super.onPause();
        pause();
    }
    protected void pause() {
        releaseVideoView();
    }

    @Override
    public void onResume() {
        super.onResume();
        resume();
    }
    protected void resume() {
        if (mLastPos == -1)
            return;
        //恢复上次播放的位置
        startPlay(mLastPos);
    }
    protected VideoViewManager getVideoViewManager() {
        return VideoViewManager.instance();
    }
    public void init(String userId) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url("http://110.41.60.211:8080/video/getCollectVideo?"+"&userId="+userId)
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("VideoFragment", "网络请求失败", e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    return;
                }

                String result = response.body().string();
                try {

                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject dataObj = jsonObject.getJSONObject("data");
                    String total= dataObj.getString("total");
                    if (total.equals("0")) {
                        runOnUiThread(() -> {
                            showToast("暂无收藏视频");
                        });
                        response.body().close();
                        return;
                    }
                    JSONArray dataArray = dataObj.getJSONArray("list");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataItem = dataArray.getJSONObject(i);
                        VideoEntity videoEntity = new VideoEntity(
                                dataItem.getInt("vid"),
                                dataItem.getString("vtitle"),
                                dataItem.getString("author"),
                                dataItem.getInt("collectNum"),
                                dataItem.getInt("likeNum"),
                                dataItem.getInt("commentNum"),
                                dataItem.getString("headUrl"),
                                dataItem.getString("coverUrl"),
                                dataItem.getInt("categoryId"),
                                dataItem.getString("playUrl")
                        );

                        videoList.add(videoEntity);

                    }

                    runOnUiThread(() -> {
                        if (collectAdapter == null) {
                            collectAdapter = new CollectAdapter(CollectActivity.this,videoList);
                            collectAdapter.setOnItemChildClickListener(CollectActivity.this);
                            recyclerView.setAdapter(collectAdapter);
                        } else {
                            collectAdapter.notifyDataSetChanged();
                        }
                    });
                    response.body().close();
                } catch (JSONException e) {
                    Log.e("VideoFragment", "JSON解析错误", e);
                }
            }

        });
    }

}