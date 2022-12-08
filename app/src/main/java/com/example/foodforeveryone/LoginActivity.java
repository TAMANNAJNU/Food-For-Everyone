package com.example.foodforeveryone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.example.foodforeveryone.admin.AdminHomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn, registerFromLoginBtn;
    private EditText etEmail, etPassword;
    private TextView forgotPass;
    private LayoutInflater inflater;
    private AlertDialog.Builder reset_alert;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference databaseReference;
    private String userID;
    private String firebaseToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getSupportActionBar();
        //bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFFFF")));
        bar.hide();
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.end_color));
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);

        forgotPass = findViewById(R.id.forgotPassId);

        loginBtn = findViewById(R.id.loginBtnId);
        registerFromLoginBtn = findViewById(R.id.registerFromLoginId);

        reset_alert = new AlertDialog.Builder(this);
        inflater = this.getLayoutInflater();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });

        registerFromLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doPasswordRecovery();
            }
        });

    }
/*
    @Override
    protected void onStart() {
        super.onStart();

        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
    }*/

    private void doPasswordRecovery() {
        View view = inflater.inflate(R.layout.reset_pop, null);

        reset_alert.setTitle("Reset Forgot Password?")
                .setMessage("Enter Your Email to get Password Reset link")
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //validate the email address
                        EditText email = view.findViewById(R.id.forgotEmailId);
                        if (email.getText().toString().isEmpty()){
                            email.setError("Required Field");
                            return;
                        }

                        //send the reset link
                        firebaseAuth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this, "Reset Email Send", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }).setNegativeButton("Cancel", null)
                .setView(view)
                .create().show();
    }

    private void doLogin() {

        String strEmail = etEmail.getText().toString();
        String strPassword = etPassword.getText().toString();

        if (strEmail.isEmpty()){
            etEmail.setError("Required Field");
            etEmail.requestFocus();
            return;
        }

        if (strPassword.isEmpty()){
            etPassword.setError("Required Field");
            etPassword.requestFocus();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(strEmail, strPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                userID = firebaseAuth.getCurrentUser().getUid();
                /*FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()){
                            firebaseToken = task.getResult();
                            //databaseReference.child("UserLoginToken").child(userID).setValue(firebaseToken);
                            saveLoginSessionData(userID, progressDialog, firebaseToken);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        firebaseAuth.signOut();
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Token Doesn't Save, Please Login Again", Toast.LENGTH_LONG).show();
                    }
                });*/
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()){
                            firebaseToken = task.getResult();
                            saveLoginSessionData(userID, progressDialog, firebaseToken);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        firebaseAuth.signOut();
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Token Doesn't Save, Please Login Again", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void saveLoginSessionData(String userID, ProgressDialog progressDialog, String firebaseToken) {
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                SharedPreferences sharedPreferences = getSharedPreferences("loginSessionSharedPreferences", Context.MODE_PRIVATE);
                if (documentSnapshot.exists()){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userName", documentSnapshot.getString("name"));
                    editor.putString("userEmail", documentSnapshot.getString("email"));
                    editor.putString("userMobile", documentSnapshot.getString("mobileNumber"));
                    editor.putString("userToken", firebaseToken);
                    editor.putBoolean("isAdmin", documentSnapshot.getBoolean("isAdmin"));
                    editor.apply();

                    databaseReference.child("UserLoginToken").child(documentSnapshot.getString("mobileNumber")).setValue(firebaseToken);
                }


                progressDialog.dismiss();

                if (sharedPreferences.getBoolean("isAdmin", false)){
                    startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Session Data Doesn't Save, Please Login Again", Toast.LENGTH_LONG).show();
            }
        });

    }
}