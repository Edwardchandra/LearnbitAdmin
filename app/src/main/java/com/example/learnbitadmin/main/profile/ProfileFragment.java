package com.example.learnbitadmin.main.profile;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.learnbitadmin.R;
import com.example.learnbitadmin.launch.MainActivity;
import com.example.learnbitadmin.main.profile.changepassword.ChangePasswordActivity;
import com.example.learnbitadmin.main.profile.editprofile.EditProfileActivity;
import com.example.learnbitadmin.model.Admins;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private TextView profileName, profileEmail;
    private ImageView profileImage;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ConstraintLayout signOutButton = view.findViewById(R.id.signOutButton);
        ConstraintLayout editProfileButton = view.findViewById(R.id.editProfileButton);
        ConstraintLayout changePasswordButton = view.findViewById(R.id.changePasswordButton);
        ConstraintLayout reportBugButton = view.findViewById(R.id.reportProblemButton);
        profileName = view.findViewById(R.id.profileName);
        profileEmail = view.findViewById(R.id.profileEmail);
        profileImage = view.findViewById(R.id.profileImage);

        signOutButton.setOnClickListener(this);
        editProfileButton.setOnClickListener(this);
        changePasswordButton.setOnClickListener(this);
        reportBugButton.setOnClickListener(this);

        setupFirebase();
        retrieveData();
        retrieveImage();

        return view;
    }

    private void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        user = firebaseAuth.getCurrentUser();
    }

    private void retrieveData(){
        DatabaseReference databaseReference = firebaseDatabase.getReference("Admins").child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Admins admins = dataSnapshot.getValue(Admins.class);

                if (admins!=null){
                    profileName.setText(admins.getName());
                    profileEmail.setText(admins.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               if (getActivity()!=null){
                   Toast.makeText(getContext(), getActivity().getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
               }
            }
        });
    }

    private void retrieveImage(){
        StorageReference storageReference = firebaseStorage.getReference("Admins").child(user.getUid()).child("profileImage");
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            if (getActivity()!=null){
                Glide.with(getActivity()).load(uri).into(profileImage);
            }
        }).addOnFailureListener(e -> {
            if (getActivity() != null){
                Toast.makeText(getContext(), getActivity().getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signOut(){
        firebaseAuth.signOut();
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }
    
    private void sendBugReport(){
        try{
            Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "learnbitapp@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "[BUG] Learnbit Admin Bug Report");
            intent.putExtra(Intent.EXTRA_TEXT, "<---... specify the bug or problems here ...--->");
            startActivity(intent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(getContext(), "No email application found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signOutButton:
                signOut();
                break;
            case R.id.editProfileButton:
                Intent editProfileIntent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(editProfileIntent);
                break;
            case R.id.changePasswordButton:
                Intent changePasswordIntent = new Intent(getContext(), ChangePasswordActivity.class);
                startActivity(changePasswordIntent);
                break;
            case R.id.reportProblemButton:
                sendBugReport();
                break;
        }
    }
}
