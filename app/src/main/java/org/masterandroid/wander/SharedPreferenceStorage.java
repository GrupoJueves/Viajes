package org.masterandroid.wander;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 2k45y9w789ys on 17/07/2017.
 */

public class SharedPreferenceStorage {
    private static String PREFERENCES = "preferences";
    private Context context;

    public SharedPreferenceStorage(Context context) {
        this.context = context;
    }


    /////////////////SAVE AND GET USAGE COUNTERS/////////////////////

    public void saveIsRatedState(Boolean isRatedState){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isRatedState", isRatedState);
        editor.apply();
    }

    public Boolean getIsRatedState(){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean("isRatedState", false);
    }

    public void saveCounterToRate(int translationsCount){//este creo que es el que he utilizado para el rate.
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("RaterCount", translationsCount);
        editor.apply();
    }

    public int getCounterToRate(){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getInt("RaterCount", 0);
    }
    /////////////////SAVE AND GET USAGE COUNTERS\\\\\\\\\\\\\\\\\\\\\\\\\\

}
