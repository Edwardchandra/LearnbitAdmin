package com.example.learnbitadmin.main.home.tab.course.coursedetail;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.home.tab.course.coursedetail.adapter.BenefitAdapter;
import com.example.learnbitadmin.main.home.tab.course.coursedetail.adapter.RequirementAdapter;
import com.example.learnbitadmin.main.home.tab.course.coursedetail.adapter.SectionAdapter;
import com.example.learnbitadmin.main.home.tab.course.model.Course;
import com.example.learnbitadmin.main.home.tab.course.model.Section;
import com.example.learnbitadmin.main.home.tab.course.model.User;
import com.example.learnbitadmin.main.home.tab.course.model.teacher.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private Button acceptButton, declineButton;
    private ConstraintLayout optionsButtonLayout, statusLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Query query;

    private Course course = new Course();

    private ArrayList<String> timeArrayList = new ArrayList<>();
    private ArrayList<String> scheduleArrayList = new ArrayList<>();
    private ArrayList<String> benefitArrayList = new ArrayList<>();
    private ArrayList<String> requirementArrayList = new ArrayList<>();
    private ArrayList<Section> sectionArrayList = new ArrayList<>();

    private String key, courseName, courseStatus;

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

        acceptButton = findViewById(R.id.acceptButton);
        declineButton = findViewById(R.id.declineButton);

        acceptButton.setOnClickListener(this);
        declineButton.setOnClickListener(this);

        teacherImageView.setClipToOutline(true);

        setupToolbar();
        setupFirebase();
        retrieveData();
    }

    private void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        user = firebaseAuth.getCurrentUser();
    }

    private void retrieveData(){
        courseName = getIntent().getStringExtra("courseName");
        key = getIntent().getStringExtra("key");

        if (key!=null && courseName!=null){
            databaseReference = firebaseDatabase.getReference("Course");
            query = databaseReference.child(key).orderByChild("courseName").startAt(courseName);

            query.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        course = ds.getValue(Course.class);

                        if (course != null){
                            Glide.with(getApplicationContext()).load(course.getCourseImageURL()).into(courseDetailImageView);

                            Log.d("url", course.getCourseImageURL());

                            courseNameET.setText(course.getCourseName());
                            courseCategoryET.setText(course.getCourseCategory() + " - " + course.getCourseSubcategory());

                            courseSummary.setText(course.getCourseSummary());

                            courseStatus = course.getCourseAcceptance();

                            if (courseStatus.equals("pending")){
                                courseStatusET.setText("Pending");
                                statusLayout.setBackground(getDrawable(R.drawable.pending_course_status));
                            }else if (courseStatus.equals("accepted")){
                                optionsButtonLayout.setVisibility(View.INVISIBLE);
                                courseStatusET.setText("Accepted");
                                statusLayout.setBackground(getDrawable(R.drawable.accepted_course_status));
                            }else if (courseStatus.equals("declined")){
                                optionsButtonLayout.setVisibility(View.INVISIBLE);
                                courseStatusET.setText("Declined");
                                statusLayout.setBackground(getDrawable(R.drawable.declined_course_status));
                            }

                            HashMap<String, String> date = course.getCourseDate();

                            try {
                                courseScheduleDate.setText(dateFormatter(date.get("startDate")) + " - " + dateFormatter(date.get("endDate")));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            for (HashMap.Entry<String, Boolean> entry : course.getCourseTime().entrySet()) {
                                String key = entry.getKey();

                                timeArrayList.add(key);
                            }

                            for (HashMap.Entry<String, String> entry : course.getCourseSchedule().entrySet()) {
                                String value = entry.getValue();

                                scheduleArrayList.add(value);
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

                            courseScheduleTime.setText("Every " + schedule.toString() + " at " + time.toString());

                            for (HashMap.Entry<String, String> entry : course.getCourseBenefit().entrySet()) {
                                String value = entry.getValue();

                                benefitArrayList.add(value);
                            }

                            for (HashMap.Entry<String, String> entry : course.getCourseRequirement().entrySet()) {
                                String value = entry.getValue();

                                requirementArrayList.add(value);
                            }

                            Collections.reverse(benefitArrayList);
                            Collections.reverse(requirementArrayList);

                            for (HashMap.Entry<String, Section> entry : course.getCourseCurriculum().entrySet()) {
                                String key = entry.getKey();
                                Section value = entry.getValue();

                                sectionArrayList.add(new Section(key, value.getName(), value.getTopics()));
                                sectionArrayList.sort(Comparator.comparing(Section::getWeek));

                                Log.d("section", sectionArrayList.get(0).getName() + " ");

                                setupRecyclerView();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });

            DatabaseReference databaseReference1 = firebaseDatabase.getReference("Users").child(key);
            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    if (user!=null){
                        teacherName.setText(user.getName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference databaseReference2 = firebaseDatabase.getReference("Users").child(key).child("teacher");
            databaseReference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Teacher teacher = dataSnapshot.getValue(Teacher.class);

                    if (teacher!=null){
                        String ratings = String.valueOf(teacher.getRating());

                        teacherRatings.setText(ratings + " Average Rating");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference("Users").child(key).child("profileimage");

            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getApplicationContext()).load(uri).into(teacherImageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CourseDetailActivity.this, "failed to load image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void deleteCourse(){
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
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
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.delete_menu:
                deleteCourse();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.teacher_course_detail_menu, menu);
        return true;
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
        BenefitAdapter benefitAdapter = new BenefitAdapter(benefitArrayList);
        RequirementAdapter requirementAdapter = new RequirementAdapter(requirementArrayList);
        SectionAdapter sectionAdapter = new SectionAdapter(sectionArrayList);

        RecyclerView.LayoutManager benefitLayoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView.LayoutManager requirementLayoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView.LayoutManager sectionLayoutManager = new LinearLayoutManager(getApplicationContext());

        benefitRecyclerView.setLayoutManager(benefitLayoutManager);
        requirementRecyclerView.setLayoutManager(requirementLayoutManager);
        curriculumRecyclerView.setLayoutManager(sectionLayoutManager);

        benefitRecyclerView.setAdapter(benefitAdapter);
        requirementRecyclerView.setAdapter(requirementAdapter);
        curriculumRecyclerView.setAdapter(sectionAdapter);

        benefitRecyclerView.setHasFixedSize(true);
        requirementRecyclerView.setHasFixedSize(true);
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
        databaseReference.child(key).orderByChild("courseName").startAt(courseName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String courseKey = ds.getKey();

                    if (courseKey!=null){
                        databaseReference.child(key).child(courseKey).child("courseAcceptance").setValue("declined");
                        databaseReference.child(key).child(courseKey).child("message").setValue(message);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

        alert.setPositiveButton("Yes", (dialog, which) -> declineAction(editText.getText().toString())).setNegativeButton("No", (dialog, which) -> {});

        alert.show();
    }

    private void acceptAction(){
        databaseReference.child(key).orderByChild("courseName").startAt(courseName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String courseKey = ds.getKey();

                    if (courseKey!=null){
                        databaseReference.child(key).child(courseKey).child("courseAcceptance").setValue("accepted");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
