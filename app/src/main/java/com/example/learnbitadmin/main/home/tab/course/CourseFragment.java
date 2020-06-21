package com.example.learnbitadmin.main.home.tab.course;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.home.tab.course.holder.CourseHolder;
import com.example.learnbitadmin.main.home.tab.course.model.Course;
import com.example.learnbitadmin.main.home.tab.withdraw.holder.WithdrawHolder;
import com.example.learnbitadmin.main.home.tab.withdraw.model.Withdraw;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CourseFragment extends Fragment {

    private RecyclerView courseRecyclerView;
    private FirebaseDatabase firebaseDatabase;

    private FirebaseRecyclerOptions<Course> firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<Course, CourseHolder> firebaseRecyclerAdapter;

    public CourseFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_course, container, false);

        courseRecyclerView = view.findViewById(R.id.courseRecyclerView);

        setupFirebase();
        retrieveData();
        setupRecyclerView();

        return view;
    }

    private void setupFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void setupRecyclerView(){
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Course, CourseHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CourseHolder holder, int position, @NonNull Course model) {
                DatabaseReference databaseReference = firebaseRecyclerAdapter.getRef(position);
                holder.setCourse(databaseReference.getKey(), model);
            }

            @NonNull
            @Override
            public CourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);

                return new CourseHolder(view);
            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        courseRecyclerView.setLayoutManager(layoutManager);
        courseRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void retrieveData(){
        Query query = firebaseDatabase.getReference("Course").orderByChild("timestamp");
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Course>().setQuery(query, Course.class).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (firebaseRecyclerAdapter!= null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }
}
