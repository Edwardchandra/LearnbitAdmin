package com.example.learnbitadmin.main.home.tab.search.adapter;

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
import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.home.tab.course.coursedetail.CourseDetailActivity;
import com.example.learnbitadmin.main.home.tab.course.model.Course;
import com.example.learnbitadmin.main.home.tab.withdraw.model.Withdraw;

import java.util.ArrayList;

public class SearchCourseAdapter extends RecyclerView.Adapter<SearchCourseAdapter.SearchCourseViewHolder> {

    private ArrayList<Course> courseArrayList;

    public SearchCourseAdapter(ArrayList<Course> courseArrayList) {
        this.courseArrayList = courseArrayList;
    }

    @NonNull
    @Override
    public SearchCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.course_item, parent, false);

        return new SearchCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchCourseViewHolder holder, int position) {
        holder.courseName.setText(courseArrayList.get(position).getCourseName());
        holder.courseStatus.setText(courseArrayList.get(position).getCourseAcceptance());
        holder.courseTime.setText(courseArrayList.get(position).getCreateTime());
        Glide.with(holder.context.getApplicationContext()).load(courseArrayList.get(position).getCourseImageURL()).into(holder.courseImageView);

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(holder.context, CourseDetailActivity.class);
            intent.putExtra("key", courseArrayList.get(position).getCourseKey());
            holder.context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (courseArrayList == null) ? 0 : courseArrayList.size();
    }

    public void updateList(ArrayList<Course> courseArrayList){
        this.courseArrayList = courseArrayList;
        notifyDataSetChanged();
    }

    public static class SearchCourseViewHolder extends RecyclerView.ViewHolder{

        private ImageView courseImageView;
        private TextView courseName, courseStatus, courseTime;
        private Context context;
        private String key;


        public SearchCourseViewHolder(@NonNull View itemView) {
            super(itemView);

            courseImageView = itemView.findViewById(R.id.courseImageView);
            courseName = itemView.findViewById(R.id.courseName);
            courseStatus = itemView.findViewById(R.id.courseStatus);
            courseTime = itemView.findViewById(R.id.courseTime);

            itemView.setClickable(true);
            courseImageView.setClipToOutline(true);

            context = itemView.getContext();
        }
    }
}
