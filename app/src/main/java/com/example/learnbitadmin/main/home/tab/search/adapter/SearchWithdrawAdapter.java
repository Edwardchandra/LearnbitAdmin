package com.example.learnbitadmin.main.home.tab.search.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.home.tab.withdraw.model.Withdraw;
import com.example.learnbitadmin.main.home.tab.withdraw.withdrawdetail.WithdrawDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchWithdrawAdapter extends RecyclerView.Adapter<SearchWithdrawAdapter.SearchWithdrawViewHolder> {

    private ArrayList<Withdraw> withdrawArrayList;

    public SearchWithdrawAdapter(ArrayList<Withdraw> withdrawArrayList) {
        this.withdrawArrayList = withdrawArrayList;
    }

    @NonNull
    @Override
    public SearchWithdrawViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.withdraw_item, parent, false);

        return new SearchWithdrawViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchWithdrawViewHolder holder, int position) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(withdrawArrayList.get(position).getUserUid()).child("name");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                if (name!=null){
                    holder.title.setText(withdrawArrayList.get(position).getTitle());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(holder.itemView.getContext(), holder.itemView.getContext().getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
            }
        });

        holder.status.setText(withdrawArrayList.get(position).getSent());
        holder.dateTime.setText(withdrawArrayList.get(position).getDateTime());

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(holder.itemView.getContext(), WithdrawDetailActivity.class);
            intent.putExtra("key", withdrawArrayList.get(position).getKey());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    public void updateList(ArrayList<Withdraw> withdrawArrayList){
        this.withdrawArrayList = withdrawArrayList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (withdrawArrayList == null) ? 0 : withdrawArrayList.size();
    }

    public static class SearchWithdrawViewHolder extends RecyclerView.ViewHolder{

        private TextView title, status, dateTime;

        public SearchWithdrawViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.withdrawTitle);
            status = itemView.findViewById(R.id.withdrawStatus);
            dateTime = itemView.findViewById(R.id.withdrawDateTime);

            itemView.setClickable(true);
        }
    }
}
