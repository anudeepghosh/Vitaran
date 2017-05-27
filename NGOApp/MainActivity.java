/**
 * Class to provide base for holding the Fragments
 * @author Anudeep Ghosh
 * Created on 03-05-2017.
 */
package com.vitaran.ngo;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements OpenProfileInterface{

    private SharedPreferences pref;// = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
    private double longitude;
    private double latitude;

    /**
     * Default function of AppCompatActivity class overridden
     * Runs as soon as the Activity is created
     * @param savedInstanceState
     * @return
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        initFragment();
    }

    /**
     * Initializes the different elements present in the Fragment
     */
    private void initFragment(){
        //Toast.makeText(this,"Main Activity Opened : "+pref.getBoolean(Constants.LOGGEDIN_SHARED_PREF,false),Toast.LENGTH_LONG).show();
        if(pref.getBoolean(Constants.LOGGEDIN_SHARED_PREF,false)){
            //Toast.makeText(this,"Opening Profile Activity : "+pref.getBoolean(Constants.LOGGEDIN_SHARED_PREF,false),Toast.LENGTH_LONG).show();
            goToProfile();
        }else {
            Fragment fragment = new LoginFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_frame, fragment);
            ft.commit();
        }
    }

    /**
     * Opens the next Activity after Login
     */
    @Override
    public void goToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        //finish();
    }

    /**
     * Default function overridden
     * Runs when the activity reopens from Stack
     */
    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        initFragment();
    }

}
