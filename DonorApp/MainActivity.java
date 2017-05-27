/**
 * Class to provide base for holding the Fragments
 * 
 * @author Debanjana Kar
 * Created on 03-05-2017.
 *
 */

package com.vitaran.donor;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.browse.MediaBrowser;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements OpenProfileInterface, com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //bhaskar added this
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 102;
    //bhaskar added till here
    private SharedPreferences pref;// = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
    RequestQueue rq;
    private double longitude;
    private double latitude;
    String showURLdonor=Constants.BASE_URL+"showDonor.php";
    ArrayList<String> arrdonor;
    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }@Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);


        initFragment();
        Log.d(TAG, "onCreate ...............................");
        //show error dialog if GoolglePlayServices not available
      //  if (!isGooglePlayServicesAvailable()) {
        //    finish();
        //}
        rq = Volley.newRequestQueue(this);

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,showURLdonor,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONArray items = response.getJSONArray("donor");

                    arrdonor = new ArrayList<>();
                    for(int i=0;i<items.length();i++)
                    {
                        JSONObject cat = items.getJSONObject(i);
                        String mail1 = cat.getString("email");
                        //Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
                        //  if(mail1.equals(RegisterFragment.email))
                        //  {
                        //      et_email.setError("Invalid name");
                        //  }
                        arrdonor.add(mail1);
                    }
RegisterFragment.arr=arrdonor;
// Create an ArrayAdapter using the string array and a default spinner layoutu
                    // adapter = new ArrayAdapter(PledgeDonation.this,android.R.layout.simple_spinner_item,arrcat);
// Specify the layout to use when the list of choices appears
                    // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                    // scategory.setAdapter(adapter);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        rq.add(jsonObjectRequest);

        //end of testcode for array


    }
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    private void initFragment() {
       // Toast.makeText(this, "Main Activity Opened : " + pref.getBoolean(Constants.LOGGEDIN_SHARED_PREF, false), Toast.LENGTH_LONG).show();
        if (pref.getBoolean(Constants.LOGGEDIN_SHARED_PREF, false)) {
           // Toast.makeText(this, "Opening Profile Activity : " + pref.getBoolean(Constants.LOGGEDIN_SHARED_PREF, false), Toast.LENGTH_LONG).show();
            goToProfile();
        } else {
            Fragment fragment = new LoginFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_frame, fragment);
            ft.commit();
        }
    }

    public void locateDonor() {
        updateUI();

    }
    //getting json array from donor table for email check

    @Override
    public void goToProfile() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
        //finish();
    }



    @Override
    public void onStart() {

        super.onStart();

       /*
        Bug-ID 68 related to testcase Quarks-6
        Bug Status :  Solved
        Bug : Location Tracking does not work
        Solution : Location Tracking works, using Fused Location now.
        */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
            }
            else{
                Log.d(TAG, "onStart fired ..............");
                mGoogleApiClient.connect();

            }
        }
        else {

            Log.d(TAG, "onStart fired ..............");
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }




/*private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            Toast.makeText(get,""+locationAddress, Toast.LENGTH_SHORT).show();
            //.setText(locationAddress);
        }
    }
    */

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());
            RegisterFragment.longitude= Double.parseDouble(lng);
            RegisterFragment.latitude= Double.parseDouble(lat);
          /*  tvLocation.setText("At Time: " + mLastUpdateTime + "\n" +
                    "Latitude: " + lat + "\n" +
                    "Longitude: " + lng + "\n" +
                    "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                    "Provider: " + mCurrentLocation.getProvider());*/
        } else {
            Log.d(TAG, "location is null ...............");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
       // LocationServices.FusedLocationApi.removeLocationUpdates(
             //   mGoogleApiClient, this);
       // Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }
}
