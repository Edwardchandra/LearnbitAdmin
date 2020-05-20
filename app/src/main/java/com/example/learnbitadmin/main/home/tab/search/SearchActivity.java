package com.example.learnbitadmin.main.home.tab.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.home.tab.withdraw.holder.WithdrawHolder;
import com.example.learnbitadmin.main.home.tab.withdraw.model.Withdraw;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SearchActivity extends AppCompatActivity {
    private Toolbar searchToolbar;
    private RecyclerView searchRecyclerView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private FirebaseRecyclerOptions<Withdraw> firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<Withdraw, WithdrawHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchToolbar = findViewById(R.id.searchToolbar);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);

        setupToolbar();
        setupFirebase();
        retrieveDataFromFirebase();
    }

    private void setupToolbar(){
        setSupportActionBar(searchToolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupWithdrawRecyclerView(){
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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        searchRecyclerView.setLayoutManager(layoutManager);
        searchRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        user = firebaseAuth.getCurrentUser();
    }

    private void retrieveDataFromFirebase(){
        databaseReference = firebaseDatabase.getReference("Withdraw");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.teacher_search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_menu);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Withdraws");
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
                Query query1 = databaseReference.orderByChild("destinationName").startAt(query);
                firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Withdraw>().setQuery(query1, Withdraw.class).build();

                setupWithdrawRecyclerView();

                firebaseRecyclerAdapter.startListening();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query query1 = databaseReference.orderByChild("destinationName").startAt(newText);
                firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Withdraw>().setQuery(query1, Withdraw.class).build();

                setupWithdrawRecyclerView();

                firebaseRecyclerAdapter.startListening();

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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseRecyclerAdapter!=null){
            firebaseRecyclerAdapter.stopListening();
        }
    }
}
