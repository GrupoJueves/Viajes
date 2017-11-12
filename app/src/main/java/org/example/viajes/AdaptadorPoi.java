package org.example.viajes;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Ivan on 12/11/2017.
 */

public class AdaptadorPoi extends RecyclerView.Adapter<AdaptadorPoi.ViewHolder> {

    private LayoutInflater inflador;//Crea Layouts a partir del XML
    private long id;

    private Cursor c;
    private AdaptadorPoi.OnItemClickListener escucha;
    private View.OnLongClickListener onLongClickListener; // ecuchador long
    private Context contexto;


    //lo utilizaremos desde la actividad
    interface OnItemClickListener {
        public void onClick(AdaptadorPoi.ViewHolder holder, long id);
    }



    // Constructor
    public AdaptadorPoi(Context contexto, Cursor c, AdaptadorPoi.OnItemClickListener escucha) {
        this.c=c;
        this.escucha=escucha;
    }




    //ViewHolder con los elementos a modificar
    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView titulo;
        ImageView check;

        public ViewHolder(View vista) {
            super(vista);
            //referencio los elemtos a modificar
            titulo = (TextView) vista.findViewById(R.id.titulo);
            check = (ImageView) vista.findViewById(R.id.visto);

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
    public AdaptadorPoi.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //inflamos la vista
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.prueba, parent, false); //Cambiar elemnto lista por el nombre del layout del elemento del reciclerview

        vista.setOnLongClickListener(onLongClickListener);//aplicamos escuchador long a cada vista, aunque aun no lo usamos
        return new AdaptadorPoi.ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(AdaptadorPoi.ViewHolder holder, int position) {
        //siguiente elemento del cursor
        c.moveToPosition(position);


        //Modifico los elementos de la vista
        holder.titulo.setText(c.getString(c.getColumnIndex("title")));
       /* int tele = c.getInt(c.getColumnIndex("checked"));
        /*if (tele == 0) {
            holder.check.setVisibility(View.INVISIBLE);
        }*/



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
                return c.getLong(c.getColumnIndex("_id")); //se puede especipicar el id, por ejemplo route_id, pero _id es general y funcionara siempre
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




