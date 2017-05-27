/**
 * To store all the static variables used throughout the project
 * generally as keys for 'key:value' pair type data.
 *
 * @author Anudeep Ghosh
 * Created on 03-05-2017.
 */
package com.vitaran.ngo;


public class Constants {

    //public static final String BASE_URL = "http://vitaran.000webhostapp.com/";
    public static final String BASE_URL = "http://192.168.23.1/vitaran/";
    //public static final String BASE_URL = "http://192.168.137.1/";
    //public static final String REGISTER_URL = BASE_URL + "register.php"; //Only one NGO for the time being
    public static final String LOGIN_URL = BASE_URL+"loginNGO.php";
    public static final String ADD_ITEM_URL = BASE_URL+"addItemsNGO.php";
    public static final String ADD_TAG_URL = BASE_URL+"addTagsNGO.php";
    public static final String FILL_URL = BASE_URL+"fillSpinner.php";
    public static final String FILL_TABLE_URL = BASE_URL+"fillNGOItemTable.php";
    //public static final String FILL_TABLE_URL = "http://vitaran.000webhostapp.com/fillNGOItemTable.php";
    public static final String FILL_MANAGE_DONATION_TABLE_URL = BASE_URL+"fillManageDonationTable.php";
    public static final String UPDATE_DONATION_TABLE_URL = BASE_URL+"updateDonations.php";
    public static final String UPDATE_COLLECTION_TABLE_URL = BASE_URL+"updateCollections.php";
    public static final String UPDATE_DONATION_LOCATION_TABLE_URL = BASE_URL+"updateDonationLoc.php";


    public static final String REGISTER_OPERATION = "register";
    public static final String LOGIN_OPERATION = "login";
    public static final String CHANGE_PASSWORD_OPERATION = "chgPass";

    public static final String REGISTER_SUCCESS = "success";
    public static final String LOGIN_SUCCESS = "success";
    //public static final String IS_LOGGED_IN = "isLoggedIn";
    //public static final String LOGGED_IN = "false";
    public static final String SHARED_PREF_NAME = "vitaranClient";
    public static final String EMAIL_SHARED_PREF = "email";
    public static final String NAME_SHARED_PREF = "name";
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";

    public static final String LOGIN_RESPONSE_SUCCESS = "success";
    public static final String LOGIN_RESPONSE_FAILURE = "failure";

    public static final String RESULT = "result";
    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";
    public static final String STATUS = "status";

    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String ADDRESS = "address";
    public static final String CONTACT = "contact";
    public static final String UNIQUE_ID = "unique_id";

    public static final String TAG = "Connection to Vitaran";

}
