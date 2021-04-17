package com.example.blooddonar.sing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blooddonar.R;
import com.example.blooddonar.home.HomeActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private FirebaseDatabase myRef;
    private static final int RC_SIGN_IN = 0;
    private GoogleSignInClient mGoogleSignInClient;
    private String i;
    TextInputEditText emailInputEditText,passInputEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        i=null;
        mAuth=FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        callbackManager = CallbackManager.Factory.create();
        emailInputEditText=findViewById(R.id.email_sing_in);
        passInputEditText=findViewById(R.id.pass_signIn);

    }

    public void singInButton(View view) {
        String email = emailInputEditText.getText().toString();
        String pass = passInputEditText.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "please chick the email form", Toast.LENGTH_SHORT).show();
        }else if (pass.equals(""))
        {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            create(email,pass);
        }

    }
    private void create(String email,String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignInActivity.this, "success", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInActivity.this, "Authentication failed because "+task.getException(),Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

    }

    public void face_sing(View view)
    {
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        loadProfileFace(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException exception) {

                    }
                });
    }

    public void google_sing(View view) {
        signInGoogle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

            }
        }
    }
    private void loadProfileFace(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("ooo", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUIForFaceGoogle(user);
                        }
                        else
                        {
                            Log.i("ooo", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void updateUIForFaceGoogle(final FirebaseUser user) {

        myRef = FirebaseDatabase.getInstance();
        DatabaseReference child = myRef.getReference("Users");
        child.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (i==null) {
                if (dataSnapshot.hasChild(mAuth.getUid())) {
                    i = "t";
                }
                else {
                    i = "f";
                }
            }
            if (i.equals("f"))
            {
                    writeNewUser(mAuth.getUid(),mAuth.getCurrentUser().getDisplayName());
            }
            else if (i.equals("t")) {
                    Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
            }

            }
            @Override
            public void onCancelled(DatabaseError error)
            {
                Log.w("oooo", "Failed to read value", error.toException());
                finish();
            }

        });

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("ooo", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("oooo", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUIForFaceGoogle(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("ooo", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void GOSingUp(View view) {
        Intent intent=new Intent(this,SingUpActivity.class);
        startActivity(intent);
    }
    private void writeNewUser(String userId, String name) {
        myRef=FirebaseDatabase.getInstance();
        DatabaseReference child = myRef.getReference("Users").child(userId).child("UserName");
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("name", name);
        child.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent=new Intent(SignInActivity.this,CreateProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}





  //intent to phone validation
  /*  private TextInputEditText email_signUp;
    private Spinner country_spinner;
    List<Country> countries;*/

      /*  String email_text = email_signUp.getText().toString();
        if (email_text.isEmpty())
        {
            Toast.makeText(this, "Phone Number is Empty", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent=new Intent(this,PhoneSingActivity.class);
            int selectedItemPosition = country_spinner.getSelectedItemPosition();
            Country country = countries.get(selectedItemPosition);
            intent.putExtra("phone", country.getCode()+email_text);
            startActivity(intent);
        }*/

             /* GetCountry GetCountry = new GetCountry();
        countries = GetCountry.countries(this);
        email_signUp = findViewById(R.id.phone_number_editText);
        country_spinner = findViewById(R.id.country_spinner);
        String[] names = new String[countries.size()];
        for (int i = 0; i < countries.size(); i++) {
            names[i] = countries.get(i).getName();
        }
        country_spinner.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_item, names));
*/