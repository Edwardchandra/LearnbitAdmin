package com.example.learnbitadmin.main.home.tab.terminate.terminatedetail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.home.tab.course.model.Course;
import com.example.learnbitadmin.main.home.tab.terminate.model.Terminate;
import com.example.learnbitadmin.main.model.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class TerminateDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView courseImageView;
    private TextView courseName, courseStudent, courseTeacher, studentName, studentReason, studentEmail, dateTime, status, teacherEmail;
    private String key, courseTime, studentUid, courseUid, teacherUid, courseStudentName, timestamp, date, startTime;
    private long price, teacherBalance;
    private ConstraintLayout buttonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminate_detail);

        courseImageView = findViewById(R.id.courseImageView);
        courseName = findViewById(R.id.courseName);
        courseStudent = findViewById(R.id.courseStudent);
        courseTeacher = findViewById(R.id.courseTeacher);
        studentName = findViewById(R.id.studentName);
        studentReason = findViewById(R.id.reason);
        studentEmail = findViewById(R.id.studentEmail);
        dateTime = findViewById(R.id.dateTime);
        status = findViewById(R.id.status);
        teacherEmail = findViewById(R.id.teacherEmail);
        buttonLayout = findViewById(R.id.buttonLayout);
        Button halfRefund = findViewById(R.id.halfRefund);
        Button fullRefund = findViewById(R.id.fullRefund);

        halfRefund.setOnClickListener(this);
        fullRefund.setOnClickListener(this);

        courseImageView.setClipToOutline(true);

        getCurrentDateTime();
        setupToolbar();
        retrieveIntentData();
        retrieveData();
    }

    private void retrieveIntentData(){
        key = getIntent().getStringExtra("key");
    }

    private void retrieveData(){
        FirebaseDatabase.getInstance().getReference("Terminate").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Terminate terminate = dataSnapshot.getValue(Terminate.class);
                if (terminate!=null){

                    FirebaseDatabase.getInstance().getReference("Course").child(terminate.getCourseUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Course course = dataSnapshot.getValue(Course.class);
                            if (course!=null){
                                for (HashMap.Entry<String, String> courseDate : course.getCourseDate().entrySet()){
                                    if (courseDate.getKey().equals("startDate")){
                                        startTime = courseDate.getValue();
                                    }
                                }
                                price = course.getCoursePrice();
                                teacherUid = course.getTeacherUid();
                                courseName.setText(course.getCourseName());
                                Glide.with(getApplicationContext()).load(course.getCourseImageURL()).into(courseImageView);
                                FirebaseDatabase.getInstance().getReference("Users").child(course.getTeacherUid()).child("name").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String name = dataSnapshot.getValue(String.class);
                                        if (name!=null){
                                            courseTeacher.setText(name);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(TerminateDetailActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                FirebaseDatabase.getInstance().getReference("Users").child(course.getTeacherUid()).child("teacher").child("balance").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Long balance = dataSnapshot.getValue(Long.class);
                                        if (balance!=null){
                                            teacherBalance = balance;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(TerminateDetailActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                courseStudent.setText(getString(R.string.students, course.getCourseStudent().size()));
                                FirebaseDatabase.getInstance().getReference("Users").child(course.getTeacherUid()).child("email").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String email = dataSnapshot.getValue(String.class);
                                        if (email!=null){
                                            teacherEmail.setText(email);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(TerminateDetailActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(TerminateDetailActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
                        }
                    });

                    FirebaseDatabase.getInstance().getReference("Users").child(terminate.getUserUid()).child("name").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.getValue(String.class);
                            if (name!=null){
                                studentName.setText(name);
                                courseStudentName = name;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(TerminateDetailActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
                        }
                    });

                    FirebaseDatabase.getInstance().getReference("Users").child(terminate.getUserUid()).child("email").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String email = dataSnapshot.getValue(String.class);
                            if (email!=null){
                                studentEmail.setText(email);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(TerminateDetailActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
                        }
                    });

                    studentReason.setText(terminate.getReason());
                    dateTime.setText(terminate.getDateTime());
                    status.setText(terminate.getStatus());
                    courseTime = terminate.getCourseTime();
                    studentUid = terminate.getUserUid();
                    courseUid = terminate.getCourseUid();

                    if (terminate.getStatus().equalsIgnoreCase("resolved")){
                        buttonLayout.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TerminateDetailActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupToolbar(){
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Terminate Request Detail");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    //inflate right dot menu to toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.terminate_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.cancel_menu:
                cancelAction();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void cancelAction(){
        FirebaseDatabase.getInstance().getReference("Terminate").child(key).child("status").setValue("resolved");
        FirebaseDatabase.getInstance().getReference("Users").child(studentUid).child("student").child("courses").child(courseUid).setValue(courseTime);
        FirebaseDatabase.getInstance().getReference("Notification").child(teacherUid).push()
                .setValue(new Notification(getString(R.string.student_terminate_cancel, courseStudentName)
                        , getString(R.string.student_terminate_cancel_message, courseStudentName)
                        , "teacher", timestamp, date));
    }

    private void halfRefund(){
        FirebaseDatabase.getInstance().getReference("Terminate").child(key).child("status").setValue("resolved");
        FirebaseDatabase.getInstance().getReference("Users").child(studentUid).child("student").child("courses").child(courseUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TerminateDetailActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
            }
        });

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);

        Date startDate = new Date();
        Date currentDate = new Date();

        try {
            startDate = simpleDateFormat1.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (currentDate.before(startDate)){
            FirebaseDatabase.getInstance().getReference("Course").child(courseUid).child("courseTime").child(courseTime).setValue(false);
        }

        long totalBalance = teacherBalance - (price/2);
        FirebaseDatabase.getInstance().getReference("Users").child(teacherUid).child("teacher").child("balance").setValue(totalBalance);

        FirebaseDatabase.getInstance().getReference("Notification").child(teacherUid).push()
                .setValue(new Notification(getString(R.string.student_terminated, courseStudentName)
                        , getString(R.string.student_terminated_message, courseStudentName, studentReason.getText().toString())
                        , "teacher", timestamp, date));
    }

    private void fullRefund(){
        FirebaseDatabase.getInstance().getReference("Terminate").child(key).child("status").setValue("resolved");
        FirebaseDatabase.getInstance().getReference("Users").child(studentUid).child("student").child("courses").child(courseUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TerminateDetailActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
            }
        });

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);

        Date startDate = new Date();
        Date currentDate = new Date();

        try {
            startDate = simpleDateFormat1.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (currentDate.before(startDate)){
            FirebaseDatabase.getInstance().getReference("Course").child(courseUid).child("courseTime").child(courseTime).setValue(false);
        }

        FirebaseDatabase.getInstance().getReference("Notification").child(teacherUid).push()
                .setValue(new Notification(getString(R.string.student_terminated, courseStudentName)
                        , getString(R.string.student_terminated_message, courseStudentName, studentReason.getText().toString())
                        , "teacher", timestamp, date));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.halfRefund:
                halfRefund();
                finish();
                break;
            case R.id.fullRefund:
                fullRefund();
                finish();
                break;
        }
    }

    private void getCurrentDateTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.ENGLISH);

        date = simpleDateFormat.format(new java.util.Date());

        long timestampLong = System.currentTimeMillis()/1000;
        timestamp = Long.toString(timestampLong);
    }
}