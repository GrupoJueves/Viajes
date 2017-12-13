package org.masterandroid.wander;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.WindowManager;

/**
 * Created by gotz on 13/12/17.
 */

public class SplashScreenActivity extends Activity {
    private final int DURACION_SPLASH = 1500;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splashscreen);
        context = this;
        ConsultaBD.inicializaBD(this);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                Boolean rememberMe = pref.getBoolean("rememberMe",false);
                Intent intent;
                if (rememberMe){
                    intent = new Intent(context, ListaItinerariosActivity.class);
                }
                else {
                    intent = new Intent(context, InicioSesionActivity.class);
                }
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(context,
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                startActivity(intent,bundle);
                finish();
            }
        }, DURACION_SPLASH);
    }
}
