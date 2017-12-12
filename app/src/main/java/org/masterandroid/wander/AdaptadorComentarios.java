package org.masterandroid.wander;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by rodriii on 11/12/17.
 */

public class AdaptadorComentarios extends RecyclerView.Adapter<AdaptadorComentarios.ViewHolder> {

    private LayoutInflater inflador;//Crea Layouts a partir del XML
    private long id;

    private Cursor c;
    private AdaptadorComentarios.OnItemClickListener escucha;
    private View.OnLongClickListener onLongClickListener; // ecuchador long
    private Context contexto;


    //lo utilizaremos desde la actividad
    interface OnItemClickListener {
        public void onClick(AdaptadorComentarios.ViewHolder holder, long idCliente);
    }



    // Constructor
    public AdaptadorComentarios(Context contexto, Cursor c) {
        this.c=c;
        this.escucha=escucha;
    }




    //ViewHolder con los elementos a modificar
    public class ViewHolder extends RecyclerView.ViewHolder
           /* implements View.OnClickListener*/ {
        TextView usuario,fecha,comentario;
        RatingBar valoracion;
        //ImageView foto;

        public ViewHolder(View vista) {
            super(vista);
            //referencio los elemtos a modificar
           usuario = (TextView) vista.findViewById(R.id.name_user);
           fecha = vista.findViewById(R.id.date);
           comentario = vista.findViewById(R.id.recyclerComentario);
           valoracion = vista.findViewById(R.id.recyclerRatingBar);

           // vista.setOnClickListener(this);
            // vista.setOnLongClickListener(this);
        }
       /* @Override
        public void onClick(View view) {
            escucha.onClick(this, obtenerId(getAdapterPosition()));
        }*/


    }

    //Creamos el viewHolder con las vistas de los elementos sin personalizar
    @Override
    public AdaptadorComentarios.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //inflamos la vista
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rating, parent, false); //Cambiar prueba por el nombre del layout del elemento del reciclerview

        //vista.setOnLongClickListener(onLongClickListener);//aplicamos escuchador long a cada vista
        return new AdaptadorComentarios.ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(AdaptadorComentarios.ViewHolder holder, int position) {
        //siguiente elemento del cursor
        c.moveToPosition(position);


       holder.usuario.setText(c.getString(c.getColumnIndex("name")));
       holder.fecha.setText(DateFormat.getDateInstance().format(new Date(c.getLong(c.getColumnIndex("date")))));
        holder.comentario.setText(c.getString(c.getColumnIndex("comment")));
        holder.valoracion.setRating((float)(0+c.getInt(c.getColumnIndex("rating"))));


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




