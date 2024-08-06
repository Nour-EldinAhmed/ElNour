package com.example.elnour;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elnour.Biology.FileModelsBiology;

import java.util.List;
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {
    private List<FileModelsBiology> files;
    private Context context;

    public FileAdapter(Context context, List<FileModelsBiology> files) {
        this.context = context;
        this.files = files;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        FileModelsBiology file = files.get(position);
       // holder.fileName.setText(file.getName());
      //  Glide.with(context).load(file.getUrl()).into(holder.fileIcon);
/*
        holder.itemView.setOnClickListener(v -> {
            downloadFile(holder.fileName.getContext(),files.get(position).getName(),".pdf",DIRECTORY_DOWNLOADS,files.get(position).getUrl());
         //   downloadFile(holder.fileName.getContext(),files.get(position).getName(),"*",DIRECTORY_DOWNLOADS,files.get(position).getUrl());
            });*/
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        ImageView fileIcon;

        public FileViewHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            fileIcon = itemView.findViewById(R.id.file_icon);
        }
    }
    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {

        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadmanager.enqueue(request);
    }
}
