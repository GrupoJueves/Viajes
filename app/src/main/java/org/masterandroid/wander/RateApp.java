package org.masterandroid.wander;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by 2k45y9w789ys on 10/08/2017.
 */

public class RateApp {

    private final static int TRASNLATIONS_UNTIL_PROMPT = 3;//Min number of launches

    private SharedPreferenceStorage spStorage;
    private Context context;
    private AlertDialog alertDialogRateApp;

    public RateApp(Context context, SharedPreferenceStorage spStorage){
        this.spStorage = spStorage;
        this.context = context;
    }

    public void addOneRatePoint(){
        if(spStorage.getIsRatedState()){
            return;
        }
        //Increment Launcher Counter
        int countToRate = spStorage.getCounterToRate()+1;
        spStorage.saveCounterToRate(countToRate);
        //Get date of first launch
        if(countToRate >= TRASNLATIONS_UNTIL_PROMPT){
            showRateDialog();
        }
    }

    private void showRateDialog(){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.dialog_rate_app, null);

        Button btnRate = (Button) layout.findViewById(R.id.btn_dra_rate);
        Button btnRateLater = (Button) layout.findViewById(R.id.btn_dra_rate_later);

        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppStoreToRate(context);
                alertDialogRateApp.dismiss();
            }
        });
        btnRateLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spStorage.saveCounterToRate(0);
                alertDialogRateApp.dismiss();
            }
        });

        builder.setView(layout);
        alertDialogRateApp = builder.create();
        alertDialogRateApp.show();
    }


    public void openAppStoreToRate(Context context){
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
        spStorage.saveIsRatedState(true);
    }
}

