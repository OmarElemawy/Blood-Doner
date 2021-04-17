package com.example.blooddonar.sing;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.blooddonar.R;
import com.example.blooddonar.home.HomeActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import de.hdodenhof.circleimageview.CircleImageView;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class CreateProfileActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
{
    RelativeLayout date_of_Birth,lastDonation;
    TextView txt_date,txt_lastDonation;
    DatabaseReference myRef;
    TextInputEditText user_name_signUp;
    RadioGroup genderGroup,smokingGroup;
    CircleImageView profile_image;
    FirebaseAuth mAuth;
    BottomSheetDialog dialog;
    private View tack_image_button,select_image_button;
    TextView locationTextView;
    FusedLocationProviderClient locationProviderClient;
    HashMap<String, String> hashMap;
    Geocoder geocoder;
    Spinner blood_type_spinner;
    ProgressBar progressBarProfile;

    DatePickerDialog.OnDateSetListener dateSetListener,dateSetListener1;
    private StorageReference mStorageRef;
    String record;
    private Uri imageUri;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        setupView();
       if(mAuth.getCurrentUser().getPhotoUrl()!=null)
       {
           Picasso.get().load(mAuth.getCurrentUser().getPhotoUrl()).into(profile_image);
       }

        tack_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromCamera();
            }
        });
        select_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromGallery();

            }
        });

        date_of_Birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar=Calendar.getInstance();
                int year=calendar.get(Calendar.YEAR);
                int month=calendar.get(Calendar.MONTH);
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog=new DatePickerDialog
                        (CreateProfileActivity.this
                                ,android.R.style.Theme_Holo_Light_Dialog_MinWidth,dateSetListener,year,month,day);

                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        dateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
               txt_date.setText(i+"/"+i1+"/"+i2);
            }
        };
        lastDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar=Calendar.getInstance();
                int year=calendar.get(Calendar.YEAR);
                int month=calendar.get(Calendar.MONTH);
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog=new DatePickerDialog
                        (CreateProfileActivity.this
                                ,android.R.style.Theme_Holo_Light_Dialog_MinWidth,dateSetListener1,year,month,day);

                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        dateSetListener1= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                txt_lastDonation.setText(i+"/"+i1+"/"+i2);
            }
        };
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        int s = i1 + 1;
        txt_date.setText(i2 + "/" + s + "/" + i);
    }





    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getLocation(View view) {

        if (!CheckGPS.isLocationEnabled(this)) {
            CheckGPS.displayPromptForEnablingGPS(this);
        } else {
            if (ActivityCompat.checkSelfPermission(CreateProfileActivity.this,ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
                return;
            }
            locationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        String countryName = getCountryName(CreateProfileActivity.this, location.getLatitude(), location.getLongitude());
                        locationTextView.setText(countryName);
                    }
                }
            });
        }
    }



    public String getCountryName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }

        return null;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
        date_of_Birth = findViewById(R.id.date_of_birth);
        blood_type_spinner=findViewById(R.id.blood_type_spinner);
        geocoder = new Geocoder(this, Locale.getDefault());
        hashMap = new HashMap<>();
        mAuth = FirebaseAuth.getInstance();
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.blood_type,R.layout.color_spinner);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        blood_type_spinner.setAdapter(adapter);
        blood_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i)
                {
                    case 0:
                        record="A+";
                        break;
                    case 1:
                        record="B+";
                        break;
                    case 2:
                        record="AB+";
                        break;
                    case 3:
                        record="A-";
                        break;
                    case 4:
                        record="B-";
                        break;
                    case 5:
                        record="AB-";
                        break;
                    case 6:
                        record="O+";
                        break;
                    case 7:
                        record="O-";
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        }

        );
    }
    private void setupView() {
        myRef = FirebaseDatabase.getInstance().getReference();
        requestPermission();
        genderGroup=findViewById(R.id.gender_radio);
        smokingGroup=findViewById(R.id.smoking_radio);
        progressBarProfile=findViewById(R.id.progressBarProfile);
        locationTextView = findViewById(R.id.locationTextView);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        user_name_signUp=findViewById(R.id.user_name_signUp);
        txt_date=findViewById(R.id.txt_date);
        profile_image = findViewById(R.id.profile_image_create);
        lastDonation=findViewById(R.id.lastDonation);
        txt_lastDonation=findViewById(R.id.txt_lastDonation);
        View view = getLayoutInflater().inflate(R.layout.dialog_item, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

        tack_image_button = view.findViewById(R.id.tack_image_dialog);
        select_image_button = view.findViewById(R.id.select_image_dialog);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
                 imageUri = data.getData();
        }
        else if (requestCode == 2 && resultCode == RESULT_OK) {
         imageUri = data.getData();
        }
        Picasso.get().load(imageUri).into(profile_image);
        dialog.dismiss();

    }

    private void fromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
    }

    private void fromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }
    public void getProfile(View view) {
        dialog.show();
    }
    private void create() {
        FirebaseUser user = mAuth.getCurrentUser();
        String user_id = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("MoreUsersData").child(user_id);
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("location",locationTextView.getText().toString());
        hashMap.put("date",txt_date.getText().toString());
        hashMap.put("blood_type",record);
        hashMap.put("gender",genderType());
        hashMap.put("isSmoking",smokingType());
        hashMap.put("lastDonation",txt_lastDonation.getText().toString());
        myRef.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CreateProfileActivity.this, "ok", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(CreateProfileActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private String genderType() {
        int checkedRadioButtonId = genderGroup.getCheckedRadioButtonId();
        String s;
        switch (checkedRadioButtonId)
        {
            case R.id.male:
                s="Male";
                break;
            case R.id.female:
                s="Female";
                break;
                default:
                    s="";
        }
        return s;
    }

    private String smokingType() {
        int checkedRadioButtonId = smokingGroup.getCheckedRadioButtonId();
        String s;
        switch (checkedRadioButtonId)
        {
            case R.id.smoking_yes:
                s="yes";
                break;
            case R.id.smoking_no:
                s= "no";
                break;
            default:
                s= "";
        }
        return s;
    }

    private void uploadProfile(FirebaseUser user) {
        Bitmap bitmap = ((BitmapDrawable) profile_image.getDrawable()).getBitmap();
        Uri uri = bitmapToUriConverter(bitmap);
        StorageReference riversRef = mStorageRef.child(user.getUid()).child("Profile/rivers.jpg");
        progressBarProfile.setVisibility(View.VISIBLE);
        riversRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                        {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(CreateProfileActivity.this, "don", Toast.LENGTH_SHORT).show();
                        create();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(CreateProfileActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                long totalByteCount = taskSnapshot.getTotalByteCount();
                long bytesTransferred = taskSnapshot.getBytesTransferred();
                int o = (int) ((bytesTransferred / totalByteCount)*100);
                progressBarProfile.setProgress(o);
            }
        });

    }

    public void SitUserDataButton(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        uploadProfile(user);
    }

    public void skipCreate(View view) {

    }
    public Uri bitmapToUriConverter(Bitmap mBitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 100, 100);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 200, 200,
                    true);
            File file = new File(getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = openFileOutput(file.getName(),
                    Context.MODE_WORLD_READABLE);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
