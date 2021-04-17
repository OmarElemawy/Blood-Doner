package com.example.blooddonar.home;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.example.blooddonar.R;
import com.example.blooddonar.sing.SignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.example.blooddonar.home.ui.main.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    ImageView menu_button;
    private FirebaseAuth mAuth;
    TextView home_username;
    ImageView home_profile;
    private StorageReference mStorageRef;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        menu_button = findViewById(R.id.menu_home);
        mAuth = FirebaseAuth.getInstance();
        home_username = findViewById(R.id.home_username);
        home_profile = findViewById(R.id.home_profile);
        if (mAuth.getCurrentUser()==null)
        {
            Drawable drawable = getResources().getDrawable(R.drawable.log_in_image);
            home_profile.setImageDrawable(drawable);
            home_username.setText("blood Donor");
        }
        else
        {
            StorageReference pathReference = mStorageRef.child(mAuth.getCurrentUser().getUid()).child("Profile/rivers.jpg");
            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(home_profile);
                }
            });
            FirebaseDatabase myRef;
            myRef= FirebaseDatabase.getInstance();
            DatabaseReference child = myRef.getReference("Users").child(mAuth.getUid()).child("UserName");
            child.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   String value = dataSnapshot.child("name").getValue().toString();
                   home_username.setText(value);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }


    public void menu_click(View view) {
        PopupMenu menu=new PopupMenu(HomeActivity.this,menu_button);
        menu.getMenuInflater().inflate(R.menu.home,menu.getMenu());
        Toast.makeText(this, "omar", Toast.LENGTH_SHORT).show();
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId==R.id.log_out){
                    mAuth.signOut();
                    Intent intent=new Intent(HomeActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });
        menu.show();
    }

    public void addRequest(View view) {
      Intent intent=new Intent(this,AddRequestActivity.class);
      startActivity(intent);

    }
}