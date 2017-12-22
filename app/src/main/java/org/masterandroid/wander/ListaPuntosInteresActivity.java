package org.masterandroid.wander;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class ListaPuntosInteresActivity extends AppCompatActivity implements AdaptadorPuntosInteres.OnItemClickListener {

    private RecyclerView recyclerViewitinerario;
    public AdaptadorPuntosInteres adaptador;
    private RecyclerView.LayoutManager lManager;
    private LinearLayout vacio;

    private ProgressBar cargando;

    private ItemTouchHelper mItemTouchHelper;

    private FlowingDrawer mDrawer;

    //Valor para la llamada a añadir poi
    final static int RESULTADO_AÑADIR = 1500;

    //Variable donde almacenare el identificador de la ruta
    private long id_ruta;

    private SharedPreferences pref;

    private String nombrePuntoInteres = "";


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
        setContentView(R.layout.activity_lista_puntos_interes);




        //instanciomas aplication para publi y pagos
        app = (ApplicationClass) getApplication();



        //pagos

        app = (ApplicationClass) getApplication();
        serviceBilling = app.getServiceBilling();
        quitarAnunciosToken = app.getQuitarAnunciosToken();

        //Shared prefereces storage (Esto seria mejor meterlo en la clase aplication e inicializarlo solo una vez)
        spStorage = app.getSpStorage();
        //Rate App
        rateApp = new RateApp(this, spStorage);

        //recojo el valor del identificador de la ruta
        Bundle extras = getIntent().getExtras();
        id_ruta = extras.getLong("id", -1);

        //inicializo la base de datos, si no existe la crea
        ConsultaBD.inicializaBD(this);

        //cambio los valores del toolbar
        cambiarTitulo();

        //imagen aprendizaje
        abrePrimeraVez();

        //referenci el progresbar
        cargando=findViewById(R.id.cargando);



        //Inicializar los elementos
        listaPuntosInteres();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImageView imagen = findViewById(R.id.aprendizaje);
                imagen.setVisibility(View.GONE);

                /*TextView explanation_1 = findViewById(R.id.ap_visualiza_itinerario_mapa);
                explanation_1.setVisibility(View.GONE);
                TextView explanation_2 = findViewById(R.id.ap_pulsa_crear_poi);
                explanation_2.setVisibility(View.GONE);
                TextView explanation_3 = findViewById(R.id.ap_desliza_marcar_poi);
                explanation_3.setVisibility(View.GONE);
                TextView explanation_4 = findViewById(R.id.ap_pulsado_posicion_poi);
                explanation_4.setVisibility(View.GONE);
                TextView explanation_5 = findViewById(R.id.ap_desliza_eliminar_poi);
                explanation_5.setVisibility(View.GONE);*/

                TextView b_entendido = findViewById(R.id.entendido);
                b_entendido.setVisibility(View.GONE);

                Intent i = new Intent(ListaPuntosInteresActivity.this, SelectPOIGrafic.class);
                i.putExtra("id", id_ruta);
                startActivityForResult(i, RESULTADO_AÑADIR);

                cargando.setVisibility(View.VISIBLE);

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
        NavigationView navigationView = (NavigationView) findViewById(R.id.vNavigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressWarnings("StatementWithEmptyBody")
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_itinerario_mapa) {
                    //apliar directions
                    Intent i = new Intent(ListaPuntosInteresActivity.this, MapaRuta.class);
                    i.putExtra("id", id_ruta);
                    startActivityForResult(i,2334);
                    cargando.setVisibility(View.VISIBLE);


                } else if (id == R.id.nav_visitado) {
                    //Toast.makeText(getApplicationContext(), "Marcar itinerario como visitado", Toast.LENGTH_SHORT).show();
                    ConsultaBD.changeCheck((int)id_ruta,true);
                    setResult(RESULT_OK);

                /*}else if (id == R.id.nav_eliminar) {
                    ConsultaBD.deleteRoute((int)id_ruta);
                    setResult(RESULT_OK);

                    finish();*/

                } else if (id == R.id.nav_salir) {
                   setResult(1983);
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
                    Intent i = new Intent(ListaPuntosInteresActivity.this, PerfilUsuarioActivity.class);
                    startActivity(i);
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
        setAds(app.adsEnabled());
    }

    public void listaPuntosInteres(){

        //Obtenemos el cursor con todos los puntos del usuario
        Cursor c = ConsultaBD.listadoPOIItinerario((int)id_ruta);

        vacio = findViewById(R.id.vacio);
        recyclerViewitinerario = (RecyclerView) findViewById(R.id.reciclador);
        recyclerViewitinerario.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recyclerViewitinerario.setLayoutManager(lManager);


        //creamos el adaptador
        adaptador = new AdaptadorPuntosInteres(this, c, this);
        //Esto seria para el caso de que no existireran rutas para este usuario
        // emptyview seria lo que se mostraria en una lista vacia
        if (adaptador.getItemCount() == 0) {
            vacio.setVisibility(View.VISIBLE);
            recyclerViewitinerario.setVisibility(View.GONE);
        } else {

            vacio.setVisibility(View.GONE);
            recyclerViewitinerario.setVisibility(View.VISIBLE);
        }
       /* //Implementamos la funcionalidad del longClick
        adaptador.setOnItemLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(final View v) {
                final int posicion = recyclerViewitinerario.getChildAdapterPosition(v);
                final long id = adaptador.getId(posicion);
                AlertDialog.Builder menu = new AlertDialog.Builder(ListaPuntosInteresActivity.this);
                CharSequence[] opciones = { getString(R.string.abrir), getString(R.string.eliminar), getString(R.string.visitado)};
                menu.setItems(opciones, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int opcion) {
                        switch (opcion) {
                            case 0: //Abrir
                                Intent intent = new Intent(ListaPuntosInteresActivity.this, DetailPOI.class);
                                //intent.putExtra("id", id);
                                startActivity(intent);
                                break;
                            case 1: //Borrar

                                new AlertDialog.Builder(ListaPuntosInteresActivity.this)
                                        .setTitle(R.string.borrar_poi)
                                        .setMessage(R.string.borrar_poi_pregunta)
                                        .setPositiveButton(R.string.confirmar, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                ConsultaBD.deletePoiRoute((int)id);
                                                listaPuntosInteres();

                                            }
                                        })
                                        .setNegativeButton(R.string.cancelar, null)
                                        .show();

                                break;
                            case 2: //Marcar como visto
                                ConsultaBD.changeCheckPoi((int) id, true);
                                listaPuntosInteres();
                                break;



                        }
                    }
                });
                menu.create().show();
                return true;
            }
        });*/

        //rellenamos el reciclerview
        recyclerViewitinerario.setAdapter(adaptador);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adaptador);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerViewitinerario);
    }

    //accion de pulsar sobre un elemento de la lista
    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(AdaptadorPuntosInteres.ViewHolder holder, long id) {
        Intent intent = new Intent(this, DetailPOI.class);
        intent.putExtra("id", id);
        startActivityForResult(intent,RESULTADO_AÑADIR, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    //Accion a realizar al volver del otra actividad
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        cargando.setVisibility(View.GONE);
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case INAPP_BILLING: {
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
                            Toast.makeText(this, "Compra completada", Toast.LENGTH_LONG).show();
                            setAds(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case RESULTADO_AÑADIR:
                if(resultCode==1983){
                    setResult(1983);
                    finish();
                }
                View myView = findViewById(R.id.reciclador);
                ViewGroup parent = (ViewGroup) myView.getParent();
                parent.removeView(myView);

                LinearLayout layout = findViewById(R.id.vista_contenedora);

                LayoutInflater inflater = LayoutInflater.from(this);
                RecyclerView nuevoLayout = (RecyclerView) inflater.inflate(R.layout.recycler, null, false);
                layout.addView(nuevoLayout);

                listaPuntosInteres();
                break;
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


    private void rateUsBtn(){
        rateApp.openAppStoreToRate(ListaPuntosInteresActivity.this);
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
            Toast.makeText(this, "InApp Billing service not available", Toast.LENGTH_LONG).show();
        }
    }

    //Funcion que cambia los valores del toolbar
    public void cambiarTitulo(){
        ImageView icono = findViewById(R.id.ref);
        TextView itinerario = findViewById(R.id.itinerario);

        Itinerario route = ConsultaBD.infoRoute((int) id_ruta);

        itinerario.setText(route.getTitulo());
        switch (route.getRef()){
            case 1:
                icono.setImageResource(R.drawable.ref1_1);
                break;
            case 2:
                icono.setImageResource(R.drawable.ref2_1);
                break;
            case 3:
                icono.setImageResource(R.drawable.ref3_1);
                break;
            case 4:
                icono.setImageResource(R.drawable.ref4_1);
                break;
            case 5:
                icono.setImageResource(R.drawable.ref5_1);
                break;
            case 6:
                icono.setImageResource(R.drawable.ref6_1);
                break;
            default:
                icono.setImageResource(R.drawable.ref1_1);
                break;
        }

    }

    public void abrePrimeraVez(){
        SharedPreferences sp = getSharedPreferences("mispreferencias", 0);
        boolean primerAcceso = sp.getBoolean("abrePrimeraVezPois", true);
        if (primerAcceso) {
            ImageView imagen = findViewById(R.id.aprendizaje);
            imagen.setVisibility(View.VISIBLE);

            /*TextView explanation_1 = findViewById(R.id.ap_visualiza_itinerario_mapa);
            explanation_1.setVisibility(View.VISIBLE);
            TextView explanation_2 = findViewById(R.id.ap_pulsa_crear_poi);
            explanation_2.setVisibility(View.VISIBLE);
            TextView explanation_3 = findViewById(R.id.ap_desliza_marcar_poi);
            explanation_3.setVisibility(View.VISIBLE);
            TextView explanation_4 = findViewById(R.id.ap_pulsado_posicion_poi);
            explanation_4.setVisibility(View.VISIBLE);
            TextView explanation_5 = findViewById(R.id.ap_desliza_eliminar_poi);
            explanation_5.setVisibility(View.VISIBLE);*/

            TextView b_entendido = findViewById(R.id.entendido);
            b_entendido.setVisibility(View.VISIBLE);
            switch (getString(R.string.locale)){
                case "1":
                    imagen.setImageResource(R.drawable.aprendizaje_pois);
                    break;
                case "2":
                    imagen.setImageResource(R.drawable.aprendizaje_pois_es);
                    break;
                case "3":
                    imagen.setImageResource(R.drawable.aprendizaje_pois_ca);
                    break;
                case "4":
                    imagen.setImageResource(R.drawable.aprendizaje_pois_eu);
                    break;
                default:
                    imagen.setImageResource(R.drawable.aprendizaje_pois);
                    break;
            }

            SharedPreferences.Editor e = sp.edit();
            e.putBoolean("abrePrimeraVezPois", false).commit();
        }
    }
    public void entendidoPois(View view){
        ImageView imagen = findViewById(R.id.aprendizaje);
        imagen.setVisibility(View.GONE);

        /*TextView explanation_1 = findViewById(R.id.ap_visualiza_itinerario_mapa);
        explanation_1.setVisibility(View.GONE);
        TextView explanation_2 = findViewById(R.id.ap_pulsa_crear_poi);
        explanation_2.setVisibility(View.GONE);
        TextView explanation_3 = findViewById(R.id.ap_desliza_marcar_poi);
        explanation_3.setVisibility(View.GONE);
        TextView explanation_4 = findViewById(R.id.ap_pulsado_posicion_poi);
        explanation_4.setVisibility(View.GONE);
        TextView explanation_5 = findViewById(R.id.ap_desliza_eliminar_poi);
        explanation_5.setVisibility(View.GONE);*/

        TextView b_entendido = findViewById(R.id.entendido);
        b_entendido.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        } else {
            super.onBackPressed();
        }
    }

}
