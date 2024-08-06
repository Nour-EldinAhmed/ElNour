package com.example.elnour;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.elnour.Video;

import java.util.List;

public class VideoAdapterLists extends RecyclerView.Adapter<VideoAdapterLists.VideoViewHolder> {

    private List<Video> videoList;
    private Context context;

    public VideoAdapterLists(List<Video> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoList.get(position);
        holder.videoName.setText(video.getVideoName());
        holder.videoPrice.setText(video.getPrice());
        holder.videoSubject.setText(video.getSubject());
        holder.videoGradeYear.setText(video.getGradeYear());

        // تحميل صورة الفيديو باستخدام Glide
        Glide.with(context)
                .load(video.getVideoUrl())  // يمكنك استخدام صورة مصغرة للفيديو هنا بدلاً من رابط الفيديو الكامل
                .placeholder(R.drawable.elnour)
                .into(holder.videoThumbnail);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra("videoUrl", video.getVideoUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        public TextView videoName, videoPrice, videoSubject, videoGradeYear;
        public ImageView videoThumbnail;

        public VideoViewHolder(View itemView) {
            super(itemView);
            videoName = itemView.findViewById(R.id.video_name);
            videoPrice = itemView.findViewById(R.id.video_price);
            videoSubject = itemView.findViewById(R.id.video_subject);
            videoGradeYear = itemView.findViewById(R.id.video_grade_year);
            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
        }
    }
}
