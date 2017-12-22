package com.enpeck.RFID.common;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by ABC on 11/01/2017.
 */

public class SessionManagement {
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "Pref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_username = "name";

    public static final String KEY_Ieccode = "mobile";

    public static final String KEY_company = "emailid";


    public static final String KEY_password = "imei";


    public SessionManagement(Context context){
        this._context=context;
        pref =_context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor =pref.edit();
    }

    public void createRegisterationSession(String name,String iec,String username,String password){
        //store input value as a true
        editor.putBoolean(IS_LOGIN,true);

        //store name in pref
        editor.putString(KEY_company,name);

        editor.putString(KEY_Ieccode,iec);

        editor.putString(KEY_username,username);

        editor.putString(KEY_password,password);

        editor.commit();

    }

    public void createRegisterationSession1(String name){
        //store input value as a true
        editor.putBoolean(IS_LOGIN,true);

        //store name in pref
        editor.putString(KEY_company,name);
/*

        editor.putString(KEY_Ieccode,iec);

        editor.putString(KEY_username,username);

        editor.putString(KEY_password,password);
*/

        editor.commit();

    }

    public HashMap<String,String> getUserDetails(){
        HashMap<String,String> user =new HashMap<>();

        //user name
        user.put(KEY_company,pref.getString(KEY_company,null));

        user.put(KEY_Ieccode,pref.getString(KEY_Ieccode,null));


        user.put(KEY_username,pref.getString(KEY_username,null));

        user.put(KEY_password ,pref.getString(KEY_password,null));


        return user;
    }


    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN,false);
    }
}
