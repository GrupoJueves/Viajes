package org.example.viajes;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by Ivan on 07/11/2017.
 */

public class AdaptadorVacio extends RecyclerView.Adapter<AdaptadorVacio.ViewHolder> {

    private LayoutInflater inflador;//Crea Layouts a partir del XML
    private long id;

    private Cursor c;
    private OnItemClickListener escucha;
    private View.OnLongClickListener onLongClickListener; // ecuchador long
    private Context contexto;


    //lo utilizaremos desde la actividad
    interface OnItemClickListener {
        public void onClick(ViewHolder holder, long idCliente);
    }



   // Constructor
    public AdaptadorVacio(Context contexto, Cursor c, OnItemClickListener escucha) {
        this.c=c;
        this.escucha=escucha;
    }




    //ViewHolder con los elementos a modificar
    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
       //TextView titulo, checked, etc;;
        //ImageView foto;

        public ViewHolder(View vista) {
            super(vista);
            //referencio los elemtos a modificar
            // EJEMPLO   Titulo = (TextView) vista.findViewById(R.id.titulo_view);

            vista.setOnClickListener(this);
            // vista.setOnLongClickListener(this);
        }
        @Override
        public void onClick(View view) {
            escucha.onClick(this, obtenerId(getAdapterPosition()));
        }


    }

    //Creamos el viewHolder con las vistas de los elementos sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //inflamos la vista
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.prueba, parent, false); //Cambiar prueba por el nombre del layout del elemento del reciclerview

        vista.setOnLongClickListener(onLongClickListener);//aplicamos escuchador long a cada vista
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //siguiente elemento del cursor
        c.moveToPosition(position);


        //Modifico los elementos de la vista
        // EJEMPLO  holder.Titulo.setText(c.getString(c.getColumnIndex("title")));


    }



    @Override
    public int getItemCount() {
        if (c != null)
            return c.getCount();
        return 0;
    }

    private long obtenerId(int posicion) {
        if (c != null) {
            if (c.moveToPosition(posicion)) {
                return c.getLong(c.getColumnIndex("_id")); //se puede especipifar el id, por ejemplo route_id
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    //lo usariamos desde la actividad
    public void setOnItemLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;

    }

    public long getId(int position) {
        id= obtenerId(position);
        return id;
    }
}




