package com.explicate.fitkitchen.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.explicate.fitkitchen.utility.MyPreferences;

/**
 * Created by Mahesh Nikam on 13/01/2017.
 */

public class App extends Application{

    public static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences(MyPreferences.My_PREFRENCES, Context.MODE_PRIVATE);
    }

    public static String getUserId()
    {
        return sharedPreferences.getString(MyPreferences.USER_ID,"");
    }

    public static String getUserName()
    {
        return sharedPreferences.getString(MyPreferences.USER_NAME,"");
    }

    public static String getUserEmail()
    {
        return sharedPreferences.getString(MyPreferences.USER_EMAIL,"");
    }

    public static String getUserPhone()
    {
        return sharedPreferences.getString(MyPreferences.USER_PHONE,"");
    }

    public static String getUserImage()
    {
        return sharedPreferences.getString(MyPreferences.USER_IMAGE,"");
    }
}
