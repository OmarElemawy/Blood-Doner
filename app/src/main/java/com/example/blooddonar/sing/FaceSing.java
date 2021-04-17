package com.example.blooddonar.sing;

import android.os.Bundle;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import org.json.JSONObject;

public class FaceSing
{
    private void load_user_profile(AccessToken accessToken)
    {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken ,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        Log.i("Login",response.toString());
                        Log.i("Login", Profile.getCurrentProfile().toString());
                        if (Profile.getCurrentProfile()!=null)
                        {
                            Profile.getCurrentProfile().getProfilePictureUri(200, 200);
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }
    private void face() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn)load_user_profile(accessToken);
    }
}
