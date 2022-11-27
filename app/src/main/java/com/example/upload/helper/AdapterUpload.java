package com.example.upload.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.upload.R;
import com.example.upload.model.DataUpload;

import java.util.ArrayList;

public class AdapterUpload extends RecyclerView.Adapter<AdapterUpload.UploadViewHolder> {
    Context context;
    ArrayList<DataUpload> uploadArrayList;

    public AdapterUpload(Context context, ArrayList<DataUpload> uploadArrayList) {
        this.context = context;
        this.uploadArrayList = uploadArrayList;
    }

    @NonNull
    @Override
    public AdapterUpload.UploadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_data, parent, false);
        return new UploadViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUpload.UploadViewHolder holder, int position) {
        holder.ViewBind(uploadArrayList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return uploadArrayList.size();
    }

    public class UploadViewHolder extends RecyclerView.ViewHolder {
        ImageView image_view;
        TextView tv_title, tv_description;
        public UploadViewHolder(@NonNull View itemView) {
            super(itemView);

            image_view = itemView.findViewById(R.id.image_view);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_description = itemView.findViewById(R.id.tv_description);
        }

        public void ViewBind(DataUpload dataUpload, int position) {
            tv_title.setText(dataUpload.getTitle());
            tv_description.setText(dataUpload.getDescription());
            Glide.with(context).load(dataUpload.getPath()).placeholder(R.drawable.default_img).into(image_view);

        }
    }
}
