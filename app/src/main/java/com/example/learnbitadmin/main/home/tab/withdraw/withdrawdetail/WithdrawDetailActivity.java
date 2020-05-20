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

import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.home.tab.withdraw.model.Withdraw;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WithdrawDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout statusIndicator;
    private TextView status, name, amount, bankName, bankNumber, bankHolder, dateTime;
    private ConstraintLayout buttonLayout;
    private Button processButton, acceptButton, declineButton;

    private FirebaseDatabase firebaseDatabase;

    private String key;
    private String userUID;

    private Long balance;

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
        acceptButton = findViewById(R.id.successButton);
        declineButton = findViewById(R.id.declineButton);

        processButton.setOnClickListener(this);
        acceptButton.setOnClickListener(this);
        declineButton.setOnClickListener(this);

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
                            status.setText("Pending");
                            buttonLayout.setVisibility(View.INVISIBLE);
                            processButton.setVisibility(View.VISIBLE);
                        }else if (withdraw.getSent().equals("processing")){
                            statusIndicator.setBackground(getDrawable(R.drawable.process_status_indicator));
                            status.setText("Processing");
                            buttonLayout.setVisibility(View.VISIBLE);
                            processButton.setVisibility(View.INVISIBLE);
                        }else{
                            buttonLayout.setVisibility(View.INVISIBLE);
                            processButton.setVisibility(View.INVISIBLE);

                            if (withdraw.getSent().equals("success")){
                                statusIndicator.setBackground(getDrawable(R.drawable.done_status_indicator));
                                status.setText("Success");
                            }else if (withdraw.getSent().equals("failed")){
                                statusIndicator.setBackground(getDrawable(R.drawable.decline_status_indicator));
                                status.setText("Failed");
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

                            }
                        });

                        firebaseDatabase.getReference("Users").child(withdraw.getUserUid()).child("teacher").child("balance").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Long value = dataSnapshot.getValue(Long.class);

                                if (value!=null){
                                    balance = value;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    private void saveData(String value, String message){
        firebaseDatabase.getReference("Withdraw").child(key).child("sent").setValue(value);
        firebaseDatabase.getReference("Withdraw").child(key).child("message").setValue(message);
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
                saveData("processing", "Dear " + name.getText().toString() + ", your withdraw request is being processed by our team, please wait for 2-3 days for the process to finish.");
                break;
            case R.id.successButton:
                saveData("success", "Dear " + name.getText().toString() + ", your withdraw request has been fulfilled and transferred to your bank account.");
                break;
            case R.id.declineButton:
                setAlert();
                break;
        }
    }

    private void setAlert(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Please specify why the withdrawal process failed");

        final EditText editText = new EditText(getApplicationContext());

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 20, 0);
        layout.addView(editText, params);

        alert.setView(layout);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveData("failed", "Dear " + name.getText().toString() + ", unfortunately we cannot process your withdraw request right now. " + editText.getText().toString());
                returnBalance();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }

    private void returnBalance(){
        Long total = balance + Long.parseLong(amount.getText().toString());

        firebaseDatabase.getReference("Users").child(userUID).child("teacher").child("balance").setValue(total);
    }
}
