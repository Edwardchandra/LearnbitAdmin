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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar signInToolbar;
    private EditText emailET;
    private EditText passwordET;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInToolbar = findViewById(R.id.signInToolbar);
        Button signInButton = findViewById(R.id.signIn_SignInButton);
        emailET = findViewById(R.id.signIn_EmailET);
        passwordET = findViewById(R.id.signIn_PasswordET);

        setupToolbar();

        signInButton.setOnClickListener(v -> {
            if(isEmpty(emailET)){
                emailET.setError(getString(R.string.email_error));
            }else if(!isValidEmail(emailET)){
                emailET.setError(getString(R.string.email_error_format));
            }else if(isEmpty(passwordET)){
                passwordET.setError(getString(R.string.password_error));
            }else if(passwordET.getText().toString().length() < 7){
                passwordET.setError(getString(R.string.password_error_character));
            }else{
                signIn();
            }
        });
    }

    private void setupToolbar(){
        setSupportActionBar(signInToolbar);

        if (getSupportActionBar() != null) {
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

    private void signIn(){
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        updateUI(user);
                    }else{
                        Toast.makeText(SignInActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(FirebaseUser user){
        if (user!=null){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }
}
