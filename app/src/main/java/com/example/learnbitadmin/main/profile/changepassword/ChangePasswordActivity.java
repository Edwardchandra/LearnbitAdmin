package com.example.learnbitadmin.main.profile.changepassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.HomeActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText oldPassword, repeatOldPassword, newPassword;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPassword = findViewById(R.id.changePassword_OldPassword);
        repeatOldPassword = findViewById(R.id.changePassword_OldPasswordRepeat);
        newPassword = findViewById(R.id.changePassword_NewPassword);
        Button saveButton = findViewById(R.id.changePassword_SaveButton);

        saveButton.setOnClickListener(v -> checkEditText());

        setupToolbar();
        setupFirebaseAuth();
    }

    private void setupToolbar(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Change Password");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupFirebaseAuth(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    private void changePassword(){
        String email = user.getEmail();
        String password = oldPassword.getText().toString();

        if (email!=null){
            AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);
            user.reauthenticate(authCredential).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    user.updatePassword(newPassword.getText().toString()).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()){
                            toast(getString(R.string.upload_success));
                        }else{
                            toast(getString(R.string.no_connection));
                        }
                    });
                }else{
                    toast(getString(R.string.password_not_match));
                }
            });
        }
    }

    private void checkEditText(){
        if (oldPassword.getText().toString().isEmpty()){
            oldPassword.setError("Old password shouldn't be empty");
        }else if (oldPassword.getText().toString().length() <= 6){
            oldPassword.setError("Old password must be more than 6 characters");
        }else if (repeatOldPassword.getText().toString().isEmpty()){
            repeatOldPassword.setError("Repeat old password shouldn't be empty");
        }else if (!repeatOldPassword.getText().toString().equals(oldPassword.getText().toString())){
            repeatOldPassword.setError("Repeat old password should have be the same with old password");
        }else if (newPassword.getText().toString().isEmpty()){
            newPassword.setError("New password shouldn't be empty");
        }else{
            changePassword();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
