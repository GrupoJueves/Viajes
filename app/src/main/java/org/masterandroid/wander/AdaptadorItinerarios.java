package org.masterandroid.wander;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;


/**
 * Created by Ivan on 07/11/2017.
 */

public class AdaptadorItinerarios extends RecyclerView.Adapter<AdaptadorItinerarios.ViewHolder> implements ItemTouchHelperAdapter {

    private LayoutInflater inflador;//Crea Layouts a partir del XML
    private long id;
    private Context contexto;

    private Cursor c;
    private OnItemClickListener escucha;
    private View.OnLongClickListener onLongClickListener; // escuchador long

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position, int direction) {

        if (direction == ItemTouchHelper.LEFT){
        //Obtengo el identificador del objeto a borrar
        int id = (int) obtenerId(position);
        //inicializo la base de datos, si no existe la crea
        ConsultaBD.inicializaBD(contexto);
        //obtengo el usuario
        int user = ConsultaBD.getUser(id);
        //elimino la ruta
        ConsultaBD.deleteRoute(id);
        //vuelvo a cargar el cursor con los itinerarios
        c=ConsultaBD.listadoItinerarios(user);

        notifyItemRemoved(position);

        }else{
            //Obtengo el identificador del objeto a modificar
            int id = (int) obtenerId(position);
            //inicializo la base de datos, si no existe la crea
            ConsultaBD.inicializaBD(contexto);
            //obtengo el usuario
            int user = ConsultaBD.getUser(id);
            //obtengo el valor actual del check
            boolean check = obtenerCheck(position);
            //Cambio la ruta
            ConsultaBD.changeCheck(id,check);
            //vuelvo a cargar el cursor con los itinerarios
            c=ConsultaBD.listadoItinerarios(user);


            notifyItemChanged(position);


        }

    }


    //lo utilizaremos desde la actividad
    interface OnItemClickListener {
        public void onClick(ViewHolder holder, long id);

    }

   // Constructor
    public AdaptadorItinerarios(Context contexto, Cursor c, OnItemClickListener escucha) {
        this.c=c;
        this.escucha=escucha;
        this.contexto = contexto;
    }

    //ViewHolder con los elementos a modificar
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView titulo, fecha;
        ImageView check;
        com.makeramen.roundedimageview.RoundedImageView  imageRef;
        android.support.constraint.ConstraintLayout itemView;

        public ViewHolder(View vista) {
            super(vista);
            //referencio los elemtos a modificar
            titulo = (TextView) vista.findViewById(R.id.titulo);
            check = (ImageView) vista.findViewById(R.id.visto);
            fecha = vista.findViewById(R.id.fecha);
            imageRef = vista.findViewById(R.id.referencia);
            itemView = vista.findViewById(R.id.contenedor);

            vista.setOnClickListener(this);

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
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.targeta_itinerarios, parent, false); //Cambiar elemnto lista por el nombre del layout del elemento del reciclerview

        vista.setOnLongClickListener(onLongClickListener); //aplicamos escuchador long a cada vista
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Bitmap bitmap1;
        Bitmap circularBitmap1;
        Palette palette;

        //siguiente elemento del cursor
        c.moveToPosition(position);

        //Modifico los elementos de la vista
        holder.titulo.setText(c.getString(c.getColumnIndex("title")));
        holder.fecha.setText(DateFormat.getDateInstance().format(new Date(c.getLong(c.getColumnIndex("date")))));
        holder.itemView.setBackgroundResource(R.color.itemRecyclerCheck);
        int tele = c.getInt(c.getColumnIndex("checked"));
         if (tele == 0) {
                holder.check.setVisibility(View.INVISIBLE);
                holder.itemView.setBackgroundResource(R.color.itemRecycler);
            }else{
             holder.check.setVisibility(View.VISIBLE);
         }
        int valorref = c.getInt(c.getColumnIndex("ref"));
        switch (valorref){
            case 1:

                holder.imageRef.setImageResource(R.drawable.ref1);
                break;
            case 2:

                holder.imageRef.setImageResource(R.drawable.ref2);
                break;
            case 3:

                holder.imageRef.setImageResource(R.drawable.ref3);
                break;
            case 4:

                holder.imageRef.setImageResource(R.drawable.ref4);
                break;
            case 5:

                holder.imageRef.setImageResource(R.drawable.ref5);
                break;
            case 6:

                holder.imageRef.setImageResource(R.drawable.ref6);
                break;
            default:

                holder.imageRef.setImageResource(R.drawable.ref1);
                break;
        }


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

    private boolean obtenerCheck(int posicion) {
        boolean check = false;
        if (c != null) {
            if (c.moveToPosition(posicion)) {
                int estado = c.getInt(c.getColumnIndex("checked"));
                if (estado == 0){
                    return true;
                }else{
                    return false;
                }
            } else {
                return check;
            }
        } else {
            return check;
        }
    }




    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {



        public ItemViewHolder(View itemView) {
            super(itemView);

        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}




