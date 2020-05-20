package com.example.learnbitadmin.main.home.tab.withdraw;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.home.tab.withdraw.holder.WithdrawHolder;
import com.example.learnbitadmin.main.home.tab.withdraw.model.Withdraw;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class WithdrawFragment extends Fragment {

    private RecyclerView withdrawRecyclerView;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseRecyclerOptions<Withdraw> firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<Withdraw, WithdrawHolder> firebaseRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_withdraw, container, false);

        withdrawRecyclerView = view.findViewById(R.id.withdrawRecyclerView);

        setupFirebase();
        retrieveData();
        setupRecyclerView();

        return view;
    }

    private void setupFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void setupRecyclerView(){
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Withdraw, WithdrawHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull WithdrawHolder holder, int position, @NonNull Withdraw model) {
                DatabaseReference databaseReference = firebaseRecyclerAdapter.getRef(position);

                holder.setWithdraw(model, databaseReference.getKey());
            }

            @NonNull
            @Override
            public WithdrawHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.withdraw_item, parent, false);

                return new WithdrawHolder(view);
            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        withdrawRecyclerView.setLayoutManager(layoutManager);
        withdrawRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void retrieveData(){
        Query query = firebaseDatabase.getReference("Withdraw").orderByChild("timestamp").limitToLast(20);

        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Withdraw>().setQuery(query, Withdraw.class).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (firebaseRecyclerAdapter!= null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }
}
