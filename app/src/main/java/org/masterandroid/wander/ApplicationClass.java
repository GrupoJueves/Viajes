package org.masterandroid.wander;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by 2k45y9w789ys on 17/07/2017.
 */

public class ApplicationClass extends Application {

    private SharedPreferenceStorage spStorage;

    private IInAppBillingService serviceBilling;
    private ServiceConnection serviceConnection;
    private boolean adsEnabled = false;
    private final String ID_ARTICULO = "org.masterandroid.wander.quitaranuncios";

    @Override
    public void onCreate() {
        super.onCreate();
        //Crear Variables
        spStorage = new SharedPreferenceStorage(this);
        serviceConectInAppBilling();
    }

    public void serviceConectInAppBilling() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                serviceBilling = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                serviceBilling = IInAppBillingService.Stub.asInterface(service);
                checkPurchasedInAppProducts();
            }
        };
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void checkPurchasedInAppProducts() {
        Bundle ownedItemsInApp = null;
        if (serviceBilling != null) {
            try {
                ownedItemsInApp = serviceBilling.getPurchases(3, getPackageName(), "inapp", null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            int response = ownedItemsInApp.getInt("RESPONSE_CODE");
            System.out.println(response);
            if (response == 0) {
                ArrayList<String> ownedSkus = ownedItemsInApp.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String> purchaseDataList = ownedItemsInApp.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String> signatureList = ownedItemsInApp.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                String continuationToken = ownedItemsInApp.getString("INAPP_CONTINUATION_TOKEN");

                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = purchaseDataList.get(i);
                    String signature = signatureList.get(i);
                    String sku = ownedSkus.get(i);
                    System.out.println("Inapp Purchase data: " + purchaseData);
                    System.out.println("Inapp Signature: " + signature);
                    System.out.println("Inapp Sku: " + sku);

                    if (sku.equals(ID_ARTICULO)) {
                        Toast.makeText(this, "Inapp comprado: " + sku + "el dia " + purchaseData, Toast.LENGTH_LONG).show();
                        adsEnabled = false;
                    } else {
                        adsEnabled = true;
                    }
                }
                if (purchaseDataList.size() == 0){
                    adsEnabled = true;
                }
            }
        }
    }


    ///////////////////////GETERS AND SETTERS///////////////////////

    public SharedPreferenceStorage getSpStorage(){
        return spStorage;
    }

    public boolean adsEnabled(){
        return adsEnabled;
    }

    public IInAppBillingService getServiceBilling(){
        return serviceBilling;
    }

    ///////////////////////GETERS AND SETTERS\\\\\\\\\\\\\\\\\\\\\\\\

}
