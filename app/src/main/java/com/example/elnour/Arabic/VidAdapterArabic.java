package com.example.elnour.Arabic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.elnour.ItemClickListener;
import com.example.elnour.R;

import java.util.List;

public class VidAdapterArabic extends RecyclerView.Adapter<VidAdapterArabic.VideoViewHolder> {

    private Context context;
    private List<FileModelsArabic> videoList;

    public VidAdapterArabic(Context context, List<FileModelsArabic> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_list, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        FileModelsArabic video = videoList.get(position);
        holder.videoTitle.setText(video.getTitle());
        holder.videoPrice.setText(String.valueOf(video.getPrice()));

        // Load video thumbnail using Glide
        Glide.with(context)
                .load(video.getVideourl())
                .into(holder.videoThumbnail);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getVideourl()));
            intent.setDataAndType(Uri.parse(video.getVideourl()), "video/*");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {

    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;
        TextView videoTitle;
        TextView videoPrice;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.list_item_images);
            videoTitle = itemView.findViewById(R.id.list_item_titles);
            videoPrice = itemView.findViewById(R.id.video_prices);
        }
    }
}
