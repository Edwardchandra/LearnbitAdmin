package com.example.learnbitadmin.main.home.tab.course.coursedetail.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.home.tab.course.model.Content;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContentAdapter  extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private ArrayList<Content> contentArrayList;
    private Context context;

    public ContentAdapter(ArrayList<Content> contentArrayList, Context context) {
        this.contentArrayList = contentArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.curriculum_content_item, parent, false);

        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        holder.contentName.setText(contentArrayList.get(position).getSectionTopicName());
        holder.contentType.setText(contentArrayList.get(position).getSectionTopicType());
    }

    @Override
    public int getItemCount() {
        return (contentArrayList == null) ? 0 : contentArrayList.size();
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {

        private TextView contentName, contentType;

        ContentViewHolder(@NonNull View itemView) {
            super(itemView);

            contentName = itemView.findViewById(R.id.contentTitle);
            contentType = itemView.findViewById(R.id.contentType);
        }
    }
}
