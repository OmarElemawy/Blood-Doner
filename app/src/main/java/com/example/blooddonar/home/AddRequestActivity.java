package com.example.blooddonar.home;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.blooddonar.R;
import com.example.blooddonar.sing.CheckGPS;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class AddRequestActivity extends AppCompatActivity {

    private Spinner blood_type_spinner;
    private String record;
    TextView locationTextView;
    FusedLocationProviderClient locationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_request);
        blood_type_spinner=findViewById(R.id.blood_need_spinner);
        locationTextView=findViewById(R.id.get_location_txt_add);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        spinnerBloodType();
    }

    private void spinnerBloodType() {
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.blood_type,R.layout.color_spinner);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        blood_type_spinner.setAdapter(adapter);
        blood_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i)
                {
                    case 0:
                        record ="A+";
                        break;
                    case 1:
                        record ="B+";
                        break;
                    case 2:
                        record ="AB+";
                        break;
                    case 3:
                        record ="A-";
                        break;
                    case 4:
                        record ="B-";
                        break;
                    case 5:
                        record ="AB-";
                        break;
                    case 6:
                        record ="O+";
                        break;
                    case 7:
                        record ="O-";
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
    public void back_to_home_post(View view) {
    }

    public void add_post(View view) {

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getLocation(View view) {

        if (!CheckGPS.isLocationEnabled(this)) {
            CheckGPS.displayPromptForEnablingGPS(this);
        } else {
            if (ActivityCompat.checkSelfPermission(AddRequestActivity.this,ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
                return;
            }
            locationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        String countryName = getCountryName(AddRequestActivity.this, location.getLatitude(), location.getLongitude());
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
}
