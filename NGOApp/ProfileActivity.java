/**
 * Class that displays just after a successful Login
 *
 * @author Anudeep Ghosh
 * Created on 04-05-2017.
 */
package com.vitaran.ngo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //private TextView tv_name,tv_email,tv_message;
    private SharedPreferences pref;
    private AppCompatButton btn_change_password,btn_logout;
    private EditText et_old_password,et_new_password,et_nav_prof;
    private AlertDialog dialog;
    private ProgressBar progress;
    private View drawerView;
    private Button btn_profile,btn_donation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        pref = this.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        drawerView = (View)findViewById(R.id.drawer_layout);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btn_profile = (Button)findViewById(R.id.btn_openProfileMngr);
        btn_donation = (Button)findViewById(R.id.btn_openDonationMngr);
        //NavigationView navigationView = (NavigationView)findViewById(R.id.nav_header);
        //View hView =  navigationView.getHeaderView(0);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        TextView nav_user_name = (TextView)hView.findViewById(R.id.tv_profile_name);
        TextView nav_user_email = (TextView)hView.findViewById(R.id.tv_profile_email);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*Snackbar.make(view, pref.getString(Constants.NAME_SHARED_PREF,"User not found")+
                        "\nProfile TextView content : "+tv_name.getText(), Snackbar.LENGTH_LONG).show();*//*
                Snackbar.make(view, "", Snackbar.LENGTH_LONG).show();

            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ManageProfile.class);
                startActivity(intent);
            }
        });

        btn_donation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ManageDonation.class);
                startActivity(intent);
            }
        });

        nav_user_name.setText(pref.getString(Constants.NAME_SHARED_PREF, ""));
        nav_user_email.setText(pref.getString(Constants.EMAIL_SHARED_PREF,""));
        //Toast.makeText(this.getBaseContext(), "Logged In : " + pref.getBoolean(Constants.LOGGEDIN_SHARED_PREF, false), Toast.LENGTH_LONG).show();
        //Snackbar.make(drawerView, "Welcome : "+pref.getString(Constants.NAME_SHARED_PREF,""), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (id == R.id.nav_manage_profile) {
            Intent intent = new Intent(this, ManageProfile.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage_donation) {
            Intent intent = new Intent(this, ManageDonation.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            logout();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Implements Logout from active account
     */
    private void logout() {

        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Constants.LOGGEDIN_SHARED_PREF,false);
        editor.putString(Constants.EMAIL_SHARED_PREF, "");
        editor.putString(Constants.NAME_SHARED_PREF, "");
        editor.clear();
        //editor.putString(Constants.NAME,"");
        //editor.putString(Constants.UNIQUE_ID,"");
        editor.commit();

        goToLogin();
    }

    /**
     * Function to reopen login page after logout
     */
    private void goToLogin() {
        super.onBackPressed();
        //finish();
    }

}
