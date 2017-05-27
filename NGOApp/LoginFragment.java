/**
 *  Fragment to provide the Login form
 *  @author Anudeep Ghosh
 *  Created on 03-05-17
 */

package com.vitaran.ngo;

import android.app.Fragment;
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

    /**
     * Default function of Fragment class overridden
     * Runs as soon as the Activity is created
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initViews(view);
        return view;
    }

    /**
     * Default function of Fragment class overridden
     * Runs when the Fragment is reopened
     */
    @Override
    public void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference
        //SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //Toast.makeText(getActivity(),"Login Fragment Resumed : "+pref.getBoolean(Constants.LOGGEDIN_SHARED_PREF,false),Toast.LENGTH_LONG).show();
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

    /**
     * Initializes the different elements present in the Fragment
     * @param view
     */
    private void initViews(View view){

        pref = getActivity().getSharedPreferences(Constants.SHARED_PREF_NAME,Context.MODE_PRIVATE);

        btn_login = (AppCompatButton)view.findViewById(R.id.btn_login);
        et_email = (EditText)view.findViewById(R.id.et_email);
        et_password = (EditText)view.findViewById(R.id.et_password);

        progress = (ProgressBar)view.findViewById(R.id.progress);

        btn_login.setOnClickListener(this);

        //Toast.makeText(getActivity(), "LogIn Fragment Opened : stateOf -> Constants.LOGGEDIN_SHARED_PREF : " + pref.getBoolean(Constants.LOGGEDIN_SHARED_PREF, false), Toast.LENGTH_LONG).show();
    }

    /**
     * Implements the functions of the OnClickListener class
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_login:
                attemptLogin();
                break;
        }
    }

    /**
     * Called when the LogIn Button is clicked
     * Checks for various element validity before
     * proceeding with Login
     */
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

    /**
     * Called to send the user details necessary for logging in
     * @param email
     * @param password
     */
    private void loginProcess(final String email, final String password){

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(getActivity(),response,Toast.LENGTH_SHORT).show();
                        String[] serverResponse = getJSONData(response);

                        if (serverResponse[0].equalsIgnoreCase(Constants.LOGIN_SUCCESS)) {
                            //Toast.makeText(getActivity(), "Login Success", Toast.LENGTH_LONG).show();

                            //Creating a shared preference
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                                    Constants.SHARED_PREF_NAME,Context.MODE_PRIVATE);

                            //Creating editor to store values to shared preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            //Adding values to editor
                            editor.putBoolean(Constants.LOGGEDIN_SHARED_PREF, true);
                            editor.putString(Constants.EMAIL_SHARED_PREF, email);
                            editor.putString(Constants.NAME_SHARED_PREF, serverResponse[1]);

                            //Saving values to editor
                            editor.commit();
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
                        Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
                        progress.setVisibility(View.INVISIBLE);
                    }
                }){
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Constants.PASSWORD, changePostParam(password));
                params.put(Constants.EMAIL, changePostParam(email));
                return params;
            }
        };
        requestQueue.add(stringRequest);
        //Toast.makeText(getActivity(),""+stringRequest,Toast.LENGTH_SHORT).show();
    }

    /**
     * Accepts the JSON response received from the server,
     * and parse it into simpler readable format
     * @param response
     * @return
     */
    private String[] getJSONData(String response) {
        String[] resp = new String[2];
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.has(Constants.LOGIN_RESPONSE_SUCCESS)) {
                JSONArray result = jsonObject.getJSONArray(Constants.LOGIN_RESPONSE_SUCCESS);
                JSONObject data = result.getJSONObject(0);
                resp[0] = "success";
                resp[1] = data.getString(Constants.NAME_SHARED_PREF);
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

    /**
     * Checks for the validity of the email-ID
     * @param et
     * @return
     */
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

    /**
     * Checks for the validity of the password
     * @param et
     * @return
     */
    private boolean isValidPassword(EditText et) {
        // Code for password specifications.
        String email = et.getText().toString();
        if (email.isEmpty()) {
            et.setError("This field is required");
            return false;
        } //else if( #Match pattern for required password criteria) { #Handle password likewise }
        return true;
    }

    /**
     * Handling server PHP version difference between test server and deployment server
     * @param str
     * @return
     */
    public String changePostParam(String str) {
        if (Constants.BASE_URL.equalsIgnoreCase("http://vitaran.000webhostapp.com/")){
            return "\""+str+"\"";
        }
        return str;
    }
}
