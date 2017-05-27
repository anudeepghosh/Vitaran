/**
 * To view the donations pledged
 *
 * @author Harshit Kandhway
 * Created on 05-05-2017.
 *
 */


package com.vitaran.donor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
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
import java.util.StringTokenizer;

public class ViewPledge extends AppCompatActivity {
String pass;
    EditText e;
    ArrayList<String> arrcat;
    ArrayAdapter<String> adapter;
    Button b;
    Spinner s;
    RequestQueue requestQueue;
    String dID,dlat,dlong,item="",category="",chk="0",units="0",quant="0";
    //String donationURL="http://192.168.1.11/insertdonation.php";
    String showDonationURL=Constants.BASE_URL+"showDonation.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pledge);
        SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //chng1
        dID = sharedPreferences.getString(Constants.NAME_SHARED_PREF,"");

        requestQueue = Volley.newRequestQueue(this);
        s = (Spinner) findViewById(R.id.spinner);

        //start <code>

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,showDonationURL,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONArray items = response.getJSONArray("donation");

                    arrcat = new ArrayList<>();
                    for(int i=0;i<items.length();i++)
                    {
                        JSONObject cat = items.getJSONObject(i);
                        String cname = cat.getString("category");
                        String  iname= cat.getString("item");
                        String unit = cat.getString("units");
                        String quant = cat.getString("quantity");
                        String donorid = cat.getString("donorID");
                        //Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
                        if(donorid.equals(dID))
                        arrcat.add(cname+"  "+iname+"  "+quant+"  "+unit);
                    }

// Create an ArrayAdapter using the string array and a default spinner layoutu
                    adapter = new ArrayAdapter(ViewPledge.this,android.R.layout.simple_spinner_item,arrcat);
// Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                    s.setAdapter(adapter);
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
        requestQueue.add(jsonObjectRequest);


        // </code>


        //e.setText(pass);
        /*StringTokenizer st=new StringTokenizer(pass,"\n");
        while (st.hasMoreTokens())
        {
            StringTokenizer st1 = new StringTokenizer(st.nextToken(),"-");
            category+=st1.nextToken()+",";
            item+=st1.nextToken()+",";
            units+=st1.nextToken()+",";
            quant+=st1.nextToken()+",";
        }
        */
        b = (Button)findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
   /* //conncttr
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
                        parameter.put("dName",dname);
                        parameter.put("quantity",quant);
                        parameter.put("item",item);
                        parameter.put("units",units);
                        parameter.put("dLatitude",dlat);
                        parameter.put("dLongitude",dlong);
                        parameter.put("chk",chk);
                        parameter.put("category",category);
                        return parameter;
                    }
                };
                requestQueue.add(stringRequest);
                //connctnd
                */
                Intent i = new Intent(ViewPledge.this,HomePage.class);
                startActivity(i);
            }
        });
    }
}
