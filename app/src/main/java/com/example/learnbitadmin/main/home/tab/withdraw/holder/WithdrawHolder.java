package com.example.learnbitadmin.main.home.tab.withdraw.holder;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.home.tab.withdraw.model.Withdraw;
import com.example.learnbitadmin.main.home.tab.withdraw.withdrawdetail.WithdrawDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WithdrawHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView title, status, dateTime;
    private String key;

    public WithdrawHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.withdrawTitle);
        status = itemView.findViewById(R.id.withdrawStatus);
        dateTime = itemView.findViewById(R.id.withdrawDateTime);

        itemView.setClickable(true);
        itemView.setOnClickListener(this);
    }

    public void setWithdraw(final Withdraw withdraw, final String key){
        this.key = key;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(withdraw.getUserUid()).child("name");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                if (name!=null){
                    title.setText(itemView.getContext().getString(R.string.new_withdraw, name, withdraw.getAmount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(itemView.getContext(), itemView.getContext().getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
            }
        });

        status.setText(withdraw.getSent());
        dateTime.setText(withdraw.getDateTime());
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(itemView.getContext(), WithdrawDetailActivity.class);
        intent.putExtra("key", key);
        itemView.getContext().startActivity(intent);
    }
}
