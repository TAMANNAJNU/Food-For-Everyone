package com.example.foodforeveryone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView navHeaderUsername, name;
    private View hView;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String strName;
    private Button donateBtn, collectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strName = getSharedPreferences("loginSessionSharedPreferences", Context.MODE_PRIVATE).getString("userName", "User Name");
        setContentView(R.layout.activity_home);

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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        name = findViewById(R.id.nameId);

        drawerLayout = findViewById(R.id.drawerId);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.navigationId);
        navigationView.setNavigationItemSelectedListener(this);
        hView = navigationView.getHeaderView(0);

        name.setText("Hi, " + strName);
        navHeaderUsername = hView.findViewById(R.id.navHeaderUserNameId);
        navHeaderUsername.setText(strName);

        donateBtn = findViewById(R.id.donationId);
        collectBtn = findViewById(R.id.collectId);

        donateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, DonationFormActivity.class));
            }
        });

        collectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, DonationPageActivity.class));
            }
        });

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

        if (item.getItemId() == R.id.homeMenuId) {
            /*startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();*/
        }

        if (item.getItemId() == R.id.accountMenuId){
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        if (item.getItemId() == R.id.foodCollectMenuId){
            startActivity(new Intent(getApplicationContext(), DonationPageActivity.class));
            //finish();
        }

        if (item.getItemId() == R.id.logoutMenuId) {
            firebaseAuth.signOut();
            removeSharedPreference();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        if (item.getItemId() == R.id.donateMenuId){
            startActivity(new Intent(getApplicationContext(), DonationFormActivity.class));
            //finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void removeSharedPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginSessionSharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}