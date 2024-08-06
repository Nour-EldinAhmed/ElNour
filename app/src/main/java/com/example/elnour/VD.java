package com.example.elnour;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class VD extends RecyclerView.Adapter<VD.VideoViewHolder> {
    private Context context;
    private ArrayList<Models> videoList;
   public ItemClickListener onItemClickListener;

    public VD(Context context, ArrayList<Models> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Models fileModel = videoList.get(position);

        holder.videoTitle.setText(fileModel.getTitle());
        holder.videoPrice.setText(fileModel.getPrice());
        Glide.with(context).load(fileModel.getVideourl()).into(holder.videoThumbnail);

        holder.itemView.setOnClickListener(v -> {
            if (fileModel.getVideourl() != null && !fileModel.getVideourl().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileModel.getVideourl()));
                intent.setDataAndType(Uri.parse(fileModel.getVideourl()), "video/*");
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Video URL is invalid", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public  class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView videoTitle, videoPrice;
        ImageView videoThumbnail;
        FileModel m;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
          /*  videoTitle = itemView.findViewById(R.id.video_title);
            videoPrice = itemView.findViewById(R.id.video_price);
            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
            itemView.setOnClickListener(V->{
                onItemClickListener.ItemClcik(m);
            });
        */}
    }
}
