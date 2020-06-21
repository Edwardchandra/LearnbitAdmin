package com.example.learnbitadmin.launch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.HomeActivity;
import com.example.learnbitadmin.model.Admins;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private androidx.appcompat.widget.Toolbar registerToolbar;
    private EditText nameET;
    private EditText emailET;
    private EditText passwordET;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerToolbar = findViewById(R.id.registerToolbar);
        Button createAccountButton = findViewById(R.id.register_RegisterButton);
        nameET = findViewById(R.id.register_NameET);
        emailET = findViewById(R.id.register_EmailET);
        passwordET = findViewById(R.id.register_PasswordET);

        createAccountButton.setOnClickListener(this);

        actionBarSetup();
        setupFirebaseAuth();
    }

    private void checkEditText(){
        if(isEmpty(nameET)){
            nameET.setError(getString(R.string.name_error));
        }else if(nameET.getText().toString().length() < 3){
            nameET.setError(getString(R.string.name_error_character));
        }else if(isEmpty(emailET)){
            emailET.setError(getString(R.string.email_error));
        }else if(!isValidEmail(emailET)){
            emailET.setError(getString(R.string.email_error_format));
        }else if(isEmpty(passwordET)){
            passwordET.setError(getString(R.string.password_error));
        }else if(passwordET.getText().toString().length() < 7){
            passwordET.setError(getString(R.string.password_error_character));
        }else{
            createAccount();
        }
    }

    private void actionBarSetup(){
        setSupportActionBar(registerToolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isEmpty(EditText editText){
        return TextUtils.isEmpty(editText.getText().toString());
    }

    private boolean isValidEmail(EditText editText) {
        return !TextUtils.isEmpty(editText.getText().toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches();
    }

    private void setupFirebaseAuth(){
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register_RegisterButton) {
            checkEditText();
        }
    }

    private void createAccount(){
        firebaseAuth.createUserWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        user = firebaseAuth.getCurrentUser();
                        updateUI(user);

                        saveData();
                    }else{
                        Toast.makeText(RegisterActivity.this, "Register Failed. Account already exist.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Admins").child(user.getUid());

        databaseReference.setValue(new Admins(nameET.getText().toString(), emailET.getText().toString()));
    }

    private void updateUI(FirebaseUser user){
        if (user!=null){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }
}
