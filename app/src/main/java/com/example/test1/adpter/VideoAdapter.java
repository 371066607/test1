package com.example.test1.adpter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.example.test1.entity.VideoEntity;
import com.example.test1.fragment.VideoFragment;
import com.example.test1.listener.OnItemChildClickListener;
import com.example.test1.listener.OnItemClickListener;
import com.example.test1.view.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videocontroller.component.PrepareView;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private Context mContext;
    private List<VideoEntity> datas;
    // ...
//    private JSONArray jsonArray;
    private OnItemChildClickListener mOnItemChildClickListener;

    private OnItemClickListener mOnItemClickListener;
    public void setDatas(List<VideoEntity> datas) {
        this.datas = datas;
    }

    public VideoAdapter(Context context, List<VideoEntity> datas){
        this.mContext=context;
        this.datas=datas;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(mContext).inflate(R.layout.item_video_layout,parent,false);
        return new ViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ViewHoler vh = (ViewHoler) holder;
        VideoEntity videoEntity = datas.get(position);
        vh.tvTitle.setText(videoEntity.getTitle());
        vh.author.setText(videoEntity.getName());
        vh.dzCount.setText(String.valueOf(videoEntity.getDzCount()));
        vh.collectCount.setText(String.valueOf(videoEntity.getCollectCount()));
        vh.commentCount.setText(String.valueOf(videoEntity.getCommentCOUNT()));
        Picasso.get().load(videoEntity.getHeadUrl())
                .transform(new CircleTransform())
                .into(vh.imgHeader);

        Picasso.get().load(videoEntity.getImgCover())
                .into(vh.imgCover1);

        vh.mPosition = position;

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }



    public class ViewHoler extends RecyclerView.ViewHolder implements View.OnClickListener{

        private StandardVideoController mVideoController;
        private TextView tvTitle;
        private TextView author;
        private TextView dzCount;
        private TextView collectCount;
        private TextView commentCount;
        private ImageView imgCover1;
        private ImageView imgHeader;
        public PrepareView imgCover;
        public int mPosition;
//        public PrepareView mPrepareView;
        public FrameLayout mPlayerContainer;
        private ImageView imgCollect;
        // 声明一个变量来记录当前的状态
        private boolean isCollected = false;

        private boolean isColored() {
            return !isCollected;
        }

        public ViewHoler(@NonNull View view) {
            super(view);
            imgCollect = view.findViewById(R.id.img_collect);
            tvTitle = view.findViewById(R.id.title);
            author = view.findViewById(R.id.author);
            dzCount = view.findViewById(R.id.dz);
            collectCount = view.findViewById(R.id.collect);
            commentCount = view.findViewById(R.id.comment);
            imgHeader = view.findViewById(R.id.img_header);
            imgCover = view.findViewById(R.id.img_cover);
            imgCover1= view.findViewById(R.id.img_cover1);
            mPlayerContainer = view.findViewById(R.id.player_container);
            if (mOnItemChildClickListener != null) {
                mPlayerContainer.setOnClickListener(this);
            }
            if (mOnItemClickListener != null) {
                view.setOnClickListener(this);
            }
            //通过tag将ViewHolder和itemView绑定
            view.setTag(this);
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("UserPrefs", mContext.MODE_PRIVATE);

// 从 SharedPreferences 中读取数据
            String userId = sharedPreferences.getString("userId", "默认值");
            imgCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OkHttpClient client = new OkHttpClient.Builder().build();
                    Request request = new Request.Builder()
                            .url("http://110.41.60.211:8080/video/addCllectVid"+"?userId="+userId+"&vid="+datas.get(mPosition).getId())
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
                                Log.e("VideoFragment", "响应失败: " + request);
                                return;
                            }
                            String result = response.body().string();
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                JSONObject dataObj = jsonObject.getJSONObject("data");
                                JSONArray dataArray = dataObj.getJSONArray("list");
                                Log.d("VideoFragment", "响应成功: " + dataArray);
                                response.body().close();

                            } catch (JSONException e) {
                                Log.e("VideoFragment", "JSON解析错误", e);
                            }
                        }

                    });
                    // 改变颜色的逻辑
                    if (isColored()) {
                        imgCollect.setBackgroundResource(R.drawable.collect_select); // 变为红色
                        collectCount.setText(String.valueOf(Integer.parseInt(collectCount.getText().toString())+1));
                        isCollected = true;
                    } else {
                        imgCollect.setBackgroundResource(R.drawable.collect);  // 恢复原色
                        collectCount.setText(String.valueOf(Integer.parseInt(collectCount.getText().toString())-1));
                        isCollected = false;
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.player_container) {
                imgCover1.setVisibility(View.GONE);
                if (mOnItemChildClickListener != null) {
                    mOnItemChildClickListener.onItemChildClick(mPosition);
                }
            }else {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mPosition);
                }
            }

        }

    }

    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {

        mOnItemChildClickListener = onItemChildClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
