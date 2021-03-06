package org.masterandroid.wander;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;

public class ListaItinerariosActivity extends AppCompatActivity implements AdaptadorItinerarios.OnItemClickListener {
    private RecyclerView recyclerViewClientes;
    private LinearLayout vacio;
    public AdaptadorItinerarios adaptador;
    private RecyclerView.LayoutManager lManager;
    private SharedPreferences pref;
    private int id;

    private String nombreItinerario = "";
    private FlowingDrawer mDrawer;

    private ItemTouchHelper mItemTouchHelper;

    //RateApp
    private RateApp rateApp;
    //Shared preference storage
    private SharedPreferenceStorage spStorage;

    //Anuncios
    private AdView adView;
    private InterstitialAd interstitialAd;
    private String ID_BLOQUE_ANUNCIOS_INTERSTICIAL;
    private String ID_INICIALIZADOR_ADS;
    private boolean showInterticial=false;

    //Clases tipo sigleton
    private ApplicationClass app;

    //InAppBilling
    private IInAppBillingService serviceBilling;
    private final String ID_ARTICULO = "org.masterandroid.wander.quitaranuncios";
    private final int INAPP_BILLING = 1;
    private final String developerPayLoad = "clave de seguridad";
    private String quitarAnunciosToken = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_itinerarios);



        abrePrimeraVez();

        app = (ApplicationClass) getApplication();
        serviceBilling = app.getServiceBilling();
        quitarAnunciosToken = app.getQuitarAnunciosToken();

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        id = pref.getInt("id", -1);
        //Log.e("id_user en prefe"," "+id);
        if (id == -1) {
            this.finish();
        }

        //Shared prefereces storage (Esto seria mejor meterlo en la clase aplication e inicializarlo solo una vez)
        spStorage = app.getSpStorage();
        //Rate App
        rateApp = new RateApp(this, spStorage);


        //inicializo la base de datos, si no existe la crea
        ConsultaBD.inicializaBD(this);



        //Inicializar los elementos
        listaitinerarios();

        //pongo la imagen del perfil
        ponerImagen();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imagen = findViewById(R.id.aprendizaje);
                imagen.setVisibility(View.GONE);
                /*TextView explanation_1 = findViewById(R.id.ap_pulsa_menu);
                explanation_1.setVisibility(View.GONE);
                TextView explanation_2 = findViewById(R.id.ap_pulsa_itinerario);
                explanation_2.setVisibility(View.GONE);
                TextView explanation_3 = findViewById(R.id.ap_desliza_marcar_itinerario);
                explanation_3.setVisibility(View.GONE);
                TextView explanation_4 = findViewById(R.id.ap_desliza_eliminar_itinerario);
                explanation_4.setVisibility(View.GONE);*/
                TextView b_entendido = findViewById(R.id.entendido);
                b_entendido.setVisibility(View.GONE);


                Intent intent = new Intent(ListaItinerariosActivity.this, CrearItinerario.class);
                intent.putExtra("id", (long)id);
                startActivityForResult(intent,1694);

                rateApp.addOneRatePoint();

                if(showInterticial) {
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    }
                }
            }
        });

        // Toolbar
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final Intent intent2 = new Intent(this, InicioSesionActivity.class);

        NavigationView navigationView = (NavigationView) findViewById(R.id.vNavigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressWarnings("StatementWithEmptyBody")
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
               if (id == R.id.nav_preferencias) {
                    lanzarPreferencias(null);
                    return true;
                } else if (id == R.id.nav_salir) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("id", 0);
                    editor.putBoolean("rememberMe", false);
                    editor.commit();
                    startActivity(intent2);
                    finish();
                } else if (id == R.id.nav_rate_us) {
                    rateUsBtn();
                    return true;
                }else if (id == R.id.nav_shareapp) {
                    shareAppBtn();
                    return true;
                }else if (id == R.id.nav_privacy) {
                    privacyPolicyBtn();
                    return true;
                } else if (id == R.id.user_menu) {
                    Intent i = new Intent(ListaItinerariosActivity.this, PerfilUsuarioActivity.class);
                    startActivityForResult(i,33);
                } else if (id == R.id.nav_quitar_anuncios) {
                    comprarQuitarAds();
                    return true;
                }


                mDrawer.closeMenu();
                return true;
            }
        });
        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.toggleMenu();
            }
        });



        //Anuncios:
        ID_BLOQUE_ANUNCIOS_INTERSTICIAL = getString(R.string.ads_intersticial_id_test);
        ID_INICIALIZADOR_ADS = getString(R.string.ads_initialize_test);

        MobileAds.initialize(this, ID_INICIALIZADOR_ADS);
        adView = (AdView) findViewById(R.id.adView);
        //setAds(true);
        setAds(app.adsEnabled());
    }


    public void listaitinerarios() {
        int id = pref.getInt("id", 0);
        //Obtenemos el cursor con todas las rutas del usuario
        Cursor c = ConsultaBD.listadoItinerarios(id);

        vacio = findViewById(R.id.vacio);
        recyclerViewClientes = (RecyclerView) findViewById(R.id.reciclador);
        recyclerViewClientes.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recyclerViewClientes.setLayoutManager(lManager);

        //creamos el adaptador
        adaptador = new AdaptadorItinerarios(this, c, this);
        //Esto seria para el caso de que no existireran rutas para este usuario
        // emptyview seria lo que se mostraria en una lista vacia
        if (adaptador.getItemCount() == 0) {
            vacio.setVisibility(View.VISIBLE);
            recyclerViewClientes.setVisibility(View.GONE);
        } else {
            vacio.setVisibility(View.GONE);
            recyclerViewClientes.setVisibility(View.VISIBLE);
        }




        //rellenamos el reciclerview
        recyclerViewClientes.setAdapter(adaptador);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adaptador);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerViewClientes);
    }

    //accion de pulsar sobre un elemento de la lista
    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(AdaptadorItinerarios.ViewHolder holder, long id) {
        Intent intent = new Intent(this, ListaPuntosInteresActivity.class);
        intent.putExtra("id", id);
        //ActivityOptionsCompat options = ActivityOptionsCompat. makeSceneTransitionAnimation(ListaItinerariosActivity.this, new Pair<View, String>(holder.imageRef, getString(R.string.transition_name_icon)));
        startActivityForResult(intent,1694, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }


    @Override
    public void onBackPressed() {
        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        } else {
            super.onBackPressed();
        }
    }


    public void lanzarPreferencias(View view) {
        Intent i = new Intent(this, PreferenciasActivity.class);
        startActivity(i);
    }

    private void shareAppBtn(){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, getPackageName());
        String sAux = "http://play.google.com/store/apps/details?id=" + getPackageName();
        i.putExtra(Intent.EXTRA_TEXT, sAux);
        startActivity(Intent.createChooser(i, "choose one"));
    }

    private void privacyPolicyBtn(){
        Uri uri2 = Uri.parse("https://drive.google.com/open?id=1rRXJLMj4ixMvFJNHAiYIYHp5sU2Xk2I9");
        Intent intent2 = new Intent(Intent.ACTION_VIEW, uri2);
        startActivity(intent2);
    }

    private void rateUsBtn(){
        rateApp.openAppStoreToRate(ListaItinerariosActivity.this);
    }

    public void entendido(View view){
        ImageView imagen = findViewById(R.id.aprendizaje);
        imagen.setVisibility(View.GONE);
        /*TextView explanation_1 = findViewById(R.id.ap_pulsa_menu);
        explanation_1.setVisibility(View.GONE);
        TextView explanation_2 = findViewById(R.id.ap_pulsa_itinerario);
        explanation_2.setVisibility(View.GONE);
        TextView explanation_3 = findViewById(R.id.ap_desliza_marcar_itinerario);
        explanation_3.setVisibility(View.GONE);
        TextView explanation_4 = findViewById(R.id.ap_desliza_eliminar_itinerario);
        explanation_4.setVisibility(View.GONE);*/
        TextView b_entendido = findViewById(R.id.entendido);
        b_entendido.setVisibility(View.GONE);
    }

    public void abrePrimeraVez(){
        SharedPreferences sp = getSharedPreferences("mispreferencias", 0);
        boolean primerAcceso = sp.getBoolean("abrePrimeraVez", true);
        if (primerAcceso) {
            ImageView imagen = findViewById(R.id.aprendizaje);
            imagen.setVisibility(View.VISIBLE);
            /*TextView explanation_1 = findViewById(R.id.ap_pulsa_menu);
            explanation_1.setVisibility(View.VISIBLE);
            TextView explanation_2 = findViewById(R.id.ap_pulsa_itinerario);
            explanation_2.setVisibility(View.VISIBLE);
            TextView explanation_3 = findViewById(R.id.ap_desliza_marcar_itinerario);
            explanation_3.setVisibility(View.VISIBLE);
            TextView explanation_4 = findViewById(R.id.ap_desliza_eliminar_itinerario);
            explanation_4.setVisibility(View.VISIBLE);*/
            TextView b_entendido = findViewById(R.id.entendido);
            b_entendido.setVisibility(View.VISIBLE);
            switch (getString(R.string.locale)){
                case "1":
                    imagen.setImageResource(R.drawable.aprendizaje_itinerario);
                    break;
                case "2":
                    imagen.setImageResource(R.drawable.aprendizaje_itinerario_es);
                    break;
                case "3":
                    imagen.setImageResource(R.drawable.aprendizaje_itinerario_ca);
                    break;
                case "4":
                    imagen.setImageResource(R.drawable.aprendizaje_itinerario_eu);
                    break;
                default:
                    imagen.setImageResource(R.drawable.aprendizaje_itinerario);
                    break;
            }

            SharedPreferences.Editor e = sp.edit();
            e.putBoolean("abrePrimeraVez", false).commit();
        }
    }

    private void setAds(Boolean adsEnabled) {
        if (adsEnabled) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);

            interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId(ID_BLOQUE_ANUNCIOS_INTERSTICIAL);
            interstitialAd.loadAd(new AdRequest.Builder().build());
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });
            showInterticial = true;
        } else {
            showInterticial = false;
            adView.setVisibility(View.GONE);
        }
    }

    public void comprarQuitarAds() {
        if (serviceBilling != null) {
            Bundle buyIntentBundle = null;
            try {
                buyIntentBundle = serviceBilling.getBuyIntent(3, getPackageName(), ID_ARTICULO, "inapp", developerPayLoad);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            try {
                if (pendingIntent != null) {
                    startIntentSenderForResult(pendingIntent.getIntentSender(), INAPP_BILLING, new Intent(), 0, 0, 0);
                }
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, R.string.servicio_no_disponible, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INAPP_BILLING:
                {
                int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
                if (resultCode == RESULT_OK) {
                    try {
                        JSONObject jo = new JSONObject(purchaseData);
                        String sku = jo.getString("productId");
                        String developerPayload = jo.getString("developerPayload");
                        String purchaseToken = jo.getString("purchaseToken");
                        if (sku.equals(ID_ARTICULO)) {
                            Toast.makeText(this, R.string.compra_completada, Toast.LENGTH_LONG).show();
                            setAds(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                }
                break;
            case 1694:

                if(resultCode==1983){

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("id", 0);
                    editor.putBoolean("rememberMe", false);
                    editor.commit();
                    Intent intent2 = new Intent(this, InicioSesionActivity.class);
                    startActivity(intent2);
                    finish();
                }


                   // listaitinerarios();
                    View myView = findViewById(R.id.reciclador);
                    ViewGroup parent = (ViewGroup) myView.getParent();
                    parent.removeView(myView);

                    LinearLayout layout = findViewById(R.id.vista_contenedora);

                    LayoutInflater inflater = LayoutInflater.from(this);
                    RecyclerView nuevoLayout = (RecyclerView) inflater.inflate(R.layout.recycler, null, false);
                    layout.addView(nuevoLayout);
                    listaitinerarios();




                break;
            case 33:
                ponerImagen();
                break;
        }
    }

    public void backToBuy(String token) {
        if (serviceBilling != null) {
            try {
                int response = serviceBilling.consumePurchase(3, getPackageName(), token);
                System.out.print("Respuesta de backToBuy: " + response);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    //pone la imagen del perfil en el toolbar
    public void ponerImagen(){
        String url = "";
        url = ConsultaBD.getPhoto(id);
        com.makeramen.roundedimageview.RoundedImageView imagen = findViewById(R.id.usuario);
        //poner la foto
        if(!url.equals("") && url != null){

            imagen.setVisibility(View.VISIBLE);
            //Log.e("url",""+url);
            ponerFoto(imagen,url);

        }else{
            imagen.setVisibility(View.GONE);
        }

    }

    protected void ponerFoto(ImageView imageView, String uri) {
        if (uri != null) {
            imageView.setImageBitmap(reduceBitmap(this, uri, 1024, 1024));
        } else {
            imageView.setImageBitmap(null);
        }
    }

    public static Bitmap reduceBitmap(Context contexto, String uri, int maxAncho, int maxAlto) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(contexto.getContentResolver()
                    .openInputStream(Uri.parse(uri)), null, options);
            options.inSampleSize = (int) Math.max(
                    Math.ceil(options.outWidth / maxAncho),
                    Math.ceil(options.outHeight / maxAlto));
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(contexto.getContentResolver() .openInputStream(Uri.parse(uri)), null, options);
        } catch (FileNotFoundException e) {
            Toast.makeText(contexto, "Fichero/recurso no encontrado",
                    Toast.LENGTH_LONG).show(); e.printStackTrace();
            return null; }
    }



}
