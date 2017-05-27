/**
 * Fragment to provide the registration form
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
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private AppCompatButton btn_register, btn_locate_me;
    public EditText et_email, et_password, et_re_password, et_name, et_contact, et_address;
    private TextView tv_login;
    private ProgressBar progress;
    public static double longitude;
    public static double latitude;
    static ArrayList<String> arr;

    LocationManager mlocManager;
    RequestQueue rq;
    String address="";
    static String email;
    //private GPSTracker gps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initViews(view);

        // Adding listener to first name EditText for verifying correct format
        et_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false)
                    isValidName(et_name);
                return;
            }
        });

        // Adding listener to email EditText for verifying correct format
        et_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false)
                    isValidEmailID(et_email);
                return;
            }
        });

        // Adding listener to re_password EditText for verifying correct format
        /*et_re_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP)
                    isValidRePassword(et_password, et_re_password);
                return true;
            }
        });*/

        // Adding listener to address EditText for receiving location
        /*et_address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= et_address.getRight() - et_address.getTotalPaddingRight()) {
                        // your action for drawable click event
                        //gps = new GPSTracker(getActivity());
                        if (gps.canGetLocation()) {
                            double longitude = gps.getLongitude();
                            double latitude = gps.getLatitude();
                            Toast.makeText(getActivity(),"Latitude"+latitude+"\nLongitude"+longitude,Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
                }
                return false;
            }
        });*/

        // Adding listener to password EditText for verifying correct format
        et_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false)
                    isValidPassword(et_password);
                return;

            }
        });

        // Adding listener to contact EditText for verifying correct format
        et_contact.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false)
                    isValidContact(et_contact);
                return;
            }
        });


        return view;
    }

    private void initViews(View view) {

        btn_register = (AppCompatButton) view.findViewById(R.id.btn_register);
        btn_locate_me = (AppCompatButton) view.findViewById(R.id.btn_locate_me);
        tv_login = (TextView) view.findViewById(R.id.tv_login);
        et_name = (EditText) view.findViewById(R.id.et_name);
        et_email = (EditText) view.findViewById(R.id.et_email);
        et_password = (EditText) view.findViewById(R.id.et_password);
        et_re_password = (EditText) view.findViewById(R.id.et_repassword);
        et_contact = (EditText) view.findViewById(R.id.et_contact);
        et_address = (EditText) view.findViewById(R.id.et_address);
        et_address.setKeyListener(null);
        progress = (ProgressBar) view.findViewById(R.id.progress);

        btn_register.setOnClickListener(this);
        btn_locate_me.setOnClickListener(this);
        tv_login.setOnClickListener(this);

        //et_contact.setRawInputType(Configuration.KEYBOARD_12KEY);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_login:
                goToLogin();
                break;

            case R.id.btn_register:
                attemptRegistration();
                break;

            case R.id.btn_locate_me:
                //locate();
                 ((MainActivity)getActivity()).locateDonor();
                et_address.setText(RegisterFragment.latitude+"$"+RegisterFragment.longitude);
                break;
        }

    }

    public void moh() {
et_address.setText(RegisterFragment.latitude+"$"+RegisterFragment.longitude);
     /*   if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            Location lastLocation = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                latitude = lastLocation.getLatitude();
                longitude = lastLocation.getLongitude();
                address=String.valueOf(latitude)+"$"+String.valueOf(longitude);
            et_address.setText(address);
                Toast.makeText(getActivity(), address, Toast.LENGTH_LONG).show();

        } catch (Exception e) {e.printStackTrace();Toast.makeText(getActivity(), "problm_wih_ntwrk", Toast.LENGTH_LONG).show();
        };*/



    }
       private void attemptRegistration() {

        // Reset errors.
        et_name.setError(null);
        et_email.setError(null);
        et_address.setError(null);
        et_contact.setError(null);
        et_password.setError(null);
        et_re_password.setError(null);

        // Store values at the time of the registration attempt.
        String name = et_name.getText().toString();
        String email = et_email.getText().toString();
       // String address = et_address.getText().toString();
        String contact = et_contact.getText().toString();
        String password = et_password.getText().toString();
        //String repassword = et_re_password.getText().toString();

        Boolean cancel = false;
        View focusView = null;

        if(!isValidName(et_name)) {
            focusView = et_name;
            cancel = true;
        }
        if(!isValidEmailID(et_email)) {
            focusView = (focusView==null)?et_email:focusView;
            cancel = true;
        }
        if(!isValidPassword(et_password)) {
            focusView = (focusView==null)?et_password:focusView;
            cancel = true;
        }
        if(!isValidRePassword(et_password, et_re_password)) {
            focusView = (focusView==null)?et_re_password:focusView;
            cancel = true;
        }
        if(!isValidContact(et_contact)) {
            focusView = (focusView==null)?et_contact:focusView;
            cancel = true;
        }
        if(!isValidAddress(et_address)) {
            focusView = (focusView==null)?et_address:focusView;
            cancel = true;
        }
        if (cancel) {
            // There was an error;
            // don't attempt registration and focus the first
            // form field with an error.
            focusView.requestFocus();
            Toast.makeText(getActivity(), "Recheck the fields!", Toast.LENGTH_LONG).show();
            //Snackbar.make(getView(), "Recheck the fields!", Snackbar.LENGTH_LONG).show();
        } else {
            progress.setVisibility(View.VISIBLE);
            registerProcess(name, email, address, contact, password);
        }
    }

    private void registerProcess(final String name, final String email, final String address, final String contact, final String password){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                        if (response.equals("success")) {
                            progress.setVisibility(View.INVISIBLE);
                            goToLogin();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Constants.NAME, name);
                params.put(Constants.PASSWORD, password);
                params.put(Constants.EMAIL, email);
               // params.put(Constants.ADDRESS, address);
                params.put(Constants.CONTACT, contact);
                params.put("longitude", String.valueOf(longitude));
                params.put("latitude", String.valueOf(latitude));

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

        /*Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setContact(contact);
        user.setAddress(address);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.REGISTER_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                Toast.makeText(getActivity(), "Response::"+resp.getMessage(), Toast.LENGTH_LONG).show();
                //Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();
                progress.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

                progress.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG, "failed");

                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                //Snackbar.make(getView(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();

            }
        });
        */
    }


    private boolean isValidName(EditText et) {
        String name = et.getText().toString().trim();
        Pattern p = Pattern.compile("^[a-zA-Z\\s]+$");
        if (name.isEmpty()) {
            et.setError("This field is required");
            return false;
        } else if (!p.matcher(name).find()) { //name.matches("[a-zA-Z]+[\\sa-zA-Z]]");
            et.setError("Invalid name");
            return false;
        }
        return true;
    }

    private boolean isValidEmailID(EditText et) {
        email = et.getText().toString().trim();

                        /*
                            Bug ID-85 related to testcase Quarks-33
                            Bug Status :  Solved
                            Bug : Don't allow redundancy in email
                            Solution : Fetched all emails in arr from database and checking if email already exists in arr.
                        */


        if(arr.contains(email)){
            et.setError("Email address is already registered..!");
            return false;
        }
        if (email.isEmpty()) {
            et.setError("This field is required");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et.setError("Invalid email");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(EditText et) {
        // Code for password specifications.
        String email = et.getText().toString();
        if (email.isEmpty()) {
            et.setError("This field is required");
            return false;
        } //else if( #Match pattern for required password criteria) { #Handle password likewise }
        return true;
    }

    private boolean isValidRePassword(EditText et_pass, EditText et_repass) {
        String pass = et_pass.getText().toString(), repass = et_repass.getText().toString();
        if (repass.isEmpty()) {
            et_repass.setError("This field is required");
            return false;
        } else if (!repass.equals(pass)) {
            et_repass.setError("Passwords don't match");
            return false;
        }
        return true;
    }

    private boolean isValidContact(EditText et) {
        String contact = et.getText().toString();
        Pattern p1 = Pattern.compile("\\b\\d{3}[.-]\\d{3}[.-]\\d{4}\\b");
        Pattern p2 = Pattern.compile("\\b\\d{10}\\b");
        if (contact.isEmpty()) {
            et.setError("This field is required");
            return false;
        } else if (!((contact.length()==12&&p1.matcher(contact).find())|(p2.matcher(contact).find()&&contact.length()==10))) {
            et.setError("Invalid contact");
            return false;
        }
        return true;
    }

    private boolean isValidAddress(EditText et) {
        String address = et.getText().toString();
        if (address.isEmpty()) {
            et.setError("This field is required");
            return false;
        }
        return true;
    }

    private void goToLogin(){
        Fragment login = new LoginFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,login);
        ft.commit();
    }
}
