package com.example.learnbitadmin.main.home.tab.course.holder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.home.tab.course.coursedetail.CourseDetailActivity;
import com.example.learnbitadmin.main.home.tab.course.model.Course;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CourseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView courseImageView;
    private TextView courseName, courseStatus, courseTime;
    private Context context;
    private String key;

    public CourseHolder(@NonNull View itemView) {
        super(itemView);

        courseImageView = itemView.findViewById(R.id.courseImageView);
        courseName = itemView.findViewById(R.id.courseName);
        courseStatus = itemView.findViewById(R.id.courseStatus);
        courseTime = itemView.findViewById(R.id.courseTime);

        itemView.setClickable(true);
        itemView.setOnClickListener(this);
        courseImageView.setClipToOutline(true);

        context = itemView.getContext();
    }

    public void setCourse(String key){
        this.key = key;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Course").child(key);
        Query query = databaseReference.orderByChild("timestamp").limitToLast(20);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Course course = ds.getValue(Course.class);

                    if (course!=null){
                        courseName.setText(course.getCourseName());
                        courseStatus.setText(course.getCourseAcceptance());
                        courseTime.setText(course.getCreateTime());

                        Glide.with(context).load(course.getCourseImageURL()).into(courseImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, CourseDetailActivity.class);
        intent.putExtra("key", key);
        intent.putExtra("courseName", courseName.getText().toString());
        context.startActivity(intent);
    }
}
