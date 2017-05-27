/**
 * Class to manage donation provided by donors,
 * select and accept donations as well as notify
 * both Donor and Collection Unit.
 *
 * @author Anudeep Ghosh
 * Created on 23-05-2017.
 */

package com.vitaran.ngo;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageDonation extends AppCompatActivity implements View.OnClickListener {

/*    ManageDonation(int donations) {
        this.donations = donations;
    }*/
    private RequestQueue requestData,requestQueue,requestQCollections;
    private TableLayout table,table_hdr;
    private TextView[] tv_head;
    private Button btn_confirm_collection;
    private List<Integer> checkedBoxes = new ArrayList<>();
    private int donations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_donation);
        table = (TableLayout)findViewById(R.id.donationTable);
        table_hdr = (TableLayout)findViewById(R.id.donationTableHeader);
        populateTable("item");
        btn_confirm_collection = (Button)findViewById(R.id.btn_confirm_collection);
        btn_confirm_collection.setOnClickListener(this);
    }

    /**
     * Populates Active Donations' table
     *
     * @param orderBy, parameter according to which the sorting of the table is done
     */
    private void populateTable(final String orderBy) {
        requestData = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.FILL_MANAGE_DONATION_TABLE_URL,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        String serverResponse = parseJSONTableData(response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("fill",orderBy);
                return params;
            }
        };
        requestData.add(stringRequest);
    }

    /**
     * Parses the server returned JSON data to fill Active Donations' table
     * @param response, response received from server
     * @return
     */
    private String parseJSONTableData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.has(Constants.RESULT)) {
                JSONArray result = jsonObject.getJSONArray(Constants.RESULT);
                donations = result.length();
                createTable(result);
            } else {
                Toast.makeText(this, "Error getting server response", Toast.LENGTH_LONG).show();
            }
    } catch (JSONException e) {
        e.printStackTrace();
    }
        return null;
    }

    /**
     * Parses the server returned JSON data to get the list of mail recepients
     * @param response, response received from server
     * @return an array of email IDs
     */
    private String[] parseEmailJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject email;
            String[] mailID;
            if(jsonObject.has(Constants.RESULT)) {
                JSONArray result = jsonObject.getJSONArray(Constants.RESULT);
                mailID=new String[result.length()];
                if (result.length()>0) {
                    for (int i=0;i<result.length();i++) {
                        email = result.getJSONObject(i);
                        mailID[i] = email.getString("email");
                        //showMessage(mailID[i],2);
                    }
                    return mailID;
                }
            } else {
                Toast.makeText(this, "Error getting server response", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a table of active donations, dynamically
     * @param result
     */
    private void createTable(JSONArray result) {
        try {
            //checkedBoxes = new ArrayList<>(result.length());
            table_hdr.removeAllViews();
            table.removeAllViews();
            TextView[][] tableData = new TextView[result.length()][7];
            final TableRow[] trow = new TableRow[result.length()];

            for (int i = 0; i < result.length(); i++) {
                JSONObject upData = result.getJSONObject(i);
                tableData[i][6] = new TextView(this);
                double gLat = Double.parseDouble(upData.getString("latitude"));
                double gLong = Double.parseDouble(upData.getString("longitude"));
                String donationID = upData.getString("DID");
                double pos = getLocation(gLat, gLong);
                //showMessage(upData.getString("distance"),4);
                updateDonationLocation(pos,donationID);
                tableData[i][6].setText(upData.getString("distance"));
            }
            TableRow trow_heading;
            tv_head = new TextView[7];
            String tableHeadingData[] = {"DID","donorID","item","category","units","quantity","distance"};
            int headWidth[] = {30,10,80,80,40,30,50};
            int column[] = {R.id.column1,R.id.column2,R.id.column3,R.id.column4,R.id.column5,R.id.column6,R.id.column7};

            trow_heading = new TableRow(this);
            trow_heading.setId(View.generateViewId());
            trow_heading.setBackgroundColor(Color.rgb(139,195,74));
            trow_heading.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));

            for (int j=0; j<7; j++) {
                // Here create the TextView dynamically
                tv_head[j] = new TextView(this);
                tv_head[j].setId(column[j]);
                tv_head[j].setText(tableHeadingData[j]);
                tv_head[j].setTextColor(Color.WHITE);
                tv_head[j].setPadding(headWidth[j], 10, headWidth[j], 10);
                //tv_head[j].setPadding(40, 10, 40, 10);
                //tv_head[j].set
                trow_heading.addView(tv_head[j]);
                if (j!=6) {
                    tv_head[j].setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            sortTable(v.getId());
                            return false;
                        }
                    });
                } else if(j==6) {
                    tv_head[j].setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            sortTable(v.getId());
                            return false;
                        }
                    });
                }
            }
            // Add each table row to table layout
            table_hdr.addView(trow_heading, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT));

            for(int i=0; i<result.length();i++){//loop for adding rows dynamically

                JSONObject data = result.getJSONObject(i);

                //Create the tablerows
                trow[i] = new TableRow(this);
                trow[i].setId(R.id.row_head + i + 1);
                trow[i].setBackgroundColor(Color.rgb(220,237,200));
                trow[i].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));

                for (int j=0; j<7; j++) {
                    // Here create the TextView dynamically
                    tableData[i][j] = new TextView(this);
                    tableData[i][j].setId(R.id.col_head + 100 * i + j + 1);
                    if (j<6){
                        tableData[i][j].setText(data.getString(tableHeadingData[j]));
                    } else {
                        /*double gLat = Double.parseDouble(data.getString("latitude"));
                        double gLong = Double.parseDouble(data.getString("longitude"));
                        String donationID = data.getString(tableHeadingData[0]);
                        double pos = getLocation(gLat, gLong);
                        updateDonationLocation(pos,donationID);*/
                        tableData[i][j].setText(data.getString("distance"));
                    }
                    tableData[i][j].setTextColor(Color.BLACK);
                    tableData[i][j].setPadding(50, 10, 50, 10);
                    //Add each textView to the table row
                    trow[i].addView(tableData[i][j]);
                }
                final int k=i;
                CheckBox box = new CheckBox(this);
                trow[i].addView(box);
                box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked)
                            setCheck(trow[k].getId());
                        else if (!isChecked) {
                            removeCheck(trow[k].getId());
                        }
                    }
                });

                // Add each table row to table layout
                table.addView(trow[i], new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT));

            } // end of for loop
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Keeps a record of the checks in the checkboxes
     * @param tableRow, the row that has been selected
     */
    private void setCheck(int tableRow) {
        checkedBoxes.add(tableRow);
        String checkedRows="";
        for (int i=0;i<checkedBoxes.size();i++){
            checkedRows=(checkedBoxes.get(i)-R.id.row_head)+"\n";
        }
        //showMessage(checkedRows);

    }

    /**
     * Unchecks the checkboxes
     * @param tableRow, the row that has been selected
     */
    private void removeCheck(int tableRow) {
        checkedBoxes.remove(checkedBoxes.indexOf(tableRow));
        String checkedRows="";
        for (int i=0;i<checkedBoxes.size();i++){
            checkedRows=(checkedBoxes.get(i)-R.id.row_head)+"\n";
        }
        //showMessage(checkedRows);
    }

    private void sortTable(int id) {
        String param="";
        switch (id) {
            case R.id.column1 :
                param = "DID";
                //showMessage("column1 clicked!");
                populateTable(param);
                break;
            case R.id.column2 :
                param = "donorID";
                //showMessage("column2 clicked!");
                populateTable(param);
                break;
            case R.id.column3 :
                param = "item";
                //showMessage("column3 clicked!");
                populateTable(param);
                break;
            case R.id.column4 :
                param = "category";
                //showMessage("column4 clicked!");
                populateTable(param);
                break;
            case R.id.column5 :
                //showMessage("column5 clicked!");
                param = "units";
                populateTable(param);
                break;
            case R.id.column6 :
                //showMessage("column6 clicked!");
                param = "quantity";
                populateTable(param);
                break;
            case R.id.column7 :
                //showMessage("column7 clicked!");
                param = "pos";
                populateTable(param);
                break;
        }
    }

    private double getLocation(double mLat, double mLon) {
        double lat = 22.569410;
        double lon = 88.361176;
        /*Geocoder geocoder;
        List<Address> addresses;
        String address=null,city=null,town=null;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(mLat, mLong, 1);
            address = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            town = addresses.get(0).getSubLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        double theta = mLon - lon;
        double dist = Math.sin(Math.toRadians(mLat))*Math.sin(Math.toRadians(lat)) +
                Math.cos(Math.toRadians(mLat))*Math.cos(Math.toRadians(lat))*Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist*60*1.1515*1.609344;
        return dist;
    }

    /**
     * Function to show toasts for user warnings and info
     * @param : msg, String value
     */
    public void showMessage(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    public void showMessage(String msg, int t) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_confirm_collection){
            updateDonation();
            populateTable("");
        }
    }

    private void updateDonation() {
        try {
            JSONObject jsonObject[] = new JSONObject[checkedBoxes.size()];
            JSONArray jsonArray = new JSONArray();
            TableRow tr;
            TextView tv;
            for (int i = 0; i < checkedBoxes.size(); i++) {//for loop starts
                jsonObject[i] = new JSONObject();
                try {
                    tr = (TableRow)findViewById(checkedBoxes.get(i));

                    tv = (TextView)tr.getChildAt(0);
                    jsonObject[i].put("DID", tv.getText().toString());

                    tv = (TextView)tr.getChildAt(1);
                    jsonObject[i].put("donorID", tv.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject[i]);
                //tr.getChildAt(6);
            }//for loop ends
            //JSONObject dataID = new JSONObject();
            //+dataID.put("IDs", jsonArray);
            String jsonStr = jsonArray.toString();
            //showMessage(jsonStr,0);
            postUpdates(jsonStr);
            updateCollection(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDonationLocation(final double dist, final String donationID) {
        //requestQCollections = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPDATE_DONATION_LOCATION_TABLE_URL,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        //String serverResponse = parseJSONTableData(response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("dist",""+dist);
                params.put("donationID",""+donationID);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void updateCollection(final String updates) {
        requestQCollections = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPDATE_COLLECTION_TABLE_URL,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        //String serverResponse = parseJSONTableData(response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("json",updates);
                return params;
            }
        };
        requestQCollections.add(stringRequest);
        //notifyCollectionUnit();
    }

    private void postUpdates(final String inp) throws InterruptedException {
        requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPDATE_DONATION_TABLE_URL,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        String[] serverResponse = parseEmailJSON(response);
                        notifyDonors(serverResponse);
                        notifyCollectionUnit();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("json",inp);
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    private void notifyDonors(String[] emailIDs) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailIDs);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Notification : Vitaran Donation Accepted");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear Donor,\nthis is to notify you that " +
                "we are grateful to accept your donation.\n" +
                "Our Collection Unit will be arriving shortly");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Mail sent", "");
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void notifyCollectionUnit() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        String[] collectionMail = {"collection@vitaran.com"};
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, collectionMail);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Notification : Vitaran Donation Accepted");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please check Collection " +
                "List for accepted donations");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Mail sent", "");
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

}

