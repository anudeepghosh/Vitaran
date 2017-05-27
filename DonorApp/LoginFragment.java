/**
 * Fragment to provide the Login form
 *
 * @author Debanjana Kar
 * Created on 03-05-2017.
 *
 */

package com.vitaran.donor;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginFragment extends Fragment implements View.OnClickListener{

    private AppCompatButton btn_login;
    private EditText et_email,et_password;
    private TextView tv_register;
    private ProgressBar progress;
    private SharedPreferences pref;

    //boolean variable to check user is logged in or not
    //initially it is false
    private boolean loggedIn = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference
        //SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //Toast.makeText(getActivity(),"Login Fragment Resumed : "+pref.getBoolean(Constants.LOGGEDIN_SHARED_PREF,false), Toast.LENGTH_LONG).show();
        /*//Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(Constants.LOGGEDIN_SHARED_PREF, false);

        //If we will get true
        if(loggedIn){
            //We will start the Profile Activity
            //Intent intent = new Intent(getActivity(), ProfileFragment.class);
            //startActivity(intent);
            ((MainActivity)getActivity()).goToProfile();
        }*/
        //initViews(this.getView());
    }

    private void initViews(View view){

        pref = getActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        btn_login = (AppCompatButton)view.findViewById(R.id.btn_login);
        tv_register = (TextView)view.findViewById(R.id.tv_register);
        et_email = (EditText)view.findViewById(R.id.et_email);
        et_password = (EditText)view.findViewById(R.id.et_password);

        progress = (ProgressBar)view.findViewById(R.id.progress);

        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);

       // Toast.makeText(getActivity(), "LogIn Fragment Opened : stateOf -> Constants.LOGGEDIN_SHARED_PREF : " + pref.getBoolean(Constants.LOGGEDIN_SHARED_PREF, false), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.tv_register:
                goToRegister();
                break;

            case R.id.btn_login:
                attemptLogin();
                break;
        }
    }

    private void attemptLogin() {

        // Reset errors.
        et_email.setError(null);
        et_password.setError(null);

        // Store values at the time of the registration attempt.
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        Boolean cancel = false;
        View focusView = null;

        if (!isValidEmailID(et_email)) {
            focusView = et_email;
            cancel = true;
        }
        if (!isValidPassword(et_password)) {
            focusView = (focusView==null)?et_password:focusView;
            cancel = true;
        }
        if (cancel) {
            // There was an error;
            // don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            Toast.makeText(getActivity(), "Recheck the fields!", Toast.LENGTH_LONG).show();
            //Snackbar.make(getView(), "Recheck the fields!", Snackbar.LENGTH_LONG).show();
        } else {
            progress.setVisibility(View.VISIBLE);
            loginProcess(email, password);

        }
    }

    private void loginProcess(final String email, final String password){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String[] serverResponse = getJSONData(response);

                        for(String p : serverResponse){

                           // Toast.makeText(getActivity(),p,Toast.LENGTH_LONG).show();

                        }
                        //Toast.makeText(getActivity(),"len="+serverResponse.length,Toast.LENGTH_LONG).show();

                        if (serverResponse[0].equalsIgnoreCase(Constants.LOGIN_SUCCESS)) {
                            Toast.makeText(getActivity(), "Login Success", Toast.LENGTH_LONG).show();

                            //Creating a shared preference
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                            //Creating editor to store values to shared preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            //Adding values to editor
                            editor.putBoolean(Constants.LOGGEDIN_SHARED_PREF, true);
                            editor.putString(Constants.EMAIL_SHARED_PREF, email);
                            editor.putString(Constants.NAME_SHARED_PREF, serverResponse[1]);
                            editor.putString(Constants.LAT_SHARED_PREF, serverResponse[2]);
                            //Toast.makeText(getActivity(),"lat_is:"+serverResponse[1]+"\n"+serverResponse[2]+"\n"+serverResponse[3],Toast.LENGTH_LONG).show();
                            editor.putString(Constants.LNG_SHARED_PREF, serverResponse[3]);


                            //Saving values to editor
                            editor.commit();

                            //Starting profile activity
                            /*Intent intent = new Intent(getActivity(), ProfileFragment.class);
                            startActivity(intent);*/
                            String result = "Closing Login Fragment : "+pref.getBoolean(Constants.LOGGEDIN_SHARED_PREF,false);
                            //Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                            //Toast.makeText(getActivity(), result + "Logging in as : " + pref.getString(Constants.NAME_SHARED_PREF,"Not found"), Toast.LENGTH_LONG).show();
                            et_email.setText("");
                            et_password.setText("");

                            progress.setVisibility(View.INVISIBLE);
                            ((MainActivity)getActivity()).goToProfile();

                        }else{
                            //If the server response is not success
                            //Displaying an error message on toast
                            Toast.makeText(getActivity(), "Invalid username or password", Toast.LENGTH_LONG).show();
                            progress.setVisibility(View.INVISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),error.toString(), Toast.LENGTH_LONG).show();
                        progress.setVisibility(View.INVISIBLE);
                    }
                }){
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Constants.PASSWORD, password);
                params.put(Constants.EMAIL, email);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

        /**Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.LOGIN_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(Constants.IS_LOGGED_IN,true);
                    editor.putString(Constants.EMAIL,resp.getUser().getEmail());
                    editor.putString(Constants.NAME,resp.getUser().getName());
                    editor.putString(Constants.UNIQUE_ID,resp.getUser().getUnique_id());
                    editor.apply();
                    goToProfile();

                }
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

                progress.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG,"failed");
                Snackbar.make(getView(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();

            }
        });*/
    }

    private String[] getJSONData(String response) {
        String[] resp = new String[4];
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.has(Constants.LOGIN_RESPONSE_SUCCESS)) {
                JSONArray result = jsonObject.getJSONArray(Constants.LOGIN_RESPONSE_SUCCESS);
                JSONObject data = result.getJSONObject(0);
                resp[0] = "success";
                String[] arr = data.getString(Constants.NAME_SHARED_PREF).split("Z");
                resp[1] = arr[0];
                resp[2] = arr[1];
                resp[3] = arr[2];

            } else {
                JSONArray result = jsonObject.getJSONArray(Constants.LOGIN_RESPONSE_FAILURE);
                JSONObject data = result.getJSONObject(0);
                resp[0] = "failure";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resp;
    }

    private boolean isValidEmailID(EditText et) {
        String email = et.getText().toString().trim();
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

    private void goToRegister(){
        Fragment register = new RegisterFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,register);
        ft.commit();
    }

    /*private void goToProfile(){
        Fragment profile = new ProfileFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,profile);
        ft.commit();
    }*/
}
