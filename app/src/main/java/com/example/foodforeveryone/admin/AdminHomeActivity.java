package com.example.foodforeveryone.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodforeveryone.DonationPageActivity;
import com.example.foodforeveryone.LoginActivity;
import com.example.foodforeveryone.ProfileActivity;
import com.example.foodforeveryone.R;
import com.example.foodforeveryone.adapter.DonationPageAdapter;
import com.example.foodforeveryone.model.DonationDataModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private TextView navHeaderUsername;
    private View hView;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String strName, userId;
    private DonationPageAdapter donationPageAdapter;
    private RecyclerView recyclerView;
    private List<DonationDataModel> donationDataModelList;
    private ValueEventListener eventListener;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#636A6C")));
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.end_color));
        }

        strName = getSharedPreferences("loginSessionSharedPreferences", Context.MODE_PRIVATE).getString("userName", "Admin Name");

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userId = firebaseAuth.getCurrentUser().getUid();

        drawerLayout = findViewById(R.id.adminDrawerId);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.adminNavigationId);
        navigationView.setNavigationItemSelectedListener(this);
        hView = navigationView.getHeaderView(0);

        navHeaderUsername = hView.findViewById(R.id.navHeaderUserNameId);
        navHeaderUsername.setText(strName + " (Admin)");

        recyclerView = findViewById(R.id.adminRecyclerViewId);
        gridLayoutManager = new GridLayoutManager(AdminHomeActivity.this,1);
        recyclerView.setLayoutManager(gridLayoutManager);
        donationDataModelList = new ArrayList<>();
        donationPageAdapter = new DonationPageAdapter(AdminHomeActivity.this, donationDataModelList);
        recyclerView.setAdapter(donationPageAdapter);

        fetchPendingOrder(userId);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.homeMenu) {
            /*startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();*/
        }

        if (item.getItemId() == R.id.accountMenu){
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        if (item.getItemId() == R.id.requestMenu){
            startActivity(new Intent(getApplicationContext(), CollectRequestActivity.class));
        }


        if (item.getItemId() == R.id.logoutMenu) {
            firebaseAuth.signOut();
            removeSharedPreference();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void fetchPendingOrder(String userID) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Donation Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        eventListener = databaseReference.child("DonationInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                donationDataModelList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        DonationDataModel donationDataModel = dataSnapshot1.getValue(DonationDataModel.class);
                        if (donationDataModel.getStatus().equals("Pending")){
                            donationDataModelList.add(donationDataModel);
                        }
                    }
                }

                donationPageAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(AdminHomeActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void removeSharedPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginSessionSharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}