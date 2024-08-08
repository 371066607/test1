package com.example.test1.adpter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.example.test1.entity.VideoEntity;
import com.example.test1.listener.OnItemChildClickListener;
import com.example.test1.listener.OnItemClickListener;
import com.example.test1.view.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videocontroller.component.PrepareView;

public class CollectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private Context mContext;
    private List<VideoEntity> datas;
    private OnItemChildClickListener mOnItemChildClickListener;

    private OnItemClickListener mOnItemClickListener;
    public void setDatas(List<VideoEntity> datas) {
        this.datas = datas;
    }

    public CollectAdapter(Context context, List<VideoEntity> datas){
        this.mContext=context;
        this.datas=datas;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(mContext).inflate(R.layout.item_collect_layout,parent,false);
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
                .into(vh.imgCover2);
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
        private ImageView imgHeader;
        public PrepareView imgCover;
        private ImageView imgCover2;
        public int mPosition;
//        public PrepareView mPrepareView;
        public FrameLayout mPlayerContainer;

        public ViewHoler(@NonNull View view) {
            super(view);
            tvTitle = view.findViewById(R.id.title1);
            author = view.findViewById(R.id.author1);
            dzCount = view.findViewById(R.id.dz1);
            collectCount = view.findViewById(R.id.collect1);
            commentCount = view.findViewById(R.id.comment1);
            imgHeader = view.findViewById(R.id.img_header1);
            imgCover = view.findViewById(R.id.img_cover1);
            imgCover2 = view.findViewById(R.id.img_cover2);
            mPlayerContainer = view.findViewById(R.id.player_container1);
            if (mOnItemChildClickListener != null) {
                mPlayerContainer.setOnClickListener(this);
            }
            if (mOnItemClickListener != null) {
                view.setOnClickListener(this);
            }
            //通过tag将ViewHolder和itemView绑定
            view.setTag(this);
        }
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.player_container1) {
                imgCover2.setVisibility(View.GONE);
                if (mOnItemChildClickListener != null) {
                    mOnItemChildClickListener.onItemChildClick(mPosition);
                }
            } else {
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
