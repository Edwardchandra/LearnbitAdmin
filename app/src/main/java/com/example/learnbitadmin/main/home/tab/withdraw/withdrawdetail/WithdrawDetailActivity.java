package com.example.learnbitadmin.main.home.tab.withdraw.withdrawdetail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.home.tab.withdraw.model.Withdraw;
import com.example.learnbitadmin.main.model.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class WithdrawDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout statusIndicator;
    private TextView status, name, amount, bankName, bankNumber, bankHolder, dateTime;
    private ConstraintLayout buttonLayout;
    private Button processButton;

    private FirebaseDatabase firebaseDatabase;

    private String key;
    private String userUID;

    private Long balance;

    private String date, timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_detail);

        key = getIntent().getStringExtra("key");

        statusIndicator = findViewById(R.id.statusIndicator);
        status = findViewById(R.id.withdrawStatus);
        name = findViewById(R.id.withdrawName);
        amount = findViewById(R.id.withdrawAmount);
        bankName = findViewById(R.id.bankName);
        bankNumber = findViewById(R.id.bankNumber);
        bankHolder = findViewById(R.id.bankHolder);
        dateTime = findViewById(R.id.withdrawDateTime);
        buttonLayout = findViewById(R.id.buttonLayout);
        processButton = findViewById(R.id.processButton);
        Button acceptButton = findViewById(R.id.successButton);
        Button declineButton = findViewById(R.id.declineButton);

        processButton.setOnClickListener(this);
        acceptButton.setOnClickListener(this);
        declineButton.setOnClickListener(this);

        getCurrentDateTime();
        setupToolbar();
        retrieveData();
    }

    private void retrieveData(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (key!=null){
            final DatabaseReference databaseReference = firebaseDatabase.getReference("Withdraw").child(key);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Withdraw withdraw = dataSnapshot.getValue(Withdraw.class);

                    if (withdraw!=null){
                        if (withdraw.getSent().equals("pending")){
                            statusIndicator.setBackground(getDrawable(R.drawable.pending_status_indicator));
                            status.setText(getString(R.string.pending));
                            buttonLayout.setVisibility(View.INVISIBLE);
                            processButton.setVisibility(View.VISIBLE);
                        }else if (withdraw.getSent().equals("processing")){
                            statusIndicator.setBackground(getDrawable(R.drawable.process_status_indicator));
                            status.setText(getString(R.string.processing));
                            buttonLayout.setVisibility(View.VISIBLE);
                            processButton.setVisibility(View.INVISIBLE);
                        }else{
                            buttonLayout.setVisibility(View.INVISIBLE);
                            processButton.setVisibility(View.INVISIBLE);

                            if (withdraw.getSent().equals("success")){
                                statusIndicator.setBackground(getDrawable(R.drawable.done_status_indicator));
                                status.setText(getString(R.string.success));
                            }else if (withdraw.getSent().equals("failed")){
                                statusIndicator.setBackground(getDrawable(R.drawable.decline_status_indicator));
                                status.setText(getString(R.string.failed));
                            }
                        }

                        firebaseDatabase.getReference("Users").child(withdraw.getUserUid()).child("name").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String nameValue = dataSnapshot.getValue(String.class);

                                if (nameValue!=null){
                                    name.setText(nameValue);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(WithdrawDetailActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
                            }
                        });

                        firebaseDatabase.getReference("Users").child(withdraw.getUserUid()).child("teacher").child("balance").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Long value = dataSnapshot.getValue(Long.class);
                                if (value!=null){ balance = value; }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(WithdrawDetailActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
                            }
                        });

                        amount.setText(String.valueOf(withdraw.getAmount()));
                        bankName.setText(withdraw.getBankName());
                        bankNumber.setText(String.valueOf(withdraw.getBankNumber()));
                        bankHolder.setText(withdraw.getDestinationName());
                        dateTime.setText(withdraw.getDateTime());
                        userUID = withdraw.getUserUid();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(WithdrawDetailActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupToolbar(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Withdrawal Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void saveData(String value, String title, String message){
        firebaseDatabase.getReference("Notification").child(userUID).push()
                .setValue(new Notification(title, message, "teacher", timestamp, date));

        firebaseDatabase.getReference("Withdraw").child(key).child("sent").setValue(value);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.processButton:
                saveData("processing", getString(R.string.withdraw_processing, amount.getText().toString()), getString(R.string.withdraw_processing_message, amount.getText().toString()));
                break;
            case R.id.successButton:
                saveData("success", getString(R.string.withdraw_success, amount.getText().toString()), getString(R.string.withdraw_success_message, amount.getText().toString()));
                break;
            case R.id.declineButton:
                setAlert();
                break;
        }
    }

    private void setAlert(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Please specify why the withdraw process failed");

        final EditText editText = new EditText(getApplicationContext());

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 20, 0);
        layout.addView(editText, params);

        alert.setView(layout);

        alert.setPositiveButton("Yes", (dialog, which) -> {
            saveData("failed", getString(R.string.withdraw_failed, amount.getText().toString()), getString(R.string.withdraw_failed_message, amount.getText().toString(), editText.getText().toString()));
            returnBalance();
        }).setNegativeButton("No", (dialog, which) -> {});

        alert.show();
    }

    private void returnBalance(){
        Long total = balance + Long.parseLong(amount.getText().toString());
        firebaseDatabase.getReference("Users").child(userUID).child("teacher").child("balance").setValue(total);
    }

    private void getCurrentDateTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.ENGLISH);

        date = simpleDateFormat.format(new java.util.Date());

        long timestampLong = System.currentTimeMillis()/1000;
        timestamp = Long.toString(timestampLong);
    }
}
