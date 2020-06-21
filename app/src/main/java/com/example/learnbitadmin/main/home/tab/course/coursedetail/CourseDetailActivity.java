package com.example.learnbitadmin.main.home.tab.course.coursedetail;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.home.tab.course.coursedetail.adapter.BenefitAdapter;
import com.example.learnbitadmin.main.home.tab.course.coursedetail.adapter.RequirementAdapter;
import com.example.learnbitadmin.main.home.tab.course.coursedetail.adapter.SectionAdapter;
import com.example.learnbitadmin.main.home.tab.course.model.Course;
import com.example.learnbitadmin.main.home.tab.course.model.Section;
import com.example.learnbitadmin.main.model.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CourseDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar courseDetailToolbar;
    private ImageView courseDetailImageView, teacherImageView;
    private TextView courseNameET, courseCategoryET, teacherName, teacherCourseCount, teacherRatings, courseSummary, courseScheduleDate, courseScheduleTime, courseStatusET;
    private RecyclerView benefitRecyclerView, requirementRecyclerView, curriculumRecyclerView;
    private ConstraintLayout optionsButtonLayout, statusLayout;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private BenefitAdapter benefitAdapter;
    private RequirementAdapter requirementAdapter;
    private SectionAdapter sectionAdapter;

    private ArrayList<String> timeArrayList = new ArrayList<>();
    private ArrayList<String> scheduleArrayList = new ArrayList<>();
    private ArrayList<String> benefitArrayList = new ArrayList<>();
    private ArrayList<String> requirementArrayList = new ArrayList<>();
    private ArrayList<Section> sectionArrayList = new ArrayList<>();

    private String teacherUid;
    private String courseStatus;
    private String dateTime;
    private String timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        courseDetailToolbar = findViewById(R.id.teacherCourseDetail_Toolbar);
        courseDetailImageView = findViewById(R.id.courseDetail_ImageView);
        courseNameET = findViewById(R.id.courseDetail_CourseTitle);
        courseCategoryET = findViewById(R.id.courseDetail_CourseCategory);
        courseSummary = findViewById(R.id.courseSummary);
        courseScheduleDate = findViewById(R.id.courseScheduleDate);
        courseScheduleTime = findViewById(R.id.courseScheduleTime);
        statusLayout = findViewById(R.id.courseDetail_StatusLayout);
        benefitRecyclerView = findViewById(R.id.benefitRecyclerView);
        requirementRecyclerView = findViewById(R.id.requirementRecyclerView);
        curriculumRecyclerView = findViewById(R.id.curriculumRecyclerView);
        courseStatusET = findViewById(R.id.courseDetail_Status);
        optionsButtonLayout = findViewById(R.id.optionsButtonLayout);

        teacherImageView = findViewById(R.id.teacherImageView);
        teacherName = findViewById(R.id.teacherName);
        teacherCourseCount = findViewById(R.id.teacherCourseCount);
        teacherRatings = findViewById(R.id.teacherRatings);

        Button acceptButton = findViewById(R.id.acceptButton);
        Button declineButton = findViewById(R.id.declineButton);

        acceptButton.setOnClickListener(this);
        declineButton.setOnClickListener(this);

        teacherImageView.setClipToOutline(true);

        getCurrentDateTime();
        setupToolbar();
        setupFirebase();
        setupRecyclerView();
        retrieveData();
    }

    private void setupFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void retrieveData(){
        String key = getIntent().getStringExtra("key");

        if (key !=null){
            databaseReference = firebaseDatabase.getReference("Course").child(key);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Course course = dataSnapshot.getValue(Course.class);

                    if (course != null){
                        Glide.with(getApplicationContext()).load(course.getCourseImageURL()).into(courseDetailImageView);
                        courseNameET.setText(course.getCourseName());
                        courseCategoryET.setText(getString(R.string.divider, course.getCourseCategory(), course.getCourseSubcategory()));
                        courseSummary.setText(course.getCourseSummary());
                        courseStatus = course.getCourseAcceptance();
                        teacherUid = course.getTeacherUid();

                        switch (courseStatus) {
                            case "pending":
                                courseStatusET.setText(getString(R.string.pending));
                                statusLayout.setBackground(getDrawable(R.drawable.pending_course_status));
                                break;
                            case "accepted":
                                optionsButtonLayout.setVisibility(View.INVISIBLE);
                                courseStatusET.setText(getString(R.string.accepted));
                                statusLayout.setBackground(getDrawable(R.drawable.accepted_course_status));
                                break;
                            case "declined":
                                optionsButtonLayout.setVisibility(View.INVISIBLE);
                                courseStatusET.setText(getString(R.string.declined));
                                statusLayout.setBackground(getDrawable(R.drawable.declined_course_status));
                                break;
                        }

                        try {
                            courseScheduleDate.setText(getString(R.string.divider, dateFormatter(course.getCourseDate().get("startDate")), dateFormatter(course.getCourseDate().get("endDate"))));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        for (HashMap.Entry<String, Boolean> entry : course.getCourseTime().entrySet()) {
                            timeArrayList.add(entry.getKey());
                        }

                        for (HashMap.Entry<String, String> entry : course.getCourseSchedule().entrySet()) {
                            scheduleArrayList.add(entry.getValue());
                        }

                        Collections.reverse(timeArrayList);
                        Collections.reverse(scheduleArrayList);

                        StringBuilder time = new StringBuilder();
                        StringBuilder schedule = new StringBuilder();

                        for (int i=0;i<timeArrayList.size();i++) {
                            if (i == timeArrayList.size()-1){
                                time.append(timeArrayList.get(i)).append(".");
                            }else{
                                time.append(timeArrayList.get(i)).append(", ");
                            }
                        }

                        for (int i=0;i<scheduleArrayList.size();i++) {
                            if (i == scheduleArrayList.size()-1){
                                schedule.append(scheduleArrayList.get(i));
                            }else{
                                schedule.append(scheduleArrayList.get(i)).append(", ");
                            }
                        }

                        courseScheduleTime.setText(getString(R.string.schedule, schedule.toString(), time.toString()));

                        for (HashMap.Entry<String, String> entry : course.getCourseBenefit().entrySet()) {
                            benefitArrayList.add(entry.getValue());
                        }

                        for (HashMap.Entry<String, String> entry : course.getCourseRequirement().entrySet()) {
                            requirementArrayList.add(entry.getValue());
                        }

                        Collections.reverse(benefitArrayList);
                        Collections.reverse(requirementArrayList);

                        for (HashMap.Entry<String, Section> entry : course.getCourseCurriculum().entrySet()) {
                            sectionArrayList.add(new Section(entry.getKey(), entry.getValue().getName(), entry.getValue().getTopics()));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                                sectionArrayList.sort(Comparator.comparing(Section::getWeek));
                            }
                        }

                        firebaseDatabase.getReference("Users").child(course.getTeacherUid()).child("name").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String name = dataSnapshot.getValue(String.class);
                                if (name!=null){
                                    teacherName.setText(name);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                toast(getString(R.string.retrieve_failed));
                            }
                        });

                        firebaseDatabase.getReference("Users").child(course.getTeacherUid()).child("teacher").child("rating").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Float rating = dataSnapshot.getValue(Float.class);
                                if (rating!=null){
                                    teacherRatings.setText(getString(R.string.rating, rating));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                toast(getString(R.string.retrieve_failed));
                            }
                        });

                        FirebaseDatabase.getInstance().getReference("Course").orderByChild("teacherUid").equalTo(course.getTeacherUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long count = dataSnapshot.getChildrenCount();
                                teacherCourseCount.setText(getString(R.string.course_count, count));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                toast(getString(R.string.retrieve_failed));
                            }
                        });

                        FirebaseStorage.getInstance().getReference("Users").child(course.getTeacherUid()).child("profileimage")
                                .getDownloadUrl().addOnSuccessListener(uri -> Glide.with(getApplicationContext()).load(uri).into(teacherImageView))
                                .addOnFailureListener(e -> toast(getString(R.string.retrieve_failed)));

                        sectionAdapter.notifyDataSetChanged();
                        requirementAdapter.notifyDataSetChanged();
                        benefitAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    toast(getString(R.string.retrieve_failed));
                }
            });

        }
    }

    private void setupToolbar(){
        setSupportActionBar(courseDetailToolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String dateFormatter(String inputDate) throws ParseException {
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
        SimpleDateFormat newDateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);

        Date date = oldDateFormat.parse(inputDate);

        if (date!=null){
            return newDateFormat.format(date);
        }else {
            return inputDate;
        }
    }

    private void setupRecyclerView(){
        benefitAdapter = new BenefitAdapter(benefitArrayList);
        RecyclerView.LayoutManager benefitLayoutManager = new LinearLayoutManager(getApplicationContext());
        benefitRecyclerView.setLayoutManager(benefitLayoutManager);
        benefitRecyclerView.setAdapter(benefitAdapter);
        benefitRecyclerView.setHasFixedSize(true);

        requirementAdapter = new RequirementAdapter(requirementArrayList);
        RecyclerView.LayoutManager requirementLayoutManager = new LinearLayoutManager(getApplicationContext());
        requirementRecyclerView.setLayoutManager(requirementLayoutManager);
        requirementRecyclerView.setAdapter(requirementAdapter);
        requirementRecyclerView.setHasFixedSize(true);

        sectionAdapter = new SectionAdapter(sectionArrayList);
        RecyclerView.LayoutManager sectionLayoutManager = new LinearLayoutManager(getApplicationContext());
        curriculumRecyclerView.setLayoutManager(sectionLayoutManager);
        curriculumRecyclerView.setAdapter(sectionAdapter);
        curriculumRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.declineButton:
                setAlert();
                break;
            case R.id.acceptButton:
                acceptAction();
                break;
        }
    }

    private void declineAction(String message){
        databaseReference.child("courseAcceptance").setValue("declined");
        firebaseDatabase.getReference("Notification").child(teacherUid).push()
                .setValue(new Notification(getString(R.string.course_declined, courseNameET.getText().toString())
                        , getString(R.string.course_declined_message, courseNameET.getText().toString(), message)
                        , "teacher", timestamp, dateTime));
    }

    private void setAlert(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Please specify why the the course is declined");

        final EditText editText = new EditText(getApplicationContext());

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 20, 0);
        layout.addView(editText, params);

        alert.setView(layout);

        alert.setPositiveButton("Yes", (dialog, which) -> declineAction(editText.getText().toString()))
                .setNegativeButton("No", (dialog, which) -> {});

        alert.show();
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void acceptAction() {
        databaseReference.child("courseAcceptance").setValue("accepted");
        firebaseDatabase.getReference("Notification").child(teacherUid).push()
                .setValue(new Notification(getString(R.string.course_accepted, courseNameET.getText().toString())
                        , getString(R.string.course_accepted_message, courseNameET.getText().toString())
                        , "teacher", timestamp, dateTime));
    }

    private void getCurrentDateTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.ENGLISH);

        dateTime = simpleDateFormat.format(new java.util.Date());

        long timestampLong = System.currentTimeMillis()/1000;
        timestamp = Long.toString(timestampLong);
    }
}
