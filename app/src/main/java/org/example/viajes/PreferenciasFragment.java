package org.example.viajes;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by gotz on 14/11/17.
 */

public class PreferenciasFragment extends PreferenceFragment {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}