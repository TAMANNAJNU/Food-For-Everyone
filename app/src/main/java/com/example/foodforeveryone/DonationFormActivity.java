package com.example.foodforeveryone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodforeveryone.model.DonationDataModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DonationFormActivity extends AppCompatActivity {

    private EditText name, address, description;
    private TextView mobile;
    private Button submit;
    private String strName, strMobile, strAddress, strDescription, userID, pushKey, strUserName, strUserMobileNo, strUserEmail, strUserToken;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_form);

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

        name = findViewById(R.id.donationNameId);
        mobile = findViewById(R.id.donationMobileNoId);
        address = findViewById(R.id.donationAddressId);
        description = findViewById(R.id.donationDescriptionId);
        submit = findViewById(R.id.donationSubmitBtnId);

        strMobile = getSharedPreferences("loginSessionSharedPreferences", Context.MODE_PRIVATE).getString("userMobile", "");
        mobile.setText(strMobile);
        strUserName = getSharedPreferences("loginSessionSharedPreferences", Context.MODE_PRIVATE).getString("userName", "");
        strUserMobileNo = getSharedPreferences("loginSessionSharedPreferences", Context.MODE_PRIVATE).getString("userMobile", "");
        strUserEmail = getSharedPreferences("loginSessionSharedPreferences", Context.MODE_PRIVATE).getString("userEmail", "");
        strUserToken = getSharedPreferences("loginSessionSharedPreferences", Context.MODE_PRIVATE).getString("userToken", "");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitFormData();
            }
        });
    }

    private void submitFormData() {
        strName = name.getText().toString().trim();
        strAddress = address.getText().toString().trim();
        strDescription = description.getText().toString().trim();
        userID = firebaseAuth.getCurrentUser().getUid();
        pushKey = databaseReference.push().getKey();

        if (strName.isEmpty()){
            name.setError("Required Field");
            name.requestFocus();
            return;
        }

        if (strAddress.isEmpty()){
            address.setError("Required Field");
            address.requestFocus();
            return;
        }

        if (strDescription.isEmpty()){
            description.setError("Required Field");
            description.requestFocus();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        DonationDataModel donationDataModel = new DonationDataModel(
                strName,
                strMobile,
                strAddress,
                strDescription,
                pushKey,
                userID,
                strUserName,
                strUserMobileNo,
                strUserEmail,
                "Pending",
                strUserToken
        );

        databaseReference.child("DonationInfo")
                .child(userID)
                .child(pushKey)
                .setValue(donationDataModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(DonationFormActivity.this, "Donation Data Saved Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DonationFormActivity.this, HomeActivity.class));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(DonationFormActivity.this,e.getMessage() , Toast.LENGTH_SHORT).show();
                    }
                });



    }
}