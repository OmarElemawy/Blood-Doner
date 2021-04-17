package com.example.blooddonar.sing;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.blooddonar.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import java.util.concurrent.TimeUnit;


public class PhoneSingActivity extends AppCompatActivity {
    TextInputEditText code_verification;
    private String phone;
    private FirebaseAuth mAuth;
    private String verificationCode;
    private DatabaseReference myRef;
    ProgressBar bar;
    FirebaseUser oo;
    private boolean i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_sing);
        code_verification=findViewById(R.id.code_verification);
        mAuth = FirebaseAuth.getInstance();
        bar=findViewById(R.id.progress_bar);
        oo= (FirebaseUser)getIntent().getExtras().get("oo");

        Bundle extras = getIntent().getExtras();
        if (extras!=null&&extras.containsKey("phone")) {
            phone = extras.getString("phone");
        }
        singIn("+205555555555");
    }

    public void submit_verification(View view) {
        String code= code_verification.getText().toString().trim();
        if (code.isEmpty()||code.length()<6)
        {
            code_verification.setError("Enter code...");
            code_verification.requestFocus();
            return;
        }
        bar.setVisibility(View.VISIBLE);
        verify_code(code);
        }

    private void verify_code(String code)
    {
         PhoneAuthCredential phoneAuthCredential=PhoneAuthProvider.getCredential(verificationCode,code);
         mAuth.getCurrentUser().updatePhoneNumber(phoneAuthCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
             @Override
             public void onSuccess(Void aVoid) {
                 Toast.makeText(PhoneSingActivity.this, "Ok", Toast.LENGTH_SHORT).show();
                 Intent intent=new Intent(PhoneSingActivity.this,CreateProfileActivity.class);
                 startActivity(intent);
                 finish();

             }
         });


    }
    private void singIn(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            if (phoneAuthCredential.getSmsCode()!=null)
            {
                bar.setVisibility(View.VISIBLE);
                verify_code(phoneAuthCredential.getSmsCode());
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneSingActivity.this, "Please Chick The Phone Number", Toast.LENGTH_SHORT).show();
            Log.i("ooooo",e.getMessage());
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCode =s;
        }
    };

}







  /*  mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                 if (task.isSuccessful())
                 {

                     final FirebaseDatabase database = FirebaseDatabase.getInstance();
                         myRef = database.getReference("UsersData").child(mAuth.getUid());
                         myRef.addValueEventListener(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                 i = dataSnapshot.exists();

                             }
                             @Override
                             public void onCancelled(DatabaseError error) {
                                 Log.w("oooo", "Failed to read value", error.toException());
                             }
                         });
                 }
                 else
                 {
                     Toast.makeText(PhoneSingActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                 }
             }
         });*/
      /*  if (i) {
            Intent intent = new Intent(PhoneSingActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        else if (!i) {
            Intent intent = new Intent(PhoneSingActivity.this, CreateProfileActivity.class);
            startActivity(intent);
            finish();
        }*/