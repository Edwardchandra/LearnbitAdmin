package com.example.learnbitadmin.main.profile.editprofile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.HomeActivity;
import com.example.learnbitadmin.model.Admins;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView profileImageView;
    private EditText profileName, profileEmail, profilePassword;

    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profilePassword = findViewById(R.id.editProfile_PasswordET);
        profileImageView = findViewById(R.id.editProfile_ImageView);
        profileName = findViewById(R.id.editProfile_NameET);
        profileEmail = findViewById(R.id.editProfile_EmailET);
        LinearLayout changeImageButton = findViewById(R.id.editProfile_ChangeImageButton);
        Button saveButton = findViewById(R.id.editProfile_SaveButton);

        changeImageButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        setupFirebase();
        retrieveData();
        retrieveImage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editProfile_ChangeImageButton:
                pickFromGallery();
                break;
            case R.id.editProfile_SaveButton:
                checkEditText();
                break;
        }
    }

    private void checkEditText(){
        if (profileName.getText().toString().isEmpty()){
            profileName.setError(getString(R.string.name_error));
        }else if (profileName.getText().toString().length() < 3){
            profileName.setError(getString(R.string.name_error_character));
        }else if (profileEmail.getText().toString().isEmpty()){
            profileName.setError(getString(R.string.email_error));
        }else if (!isValidEmail(profileEmail)){
            profileName.setError(getString(R.string.email_error_format));
        }else if (profilePassword.getText().toString().isEmpty()){
            profilePassword.setError(getString(R.string.password_error));
        }else if (profilePassword.getText().toString().length() < 7){
            profilePassword.setError(getString(R.string.password_error_character));
        }else{
            saveData();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }

    private boolean isValidEmail(EditText editText) {
        return !TextUtils.isEmpty(editText.getText().toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches();
    }

    private void saveData(){
        if (user.getEmail()!=null){
            AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), profilePassword.getText().toString());
            user.reauthenticate(authCredential).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    user.updateEmail(profileEmail.getText().toString());

                    databaseReference.setValue(new Admins(profileName.getText().toString(), profileEmail.getText().toString()));

                    Bitmap bitmap = ((BitmapDrawable) profileImageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = storageReference.putBytes(data);
                    uploadTask.addOnFailureListener(e -> toast(getString(R.string.upload_failed)))
                            .addOnSuccessListener(taskSnapshot -> toast(getString(R.string.upload_success)));
                }else{
                    toast(getString(R.string.save_failed));
                }
            });
        }
    }

    private void setupFirebase(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        user = firebaseAuth.getCurrentUser();
    }

    private void retrieveData(){
        databaseReference = firebaseDatabase.getReference("Admins").child(user.getUid());

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
                toast(getString(R.string.retrieve_failed));
            }
        });
    }

    private void retrieveImage(){
        storageReference = firebaseStorage.getReference("Admins").child(user.getUid()).child("profileImage");
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(this).load(uri).into(profileImageView))
                .addOnFailureListener(e -> toast(getString(R.string.retrieve_failed)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            if (requestCode == 0) {
                if (data!=null){
                    Uri selectedImage = data.getData();
                    profileImageView.setImageURI(selectedImage);
                }
            }
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(galleryIntent, 0);
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
