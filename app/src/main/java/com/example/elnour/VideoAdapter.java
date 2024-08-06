package com.example.elnour;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<Video> videoList;
    private Context context;
    private List<Video> selectedVideos = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Video video);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public VideoAdapter(List<Video> videoList, Context context) {
        this.videoList = videoList != null ? videoList : new ArrayList<>();
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

        if (video.getVideoName() != null) {
            holder.videoName.setText(video.getVideoName());
        } else {
            holder.videoName.setText("No name");
        }

        if (video.getPrice() != null) {
            holder.videoPrice.setText("السعر: " + video.getPrice() + " EGP");
        } else {
            holder.videoPrice.setText("السعر: N/A");
        }

        if (video.getSubject() != null) {
            holder.videoSubject.setText(video.getSubject());
        } else {
            holder.videoSubject.setText("No subject");
        }

        if (video.getGradeYear() != null) {
            holder.videoGradeYear.setText(video.getGradeYear());
        } else {
            holder.videoGradeYear.setText("No grade year");
        }

        Glide.with(context)
                .load(video.getVideoUrl())
                .placeholder(R.drawable.elnour)
                .into(holder.videoThumbnail);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(video);
            }
        });

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedVideos.contains(video));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (selectedVideos.size() < 8) {
                    selectedVideos.add(video);
                } else {
                    Toast.makeText(context, "يمكنك تحديد 8 فيديوهات فقط", Toast.LENGTH_SHORT).show();
                    holder.checkBox.setChecked(false);
                }
            } else {
                selectedVideos.remove(video);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public List<Video> getSelectedVideos() {
        return selectedVideos;
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        public TextView videoName, videoPrice, videoSubject, videoGradeYear;
        public ImageView videoThumbnail;
        public CheckBox checkBox;

        public VideoViewHolder(View itemView) {
            super(itemView);
            videoName = itemView.findViewById(R.id.video_name);
            videoPrice = itemView.findViewById(R.id.video_price);
            videoSubject = itemView.findViewById(R.id.video_subject);
            videoGradeYear = itemView.findViewById(R.id.video_grade_year);
            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
