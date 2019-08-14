package id.co.medical.management.component;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesComponent {

    Context context;
    public static final String LOGGED_IN_PREF = "logged_in_status";
    public static final String GET_DATA = "GetData";
    public static final String GET_MAKANAN = "GetMakanan";

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

    public SharedPreferencesComponent(Context context) {
        this.context = context;
    }

    //GetDataId
    public void setDataIn(String id) {
        SharedPreferences s = context.getSharedPreferences(GET_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = s.edit();
        editor.putString("Id", id);
        editor.apply();
    }

    public String getDataId() {
        SharedPreferences s = context.getSharedPreferences(GET_DATA, Context.MODE_PRIVATE);
        return s.getString("Id", null);
    }

    public void setDataOut(){
        SharedPreferences s = context.getSharedPreferences(GET_DATA, Context.MODE_PRIVATE);
        s.edit().remove("Id").apply();
    }

    //GetDataActionMakanan
    public void setActionMakananIn(String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(GET_MAKANAN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sort", name);
        editor.apply();
    }

    public String getActionMakanan() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(GET_MAKANAN, Context.MODE_PRIVATE);
        return sharedPreferences.getString("sort", "");
    }

    public void setActionMakananOut(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(GET_MAKANAN, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("sort").apply();
    }
}
