/**
 * View that is the landing page after login
 *
 * @author Debanjana Kar
 * Created on 04-05-2017.
 *
 */

package com.vitaran.donor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity {
    String tpledge,donorID;
    Button mp,vp,lo;
    TextView t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
       // SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //  dname="rahul";//getIntent().getStringExtra("nam");
        //dlat="52";//getIntent().getStringExtra("lat");
        //dlong="114";//getIntent().getStringExtra("long");
        //tpledge = getIntent().getStringExtra("total");
        lo=(Button)findViewById(R.id.logout);
        mp = (Button)findViewById(R.id.bp);
        vp = (Button)findViewById(R.id.vp);
        vp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this,ViewPledge.class);
               // i.putExtra("pass",tpledge);
               // i.putExtra("nam",donorID);
               // i.putExtra("lat",dlat);
               // i.putExtra("long",dlong);
                startActivity(i);
            }
        });
lo.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        SharedPreferences sharedpreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
       // editor.clear();


        /*
        Bug-ID 113 related to testcase Login-5
        Implementation on Suggestion :  Alternative provided
        Suggestion : Provide logout functionality to exit the app.
        Solution : On pressing quit application, the application simply closes and control returns to user's phone home page .
        */


        editor.commit();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
});
       /* t = (TextView)findViewById(R.id.textView7);
        try {
            //t.setText(tpledge);
        }
        catch(Exception e)
        {
           // t.setText("0");
        }*/
        mp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this,PledgeDonation.class);
                startActivity(i);
            }
        });
    }
}
