/**
 * To store all the static variables used throughout the project
 * generally as keys for 'key:value' pair type data.
 *
 * @author Debanjana Kar
 * Created on 03-05-2017
 *
 */

package com.vitaran.donor;


public class Constants {

    //public static final String BASE_URL = "http://vitaran.000webhostapp.com";
    //public static final String REGISTER_URL = "http://vitaran.000webhostapp.com/register.php";
    //public static final String LOGIN_URL = "http://vitaran.000webhostapp.com/login.php";

    public static final String BASE_URL = "http://192.168.137.1/";
    public static final String REGISTER_URL = BASE_URL+"register.php";
    public static final String LOGIN_URL = BASE_URL+"login.php";

    public static final String REGISTER_OPERATION = "register";
    public static final String LOGIN_OPERATION = "login";
    public static final String CHANGE_PASSWORD_OPERATION = "chgPass";

    public static final String REGISTER_SUCCESS = "success";
    public static final String LOGIN_SUCCESS = "success";
    public static final String FAILURE = "failure";
    //public static final String IS_LOGGED_IN = "isLoggedIn";
    //public static final String LOGGED_IN = "false";
    public static final String SHARED_PREF_NAME = "vitaranClient";
    public static final String EMAIL_SHARED_PREF = "email";
    public static final String NAME_SHARED_PREF = "name";
    public static final String LAT_SHARED_PREF = "latitude";
    public static final String LNG_SHARED_PREF = "longitude";
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";

    public static final String LOGIN_RESPONSE_SUCCESS = "success";
    public static final String LOGIN_RESPONSE_FAILURE = "failure";

    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String ADDRESS = "address";
    public static final String CONTACT = "contact";
    public static final String UNIQUE_ID = "unique_id";

    public static final String TAG = "Connection to Vitaran";

}
