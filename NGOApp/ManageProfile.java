/**
 * Class to manage profile of the NGO,
 * where it defines the currently required entities.
 *
 * @author Anudeep Ghosh
 * @author Debanjana Kar
 * Created on 08-05-2017.
 */

package com.vitaran.ngo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageProfile extends AppCompatActivity implements View.OnClickListener {

    private View linearLayout;
    private HttpURLConnection urlConnection=null;
    private InputStream is=null;
    private EditText et_item, et_qty, et_new_item, et_tag;
    private RequestQueue requestQueue,requestQueueTag,requestFill,requestTable;
    private Button btn_add_item, btn_add_tag;
    private Spinner spnr_item, spnr_unit, spnr_tag,spnr;
    private String item_selected,tag_selected;
    private String line=null, result=null;
    private InputMethodManager inputManager;
    private ArrayAdapter<String> arrayAdapter=null;
    private final List<String> tagList = new ArrayList<String>();
    private final List<String> unitList = new ArrayList<String>();
    private final List<String> itemList = new ArrayList<String>();
    private List<String> fillList = new ArrayList<String>();
    private TableLayout table;
    private String post_item="",post_qty,post_unit,post_tag="";//parameters to be posted
    private TextView tv_item_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profile);

        //Initialization of all the Views
        linearLayout = findViewById(R.id.layoutManageProfile);
        linearLayout.requestFocus();

        tv_item_number = (TextView)findViewById(R.id.tv_number_of_items);

        table = (TableLayout)findViewById(R.id.addItemTable);

        et_qty = (EditText)findViewById(R.id.et_qty);
        et_qty.clearFocus();
        et_qty.setHint(getString(R.string.hint_qty));

        et_item = (EditText)findViewById(R.id.et_item);
        et_item.clearFocus();
        et_item.setFocusable(true);

        et_tag = (EditText)findViewById(R.id.et_tag);
        et_tag.setHint(getString(R.string.hint_tag));

        btn_add_item = (Button)findViewById(R.id.btn_add_item);
        btn_add_item.setOnClickListener(this);

        btn_add_tag = (Button)findViewById(R.id.btn_add_tag);
        btn_add_tag.setOnClickListener(this);

        //Listener added to spinner for reading input values
        spnr_item = (Spinner)findViewById(R.id.spnr_item);
        spnr_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equalsIgnoreCase("other")) {
                    //et_item.setEnabled(true);
                    et_item.setFocusable(true);
                    et_item.setFocusableInTouchMode(true);
                    et_qty.setFocusable(true);
                    et_qty.setFocusableInTouchMode(true);
                    et_item.requestFocus();
                    item_selected = "Other";
                    post_item = "";
                } else if (parent.getItemAtPosition(position).toString().equalsIgnoreCase("Select Item")) {
                    et_item.setFocusable(false);
                    et_qty.setFocusable(false);
                }else {
                    et_item.setFocusable(false);
                    et_qty.setFocusable(true);
                    et_qty.setFocusableInTouchMode(true);
                    item_selected = parent.getItemAtPosition(position).toString();
                    post_item = item_selected;
                }
                //showMessage(item_selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showMessage("Nothing Selected");
            }
        });

        spnr_unit = (Spinner)findViewById(R.id.spnr_unit);
        spnr_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item_selected = parent.getItemAtPosition(position).toString();
                post_unit = item_selected;
                //showMessage(item_selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showMessage("Nothing Selected");
            }
        });

        spnr_tag = (Spinner)findViewById(R.id.spnr_tag);
        spnr_tag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equalsIgnoreCase("other")) {
                    //et_tag.setEnabled(true);
                    et_tag.setFocusable(true);
                    et_tag.setFocusableInTouchMode(true);
                    et_tag.requestFocus();
                    btn_add_tag.setEnabled(true);
                    btn_add_tag.setFocusable(true);
                    btn_add_tag.setFocusableInTouchMode(true);
                    tag_selected = "Other";
                    post_tag = "";
                } else {
                    //et_tag.setEnabled(false);
                    et_tag.setFocusable(false);
                    et_tag.setError(null);
                    btn_add_tag.setEnabled(false);
                    btn_add_tag.setFocusable(false);
                    tag_selected = parent.getItemAtPosition(position).toString();
                    post_tag = tag_selected;
                }
                //showMessage(item_selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showMessage("Nothing Selected");
            }
        });

        populateSpinner("tag");
        populateSpinner("item");
        populateSpinner("unit");
        populateTable();

        inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_item:
                checkBeforeAddingNewItem();
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.btn_add_tag:
                checkBeforeAddingNewTag();
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                break;
        }
    }

    /*Checks for the safe usage of addItem() function*/
    private void checkBeforeAddingNewItem() {
        // Set errprs to default, i.e. null
        et_item.setError(null);
        et_qty.setError(null);
        et_tag.setError(null);

        //Initialize a general View and
        //a Boolean value
        View focusView = null;
        Boolean cancel = false;
        //StringBuffer sb = new StringBuffer(et_qty.getText().toString());

        if (spnr_item.getSelectedItem().toString().equalsIgnoreCase("Select Item") && cancel==false) {
            showMessage("Select a valid item!");
            cancel=true;
        }
        if (spnr_unit.getSelectedItem().toString().equalsIgnoreCase("Select Unit") && cancel==false) {
            showMessage("Select a valid unit!");
            cancel=true;
        }
        if (spnr_tag.getSelectedItem().toString().equalsIgnoreCase("Select Category") && cancel==false) {
            showMessage("Select a valid category!");
            cancel=true;
        }

        if (et_qty.getText().toString().trim().isEmpty() && cancel==false) {
            et_qty.setError("Cannot be empty");
            focusView = et_qty;
            cancel = true;
        } else if (!et_qty.getText().toString().matches("[0-9]+(\\.[0-9]+)?") && cancel==false) {
            et_qty.setError("Cannot contain letters");
            focusView = et_qty;
            cancel = true;
        } else if (cancel==false){
            post_qty = et_qty.getText().toString();
        }

        if ((post_item.trim().isEmpty() || post_item.trim().equalsIgnoreCase(null)) && cancel==false) {
            if (et_item.getText().toString().equalsIgnoreCase("") && (et_item.getText().toString().length()<3)) {
                et_item.setError("Cannot be empty or invalid");
                focusView = (focusView==null)?et_item:focusView;
                cancel = true;
            } else if (cancel==false){
                post_item = et_item.getText().toString().trim();
            }
        }
        if (post_tag.trim().isEmpty() || post_tag.trim().equalsIgnoreCase(null)) {
            if (et_tag.getText().toString().equalsIgnoreCase("") && (et_tag.getText().toString().trim().length()<3)) {
                et_tag.setError("Cannot be empty or invalid");
                focusView = (focusView==null)?et_tag:focusView;;
                cancel = true;
            } else if (cancel==false){
                post_tag = et_tag.getText().toString().trim();
                int len = spnr_tag.getCount();
                spnr_tag.setSelection(len-1);
                if(!post_tag.equalsIgnoreCase(spnr_tag.getSelectedItem().toString())) {
                    et_tag.setError("Tag not found!! Add tag first");
                    focusView = (focusView==null)?et_tag:focusView;;
                    cancel = true;
                }
            }
        }
        if (cancel==true) {
            if (focusView!=null){
                focusView.requestFocus();
            }
            Toast.makeText(this, "Recheck the fields!", Toast.LENGTH_LONG).show();
        } else {
            addItem(post_item,post_qty,post_unit);
            //showMessage(post_item + ":" + post_qty + ":" + post_unit + ":" + post_tag);
            //populateTable();
        }

    }

    /*Checks for the safe usage of addTag() function*/
    private void checkBeforeAddingNewTag() {
        et_tag.setError(null);
        if(et_tag.getText().toString().trim().isEmpty() || (et_tag.getText().toString().trim().length() < 3)) {
            et_tag.setError("Invalid Tag. Tag should be more than 2 letters");
            et_tag.requestFocus();
        } else {
            post_tag = et_tag.getText().toString();
            addTag(post_tag);
        }
    }

    /**
     * Uses POST method to add news in the database
     * @param  item, String receiving the item name
     * @param  qty, String receiving the quantity of item
     * @param  unit, String receiving the unit of the quantity
     */
    private void addItem(final String item, final String qty, final String unit){

        requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.ADD_ITEM_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String serverResponse = getJSONData(response);
                        String message = "";
                        if (serverResponse.equalsIgnoreCase(Constants.SUCCESS)) {
                            message = "Item added";
                            itemList.clear();
                            populateSpinner("item");
                            populateTable();
                        }/**
                         * Bug ID-84 related to testcase Quarks - 31
                         * Bug Status : Solved
                         * Bug : Quantity must be strictly numeric
                         * Solution :
                         * 1. Made quantity numeric by using regex code to
                         * check for only numerical inputs
                         * 2. UI contraint of only numerical input applied on the EditText View
                         */
                        else{
                            //If the server response is not success
                            //Displaying an error message on toast
                            message = "Unable to add item";
                        }
                        showMessage(message);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(manageProfile,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("item", item);
                params.put("qty", qty);
                params.put("unit",unit);
                params.put("tag",post_tag);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    /**
     * Uses POST method to add new tags in the database
     * @param tag, String received for new category
     */
    private void addTag(final String tag){

        requestQueueTag = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.ADD_TAG_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String serverResponse = getJSONData(response);
                        String message = "";
                        if (serverResponse.equalsIgnoreCase(Constants.SUCCESS)) {
                            message = "Tag added";
                            tagList.clear();
                            populateSpinner("tag");
                            et_tag.setText(null);
                            //spnr_tag.setSelection();
                            //spnr_tag.setSelection(spnr_tag.getCount()-2);
                        }else{
                            //If the server response is not success
                            //Displaying an error message on toast
                            message = "Unable to add tag";
                        }
                        showMessage(message);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(manageProfile,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("tag",tag);
                return params;
            }
        };
        requestQueueTag.add(stringRequest);
    }

    /**
     * Function to show toasts for user warnings and info
     * @param : msg, String value
     */
    public void showMessage(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * @param fill
     */
    private void populateSpinner(final String fill) {
        requestFill = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.FILL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String serverResponse="";
                        switch (fill) {
                            case "item":
                                fillList=itemList;
                                spnr=spnr_item;
                                break;
                            case "tag":
                                fillList=tagList;
                                spnr=spnr_tag;
                                break;
                            case "unit":
                                fillList=unitList;
                                spnr=spnr_unit;
                                break;
                        }

                        serverResponse = parseJSON(response, fill, fillList, spnr);
                        String message = "";
                        if (serverResponse.equalsIgnoreCase(Constants.SUCCESS)) {
                            message = fill+" found";
                        }else{
                            //If the server response is not success
                            //Displaying an error message on toast
                            message = fill+" not found";
                        }
                        showMessage(message);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(manageProfile,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("fill",fill);
                return params;
            }
        };
        requestFill.add(stringRequest);
    }

    /**
     *
     */
    private void populateTable() {
        requestTable = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.FILL_TABLE_URL,
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
                params.put("fill","");
                return params;
            }
        };
        requestTable.add(stringRequest);
    }

    /**
     *
     * @param response
     * @return
     */
    private String parseJSONTableData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.has(Constants.RESULT)) {
                JSONArray result = jsonObject.getJSONArray(Constants.RESULT);
                //showMessage(""+result.length());
                JSONObject data;
                //String tag[] = new String[result.length()];
                //StringBuilder tags = new StringBuilder();
                createTable(result);
                tv_item_number.setText(" " + result.length());
                /*for (int i=0;i<result.length();i++) {
                    data = result.getJSONObject(i);
                    showMessage(data.getString("itemID")+"\n"+data.getString("item")+
                            "\n"+data.getString("quantity")+"\n"+data.getString("unit_id")+"\n"+
                            data.getString("tag_id"));
                    //tag[i]=data.getString(fill);
                    //tags.append(data.getString("tag")+"\n");
                }*/
            } else {
                Toast.makeText(this,"Error getting server response",Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Parses JSON data received from server
     * @param response, JSON response received as String
     * @return
     */
    private String getJSONData(String response) {
        String resp = "failure";
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.has(Constants.RESULT)) {
                JSONArray result = jsonObject.getJSONArray(Constants.RESULT);
                JSONObject data = result.getJSONObject(0);
                resp = data.getString(Constants.STATUS);
                //showMessage(resp+": response from getJSONData()");
            } else {
                Toast.makeText(this,"Error getting server response",Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resp;
    }

    /**
     * Parses JSON data received from server to fill the spinners with updated data
     * @param response, receives data from server in JSON format
     * @param fill, type of spinner to be filled
     * @param fillList, List<> that stores the data the spinners are to be filled with
     * @param spnr, Spinner to be populated
     * @return
     */
    private String parseJSON(String response, String fill, final List<String> fillList, final Spinner spnr) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.has(Constants.RESULT)) {
                JSONArray result = jsonObject.getJSONArray(Constants.RESULT);
                //showMessage(""+result.length());
                JSONObject data;
                String tag[] = new String[result.length()];
                //StringBuilder tags = new StringBuilder();

                //Scraping single column data and storing it into an array
                for (int i=0;i<result.length();i++) {
                    data = result.getJSONObject(i);
                    if (fill.equalsIgnoreCase("item"))
                        fill = "Name";
                    tag[i]=data.getString(fill);
                    //tags.append(data.getString("tag")+"\n");
                }
                if (fill.equalsIgnoreCase("unit"))
                    fillList.add("Select Unit");
                else if (fill.equalsIgnoreCase("tag"))
                    fillList.add("Select Category");
                else {
                    fillList.add("Select Item");
                }

                //Using the array to fill the arrayList
                for (int i=0;i<tag.length;i++) {
                    fillList.add(tag[i]);
                }
                if (!fill.equalsIgnoreCase("unit"))
                    fillList.add("other");
                arrayAdapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, fillList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spnr.setAdapter(arrayAdapter);
                //resp = data[i].getString("tag");
                //showMessage(fillList.toString());
            } else {
                Toast.makeText(this,"Error getting server response",Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "success";
    }

    private void createTable(JSONArray result) {
        try {
            TextView[][] textArray = new TextView[result.length()][5];
            TableRow[] tr_head = new TableRow[result.length()];
            TableRow table_heading;
            TextView[] tv = new TextView[5];
            String table_head[] = {"ItemID","Name","Quantity","Unit","Category"};

            table_heading = new TableRow(this);
            table_heading.setId(View.generateViewId());
            table_heading.setBackgroundColor(Color.GRAY);
            table_heading.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT));

            for (int j=0; j<5; j++) {
                // Here create the TextView dynamically
                tv[j] = new TextView(this);
                tv[j].setId(View.generateViewId());
                tv[j].setText(table_head[j]);
                tv[j].setTextColor(Color.WHITE);
                tv[j].setPadding(50, 10, 50, 10);
                table_heading.addView(tv[j]);
            }
            // Add each table row to table layout
            table.addView(table_heading, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT));

            for(int i=0; i<result.length();i++){
                JSONObject data = result.getJSONObject(i);
                //Create the tablerows
                tr_head[i] = new TableRow(this);
                //tr_head[i].setId(View.generateViewId());
                tr_head[i].setId(i + 1);
                tr_head[i].setBackgroundColor(Color.LTGRAY);
                //tr_head[i].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
                tr_head[i].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT));

                for (int j=0; j<5; j++) {
                    // Here create the TextView dynamically
                    textArray[i][j] = new TextView(this);
                    textArray[i][j].setId(10*i+j+100+1);
                    textArray[i][j].setText(data.getString(table_head[j]));
                    textArray[i][j].setTextColor(Color.BLACK);
                    textArray[i][j].setPadding(50, 10, 50, 10);
                    tr_head[i].addView(textArray[i][j]);
                }

                // Add each table row to table layout
                table.addView(tr_head[i], new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT));

            } // end of for loop
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
/**
 * MISSING FEATURES
 * 
 */