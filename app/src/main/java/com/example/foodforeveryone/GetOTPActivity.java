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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GetOTPActivity extends AppCompatActivity {
    private String verificationCodeBySystem;
    private EditText otpCode;
    private String strName, strMobile, strEmail, strPassword, strOTPCode, userID;
    private Button verifyOTPBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private TextView otpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_otpactivity);

        ActionBar bar = getSupportActionBar();
        //bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFFFF")));
        bar.hide();

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.end_color));
        }

        Bundle bundle = getIntent().getExtras();
        strName = bundle.getString("name", "");
        strEmail = bundle.getString("email", "");
        strMobile = bundle.getString("mobileNumber", "");
        strPassword = bundle.getString("password", "");

        otpText = findViewById(R.id.otpTextId);
        otpText.setText("After Successfully Verified You Are Not a Robot, OTP Will Send To Your Number " + strMobile);
        otpCode = findViewById(R.id.otpCodeId);
        verifyOTPBtn = findViewById(R.id.verifyOTPBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //firebaseAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber("+88" + strMobile)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        verifyOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OTPVerifyProcess();
            }
        });
    }

    private void OTPVerifyProcess() {

        strOTPCode = otpCode.getText().toString();

        if (strOTPCode.isEmpty() && strOTPCode.length() < 6) {
            otpCode.setError("Please Enter Valid OTP");
            otpCode.requestFocus();
            return;
        }

        progressDialog = new ProgressDialog(GetOTPActivity.this);
        progressDialog.setMessage("Creating User, Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        verifyCode(strOTPCode);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String codeByUser = phoneAuthCredential.getSmsCode();
            if (codeByUser != null) {
                verifyCode(codeByUser);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(GetOTPActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
        signInUserByCredential(credential);
    }

    private void signInUserByCredential(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (firebaseAuth.getCurrentUser() != null) {

                                firebaseAuth.signOut();

                                firebaseAuth.createUserWithEmailAndPassword(strEmail, strPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        userID = firebaseAuth.getCurrentUser().getUid();
                                        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("name", strName);
                                        user.put("email", strEmail);
                                        user.put("mobileNumber", strMobile);
                                        user.put("password", strPassword);
                                        user.put("userID", userID);
                                        user.put("isAdmin", false);

                                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                databaseReference.child("UserPhnNumList").child(databaseReference.push().getKey()).setValue(strMobile);
                                                databaseReference.child("UserPhnNumList").child(databaseReference.push().getKey()).setValue(strEmail);
                                                Toast.makeText(GetOTPActivity.this, "OTP Verified & Registered Successfully", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();

                                                startActivity(new Intent(GetOTPActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(GetOTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                            }

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(GetOTPActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}