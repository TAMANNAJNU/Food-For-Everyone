package com.example.foodforeveryone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullName, phnNumber, email, password, confirmPass;
    private Button registerBtn;
    private TextView alreadyRegisteredTxt;
    private DatabaseReference databaseReference;
    private List userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getSupportActionBar();
        //bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFFFF")));
        bar.hide();
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.end_color));
        }

        fullName = findViewById(R.id.etName);
        phnNumber = findViewById(R.id.etMobileNo);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etRegistrationPassword);
        confirmPass = findViewById(R.id.etConfirmPassword);

        alreadyRegisteredTxt = findViewById(R.id.alreadyRegisteredId);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        registerBtn = findViewById(R.id.registerButton);

        userList = new ArrayList();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRegistration();
            }
        });

        alreadyRegisteredTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void doRegistration() {

        String strFullName = fullName.getText().toString().trim();
        String strPhnNum = phnNumber.getText().toString().trim();
        String strEmail = email.getText().toString().trim();
        String strPass = password.getText().toString().trim();
        String strConPass = confirmPass.getText().toString().trim();

        if (strFullName.isEmpty()){
            fullName.setError("Full Name is Required");
            fullName.requestFocus();
            return;
        }

        if (strPhnNum.isEmpty()){
            phnNumber.setError("Mobile Number is Required");
            phnNumber.requestFocus();
            return;
        }

        if (strEmail.isEmpty()){
            email.setError("Email is Required");
            email.requestFocus();
            return;
        }

        if (strPass.isEmpty()){
            password.setError("Password is Required");
            password.requestFocus();
            return;
        }

        if (strPass.length() < 6){
            password.setError("Password Must be >= 6 Characters");
            password.requestFocus();
            return;
        }

        if (strConPass.isEmpty()){
            confirmPass.setError("Confirm Password is Required");
            confirmPass.requestFocus();
            return;
        }

        if (!strPass.equals(strConPass)){
            confirmPass.setError("Password Don't Match");
            confirmPass.requestFocus();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        databaseReference.child("UserPhnNumList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    userList.add(dataSnapshot.getValue());
                }
                if (!userList.isEmpty() || userList != null){
                    if (userList.contains(strPhnNum) || userList.contains(strEmail)){
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Your Mobile Number Or Email Already Registered", Toast.LENGTH_LONG).show();
                        return;
                    } else{
                        Intent intent = new Intent(RegisterActivity.this, GetOTPActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", strFullName);
                        bundle.putString("email", strEmail);
                        bundle.putString("mobileNumber", strPhnNum);
                        bundle.putString("password", strPass);
                        intent.putExtras(bundle);
                        progressDialog.dismiss();
                        startActivity(intent);
                        finish();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Something Went Wrong, Please Try Again Later", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Something Went Wrong, Please Try Again Later", Toast.LENGTH_LONG).show();
                return;
            }
        });

        /*ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(strEmail, strPass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                userID = firebaseAuth.getCurrentUser().getUid();
                DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                Map<String,Object> user = new HashMap<>();
                user.put("name",strFullName);
                user.put("email",strEmail);
                user.put("mobileNumber",strPhnNum);
                user.put("password", strPass);
                user.put("userID", userID);

                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });*/
    }
}