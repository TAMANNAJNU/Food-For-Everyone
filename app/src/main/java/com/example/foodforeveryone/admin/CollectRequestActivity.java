package com.example.foodforeveryone.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.foodforeveryone.DonationPageActivity;
import com.example.foodforeveryone.R;
import com.example.foodforeveryone.adapter.DonationPageAdapter;
import com.example.foodforeveryone.model.DonationDataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CollectRequestActivity extends AppCompatActivity {

    private String userID;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private DonationPageAdapter donationPageAdapter;
    private RecyclerView recyclerView;
    private List<DonationDataModel> donationDataModelList;
    private ValueEventListener eventListener;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_request);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#636A6C")));
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        //bar.hide();

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.end_color));
        }

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userID = firebaseAuth.getCurrentUser().getUid();
        recyclerView = findViewById(R.id.requestRecyclerViewId);
        gridLayoutManager = new GridLayoutManager(CollectRequestActivity.this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        donationDataModelList = new ArrayList<>();
        donationPageAdapter = new DonationPageAdapter(CollectRequestActivity.this, donationDataModelList);
        recyclerView.setAdapter(donationPageAdapter);
        fetchRequestData(userID);
    }

    private void fetchRequestData(String userID) {
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
                        if (donationDataModel.getStatus().equals("Requested")){
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
                Toast.makeText(CollectRequestActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}