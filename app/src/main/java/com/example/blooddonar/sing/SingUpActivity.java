package com.example.blooddonar.sing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import com.example.blooddonar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class SingUpActivity extends AppCompatActivity{
    TextInputEditText user_name_signUp,email_sing_up,pass_signUp,confirm_Pass_signUp;
    private FirebaseAuth mAuth;
    private ProgressDialog pdialog;
    FirebaseDatabase myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initialsVariables();
     }


    private void updateUI(FirebaseUser currentUser,String name)
    {
        writeNewUser(currentUser.getUid(),name);
    }


    public void sing_up_with_email(View view)
    {
        String email = email_sing_up.getText().toString();
        String password = pass_signUp.getText().toString();
        String c_pass = confirm_Pass_signUp.getText().toString();
        String name = user_name_signUp.getText().toString();
        if (name.length()<4)
        {
            Toast.makeText(this, "please chick the Name", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "please chick the email form", Toast.LENGTH_SHORT).show();
        }

        else if (!password.equals(c_pass))
        {
            Toast.makeText(this, "please chick confirm password", Toast.LENGTH_SHORT).show();

        }
        else if (password.length()< 8)
        {
            Toast.makeText(this, "password must be at least 8 characters", Toast.LENGTH_SHORT).show();
        }
        else
        {
            pdialog.setTitle("Registering User");
            pdialog.setCanceledOnTouchOutside(false);
            pdialog.setMessage("Please Wait while Creating the Account");
            pdialog.show();
            createUser(email,password,name);

        }
    }
    private void createUser(String email, String password, final String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SingUpActivity.this, "Authentication success", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            pdialog.dismiss();
                            updateUI(user,name);
                        } else {
                            Toast.makeText(SingUpActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            pdialog.dismiss();
                            email_sing_up.setText("");

                        }
                    }
                });
    }
    private void initialsVariables()
    {
        mAuth = FirebaseAuth.getInstance();
        pdialog = new ProgressDialog(this);
        user_name_signUp=findViewById(R.id.user_name_signUp);
        email_sing_up=findViewById(R.id.email_sing_up);
        confirm_Pass_signUp=findViewById(R.id.confirm_Pass_signUp);
        pass_signUp=findViewById(R.id.pass_signUp);
        myRef = FirebaseDatabase.getInstance();
    }
    private void writeNewUser(String userId, String name) {

        DatabaseReference child = myRef.getReference("Users").child(userId).child("UserName");
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("name", name);
        child.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent=new Intent(SingUpActivity.this,CreateProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
