package com.diabetes.manager.component;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceComponent {
    Context context;
    public static final String LOGGED_IN_PREF = "logged_in_status";

    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    //LoginSession
    public static void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED_IN_PREF, loggedIn);
        editor.apply();
    }

    public static boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean(LOGGED_IN_PREF, false);
    }

    public SharedPreferenceComponent(Context context) {
        this.context = context;
    }

    //GetDataId
    public void setDataIn(String id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("GetData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Id", id);
        editor.commit();
    }

    public String getDataId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("GetData", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Id", "");
    }

    //GetDataActionMenu
    public void setActionMenuIn(String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("GetMenu", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sort", name);
        editor.commit();
    }

    public String getActionMenu() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("GetMenu", Context.MODE_PRIVATE);
        return sharedPreferences.getString("sort", "");
    }

    public void setActionMenuOut(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("GetMenu", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("sort").commit();
    }
}
