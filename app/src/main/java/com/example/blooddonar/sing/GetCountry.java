package com.example.blooddonar.sing;


import android.content.Context;
import android.widget.Toast;

import com.example.blooddonar.Country;
import com.example.blooddonar.Example;
import com.example.blooddonar.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

 class GetCountry {


     List<Country> countries(Context context)
    {
        Gson gson = new Gson();
        Example countries = gson.fromJson(loadJSONFromAssets(context), Example.class);
      return countries.getCountries();
    }
     String loadJSONFromAssets(Context context) {
        String json = null;
        try {

            InputStream inputStream = context.getAssets().open("country_cod.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }
}
