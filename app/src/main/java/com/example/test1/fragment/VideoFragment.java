package com.example.test1.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.test1.R;
import com.example.test1.adpter.VideoAdapter;
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

public class VideoFragment extends Fragment implements OnItemChildClickListener {
    private String title;
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    private List<VideoEntity> videoList = new ArrayList<>();
    protected VideoAdapter videoAdapter;
    private int pageNum;
    private int pageSize;
    protected VideoView mVideoView;
    protected StandardVideoController mController;
    protected ErrorView mErrorView;
    protected CompleteView mCompleteView;
    protected TitleView mTitleView;
    public int mPosition;
    private int categoryId;

    protected LinearLayoutManager mLinearLayoutManager;
    protected int mCurPos = -1;
    /**
     * 上次播放的位置，用于页面切回来之后恢复播放
     */
    protected int mLastPos = mCurPos;
    public VideoFragment() {
    }

    public static VideoFragment newInstance(int categoryId) {
        VideoFragment fragment = new VideoFragment();
        fragment.categoryId = categoryId;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);  // 初始化RecyclerView
        refreshLayout = v.findViewById(R.id.refreshLayout);  // 初始化RefreshLayout
        initVideoView();
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        refreshLayout.setRefreshHeader(new MaterialHeader(getContext()));
        refreshLayout.setRefreshFooter(new BallPulseFooter(getContext()));
        pageNum = 1;
        pageSize = 5;
        init();
        return v;
    }
    protected void initVideoView() {
        mVideoView = new VideoView(getActivity());
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
        mController = new StandardVideoController(getActivity());
        mErrorView = new ErrorView(getActivity());
        mController.addControlComponent(mErrorView);
        mCompleteView = new CompleteView(getActivity());
        mController.addControlComponent(mCompleteView);
        mTitleView = new TitleView(getActivity());
        mController.addControlComponent(mTitleView);
        mController.addControlComponent(new VodControlView(getActivity()));
        mController.addControlComponent(new GestureView(getActivity()));
        mController.setEnableOrientation(true);
        mVideoView.setVideoController(mController);
    }

    public void init() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url("http://110.41.60.211:8080/video/getCategoryById" + "?pageNum=" + pageNum + "&pageSize=" + pageSize+"&categoryId="+categoryId)
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
                    JSONObject dataObj = jsonObject.getJSONObject("data");
                    JSONArray dataArray = dataObj.getJSONArray("list");
                    Log.d("VideoFragment", "响应成功: " + dataArray);
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
                    response.body().close();
                    requireActivity().runOnUiThread(() -> {
                        if (videoAdapter == null) {
                            videoAdapter = new VideoAdapter(getContext(), videoList);
                            videoAdapter.setOnItemChildClickListener(VideoFragment.this);
                            recyclerView.setAdapter(videoAdapter);
                            recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                                @Override
                                public void onChildViewAttachedToWindow(@NonNull View view) {

                                }

                                @Override
                                public void onChildViewDetachedFromWindow(@NonNull View view) {
                                    FrameLayout playerContainer = view.findViewById(R.id.player_container);
                                    View v = playerContainer.getChildAt(0);
                                    if (v != null && v == mVideoView && !mVideoView.isFullScreen()) {
                                        releaseVideoView();
                                    }
                                }
                            });
                        } else {
                            videoAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    Log.e("VideoFragment", "JSON解析错误", e);
                }
            }

        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState, int categoryId) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh();
            pageNum = 1;
            pageSize = 5;
            videoList.clear();  // 刷新时清空数据列表
            init();
             // 传入false表示刷新失败
        });
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            refreshLayout.finishLoadMore();
            pageNum++;
            pageSize = 5;
            init();
           // 传入false表示加载失败
        });
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
        Log.d("VideoFragment", "startPlay: " + mLinearLayoutManager.findViewByPosition(position));
        if (itemView == null) return;
        VideoAdapter.ViewHoler viewHolder = (VideoAdapter.ViewHoler) itemView.getTag();
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
        if(getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
    public void setPosition(int newPosition) {
        this.mPosition = newPosition;
        init(); // 这里重新请求数据
    }
}
