package com.example.harshit.vitarandonor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PledgeDonation extends AppCompatActivity implements AdapterView.OnItemSelectedListener, com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int PERMISSION_REQUEST_FINE_LOCATION = 103 ;
    Spinner sitem,scategory,sunit;
    ArrayAdapter adapter;
    EditText eaddress,edetails,equantity;
    CheckBox checkBox;
    TextView elist;
    View focusView = null;
    //Boolean cancel = false;
    String lat,lng;
    RequestQueue rq,rq1,rq2;
    int count=0;
    String[] arr,arrcat;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    String donationURL=Constants.BASE_URL+"insertdonation.php";
    String quantity,iname,cname,unit,sall="",dID,chk="0";
    //String[] pledge = new String[4];
    ArrayList<String> arr1;
    SharedPreferences sharedPreferences;
    Button bpledge,badd,find;
    String showURL = Constants.BASE_URL+"showItem.php";
    String showURLC =Constants.BASE_URL+"showCategory1.php";
String dlat,dlng;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    int mycheck=0;
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pledge_donation);
        sharedPreferences = this.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //chng1
        dID = sharedPreferences.getString(Constants.NAME_SHARED_PREF, "");
        dlat = sharedPreferences.getString(Constants.LAT_SHARED_PREF, "");
        dlng = sharedPreferences.getString(Constants.LNG_SHARED_PREF, "");

        //
        //ch1_start

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //ch1_nd
        sitem = (Spinner)findViewById(R.id.sitem);
        equantity = (EditText)findViewById(R.id.equan);

        scategory = (Spinner)findViewById(R.id.scat);
        sunit = (Spinner)findViewById(R.id.sunit);
        eaddress = (EditText)findViewById(R.id.elocation);
       // eaddress.setVisibility(View.INVISIBLE);

        eaddress.setKeyListener(null);
        edetails = (EditText)findViewById(R.id.edetails);
        edetails.setVisibility(View.INVISIBLE);
        badd = (Button)findViewById(R.id.badd);
        find = (Button)findViewById(R.id.find);
       // find.setVisibility(View.INVISIBLE);
        bpledge = (Button)findViewById(R.id.bpledge);
        rq1 = Volley.newRequestQueue(this);
        checkBox = (CheckBox)findViewById(R.id.checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    mycheck=0;
                    eaddress.setVisibility(View.INVISIBLE);
                find.setVisibility(View.INVISIBLE);
                }
                else{
                    mycheck=1;
                    eaddress.setVisibility(View.VISIBLE);
                    find.setVisibility(View.VISIBLE);
                }
            }
        });

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(PledgeDonation.this, R.array.item_unit, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner
        sunit.setAdapter(adapter3);

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI();

                eaddress.setText("latitude:"+lat+" "+"longitude:"+lng);
                                //sharedPreferences.edit().putString(Constants.LAT_SHARED_PREF,lat);
                                //sharedPreferences.edit().putString(Constants.LNG_SHARED_PREF,lng);
                dlat=lat;
                dlng=lng;

            }
        });

        badd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 quantity=equantity.getText().toString();

                if(equantity.getText().toString().isEmpty()){
                    focusView = equantity;
                    equantity.setError("Cannot Be Blank!");
                    Toast.makeText(getBaseContext(),"Quantity cannot be empty",Toast.LENGTH_SHORT).show();
                    return;
             }




  /*
        Bug-ID 114 related to testcase Quarks-36
        Bug :  Incorrect confirmation toast when location is null
        Solution : we have set a check variable mycheck that will be set to 1 when the default location checkbox is not checked
        */

              if(eaddress.getText().toString().isEmpty()&& mycheck==1){
                     focusView = eaddress;
                    eaddress.setError("Must Click on Find Me!");
                    Toast.makeText(getBaseContext(),"Click on Find Me!",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(edetails.getVisibility()==View.VISIBLE){
                    iname=edetails.getText().toString();
                    if(iname.isEmpty()){
                        focusView = edetails;
                        edetails.setError("Cannot Be Blank!");
                        Toast.makeText(getBaseContext(),"Item Name cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Toast.makeText(getBaseContext(),"Item Succesfully added",Toast.LENGTH_LONG).show();
                count++;

                //trystart

                StringRequest stringRequest = new StringRequest(Request.Method.POST, donationURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> parameter = new HashMap<String, String>();
                        parameter.put("donorID",dID);
                        parameter.put("quantity",quantity);
                        parameter.put("item",iname);
                        parameter.put("units",unit);
                        parameter.put("chk",chk);
                        parameter.put("category",cname);
                        parameter.put("latitude",dlat);
                        parameter.put("longitude",dlng);
                        return parameter;
                    }
                };
                rq1.add(stringRequest);

                //trynd


            }
        });

        bpledge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count!=0)
                {
                Intent i = new Intent(PledgeDonation.this,Success.class);
                //i.putExtra("total",sall);
                startActivity(i);}
                else {
                    Toast.makeText(getApplicationContext(),"Atleast one item should be donated.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        sitem.setOnItemSelectedListener(this);
        scategory.setOnItemSelectedListener(this);
        sunit.setOnItemSelectedListener(this);
        rq = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,showURLC,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONArray items = response.getJSONArray("Category");

                    arrcat = new String[items.length()];
                    for(int i=0;i<items.length();i++)
                    {
                        JSONObject cat = items.getJSONObject(i);
                        String name = cat.getString("tag");
                        //Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
                        arrcat[i]= name;
                    }

// Create an ArrayAdapter using the string array and a default spinner layoutu
                    adapter = new ArrayAdapter(PledgeDonation.this,android.R.layout.simple_spinner_item,arrcat);
// Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                    scategory.setAdapter(adapter);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        rq.add(jsonObjectRequest);

//code change item
// code change end item
        //rq1 = Volley.newRequestQueue(getApplicationContext());



    }


    @Override
    public void onStart() {
        super.onStart();
        //bhaskar added this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
            }
            else{
                //Log.d(TAG, "onStart fired ..............");
                mGoogleApiClient.connect();

            }
        }
        else {

           // Log.d(TAG, "onStart fired ..............");
            mGoogleApiClient.connect();
        }
        //bhaskar added till here
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        //Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
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
        //Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        //Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        //Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {
        //Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {
            String lat1 = String.valueOf(mCurrentLocation.getLatitude());
            String lng1 = String.valueOf(mCurrentLocation.getLongitude());
            lng= lng1;
            lat= lat1;
          /*  tvLocation.setText("At Time: " + mLastUpdateTime + "\n" +
                    "Latitude: " + lat + "\n" +
                    "Longitude: " + lng + "\n" +
                    "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                    "Provider: " + mCurrentLocation.getProvider());*/
        } else {
          //  Log.d(TAG, "location is null ...............");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        //Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
          //  Log.d(TAG, "Location update resumed .....................");
        }
    }
    //asdjakhbsdhabsikdbakshdkahbskdhbakshbdkahbskdhb

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==sitem.getId()){
            iname = parent.getItemAtPosition(position).toString();

            if(iname.equals("Others")){
              edetails.setVisibility(View.VISIBLE);
              }
              else

            {
                edetails.setVisibility(View.INVISIBLE);

            }
           // Toast.makeText(parent.getContext(), "Item Selected: " + iname, Toast.LENGTH_SHORT).show();
        }

        if(parent.getId()==scategory.getId()){
            cname = parent.getItemAtPosition(position).toString();
            //  if(icategory.equals("Others"))
            // other_category.setVisibility(View.VISIBLE);
             arr1 = new ArrayList<>();
           // Toast.makeText(parent.getContext(), "Category Selected: " + cname, Toast.LENGTH_SHORT).show();
            JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.POST, showURL,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        JSONArray items = response.getJSONArray("Items");
                        //  arr = new String[items.length()];
                        for(int i=0;i<items.length();i++)
                        {
                            JSONObject item = items.getJSONObject(i);
                            String name = item.getString("Name");
                            String cat = item.getString("Category");
                            if(cat.equals(cname))
                                arr1.add(name);
                        }
                        arr1.add("Others");
// Create an ArrayAdapter using the string array and a default spinner layoutu
                        adapter = new ArrayAdapter(PledgeDonation.this,android.R.layout.simple_spinner_item,arr1);
// Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                        sitem.setAdapter(adapter);
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            rq.add(jsonObjectRequest1);
        }
        if(parent.getId()==sunit.getId()){
            unit = parent.getItemAtPosition(position).toString();
           // Toast.makeText(parent.getContext(), "Unit Selected: " + unit, Toast.LENGTH_SHORT).show();
            }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
