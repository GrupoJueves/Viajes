package org.masterandroid.wander;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


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
        TextView titulo;
        ImageView check;

        public ViewHolder(View vista) {
            super(vista);
            //referencio los elemtos a modificar
            titulo = (TextView) vista.findViewById(R.id.titulo);
            check = (ImageView) vista.findViewById(R.id.visto);

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
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_itinerario, parent, false); //Cambiar elemnto lista por el nombre del layout del elemento del reciclerview

        vista.setOnLongClickListener(onLongClickListener); //aplicamos escuchador long a cada vista
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //siguiente elemento del cursor
        c.moveToPosition(position);

        //Modifico los elementos de la vista
        holder.titulo.setText(c.getString(c.getColumnIndex("title")));
        int tele = c.getInt(c.getColumnIndex("checked"));
         if (tele == 0) {
                holder.check.setVisibility(View.INVISIBLE);
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




