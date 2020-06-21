package com.example.learnbitadmin.main.home.tab.terminate.holder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.home.tab.terminate.model.Terminate;
import com.example.learnbitadmin.main.home.tab.terminate.terminatedetail.TerminateDetailActivity;
import com.example.learnbitadmin.main.home.tab.withdraw.model.Withdraw;
import com.example.learnbitadmin.main.home.tab.withdraw.withdrawdetail.WithdrawDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TerminateHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView title, status, dateTime;
    private String key;
    private  String studentName, courseName;

    public TerminateHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.withdrawTitle);
        status = itemView.findViewById(R.id.withdrawStatus);
        dateTime = itemView.findViewById(R.id.withdrawDateTime);

        itemView.setClickable(true);
        itemView.setOnClickListener(this);
    }

    public void setTerminate(final Terminate terminate, final String key){
        this.key = key;

        FirebaseDatabase.getInstance().getReference("Users").child(terminate.getUserUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                if (name!=null){
                    studentName = name;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(itemView.getContext(), itemView.getContext().getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseDatabase.getInstance().getReference("Course").child(terminate.getCourseUid()).child("courseName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                if (name!=null){
                    courseName = name;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(itemView.getContext(), itemView.getContext().getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
            }
        });

        title.setText(itemView.getContext().getString(R.string.terminate_title, studentName, courseName));
        status.setText(terminate.getStatus());
        dateTime.setText(terminate.getDateTime());
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(itemView.getContext(), TerminateDetailActivity.class);
        intent.putExtra("key", key);
        itemView.getContext().startActivity(intent);
    }
}
