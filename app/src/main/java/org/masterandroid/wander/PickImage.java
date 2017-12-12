package org.masterandroid.wander;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class PickImage extends AppCompatActivity {
    private int ref = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_image);
    }

    public void selecciona (View v){
        String tag = v.getTag().toString();
        switch (tag){
            case "1":
                ref = 1;
                break;
            case "2":
                ref = 2;
                break;
            case "3":
                ref =3;
                break;
            case "4":
                ref = 4;
                break;
            case "5":
                ref = 5;
                break;
            case "6":
                ref = 6;
                break;
            default:
                ref = 7;
                break;
        }

        Intent intent = new Intent();
        intent.putExtra("ref", ref);
        setResult(RESULT_OK, intent);
        finish();

    }





}
