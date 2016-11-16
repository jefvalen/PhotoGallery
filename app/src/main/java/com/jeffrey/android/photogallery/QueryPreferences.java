package com.jeffrey.android.photogallery;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {   //Adding recent ID preference constant pagina 472

    private static final String PREF_LAST_RESULT_ID = "lastResultId";
    private static final String PREF_IS_ALARM_ON = "isAlarmOn";
    private static final String PREF_SEARCH_ON = "searchOn";

    public static String getLastResultId(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(PREF_LAST_RESULT_ID, null);
    }

    public static void setLastResultId(Context context, String lastResultId){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_RESULT_ID, lastResultId)
                .apply();
    }

    public static boolean isAlarmOn (Context context){  //ADDING ALARM STATUS PREFERENCES PAGE 495
        return PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(PREF_IS_ALARM_ON, false);
    }

    public static void setAlarmOn(Context context, boolean isOn){ //ADDING ALARM STATUS PREFERENCES PAGE 495
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_ALARM_ON, isOn)
                .apply();
    }

    public static String getPrefSearchOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SEARCH_ON, null);
    }

    public static void setPrefSearchOn(Context context, String searchOn){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SEARCH_ON, searchOn)
                .apply();
    }
}
