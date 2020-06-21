package com.example.learnbitadmin.main.home.tab.terminate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.home.tab.terminate.holder.TerminateHolder;
import com.example.learnbitadmin.main.home.tab.terminate.model.Terminate;
import com.example.learnbitadmin.main.home.tab.withdraw.holder.WithdrawHolder;
import com.example.learnbitadmin.main.home.tab.withdraw.model.Withdraw;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class TerminateFragment extends Fragment {

    private RecyclerView terminateRecyclerView;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseRecyclerOptions<Terminate> firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<Terminate, TerminateHolder> firebaseRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_terminate, container, false);

        terminateRecyclerView = view.findViewById(R.id.terminateRecyclerView);

        setupFirebase();
        retrieveData();
        setupRecyclerView();

        return view;
    }

    private void setupFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void setupRecyclerView(){
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Terminate, TerminateHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull TerminateHolder holder, int position, @NonNull Terminate model) {
                DatabaseReference databaseReference = firebaseRecyclerAdapter.getRef(position);
                holder.setTerminate(model, databaseReference.getKey());
            }

            @NonNull
            @Override
            public TerminateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.withdraw_item, parent, false);

                return new TerminateHolder(view);
            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        terminateRecyclerView.setLayoutManager(layoutManager);
        terminateRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void retrieveData(){
        Query query = firebaseDatabase.getReference("Terminate").orderByChild("timestamp").limitToLast(20);

        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Terminate>().setQuery(query, Terminate.class).build();
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