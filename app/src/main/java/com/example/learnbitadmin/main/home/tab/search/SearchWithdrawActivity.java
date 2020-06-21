package com.example.learnbitadmin.main.home.tab.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.home.tab.search.adapter.SearchWithdrawAdapter;
import com.example.learnbitadmin.main.home.tab.withdraw.model.Withdraw;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchWithdrawActivity extends AppCompatActivity {
    private Toolbar searchToolbar;
    private RecyclerView searchRecyclerView;
    private SearchWithdrawAdapter searchAdapter;

    private ArrayList<Withdraw> withdrawArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_withdraw);

        searchToolbar = findViewById(R.id.searchToolbar);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);

        setupToolbar();
        setupRecyclerView();
        retrieveData();
    }

    private void setupToolbar(){
        setSupportActionBar(searchToolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupRecyclerView(){
        searchAdapter = new SearchWithdrawAdapter(withdrawArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        searchRecyclerView.setLayoutManager(layoutManager);
        searchRecyclerView.setAdapter(searchAdapter);
    }

    private void retrieveData(){
        withdrawArrayList.clear();
        FirebaseDatabase.getInstance().getReference("Withdraw").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String key = ds.getKey();
                    Withdraw withdraw = ds.getValue(Withdraw.class);

                    if (withdraw!=null){
                        FirebaseDatabase.getInstance().getReference("Users").child(withdraw.getUserUid()).child("name").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String name = dataSnapshot.getValue(String.class);
                                if (name!=null){
                                    withdrawArrayList.add(new Withdraw(getString(R.string.new_withdraw, name, withdraw.getAmount()), key, withdraw.getBankName(), withdraw.getBankNumber(), withdraw.getDestinationName(), withdraw.getSent(), withdraw.getAmount(), withdraw.getUserUid(), withdraw.getDateTime()));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(SearchWithdrawActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchWithdrawActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.teacher_search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_menu);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Withdraw");
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setMaxWidth(Integer.MAX_VALUE);

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(android.R.color.black));
        searchEditText.setHintTextColor(getResources().getColor(R.color.darkGray));

        ImageView searchImageView = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        searchImageView.setVisibility(View.GONE);

        ImageView searchCloseButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchCloseButton.setVisibility(View.VISIBLE);

        View searchUnderlineView = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        searchUnderlineView.setBackgroundColor(getResources().getColor(android.R.color.white));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void filter(String text){
        ArrayList<Withdraw> tempArrayList = new ArrayList<>();
        for(Withdraw withdraw : withdrawArrayList){
            if(withdraw.getTitle().toLowerCase().contains(text)){
                tempArrayList.add(withdraw);
            }
        }
        searchAdapter.updateList(tempArrayList);
    }
}
