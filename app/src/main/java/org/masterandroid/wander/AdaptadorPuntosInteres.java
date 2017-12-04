package org.masterandroid.wander;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by rodriii on 15/11/17.
 */

public class AdaptadorPuntosInteres extends RecyclerView.Adapter<AdaptadorPuntosInteres.ViewHolder> implements ItemTouchHelperAdapter{

    private LayoutInflater inflador; //Crea Layouts a partir del XML
    private long id;


    private Cursor c;
    private AdaptadorPuntosInteres.OnItemClickListener escucha;
    private View.OnLongClickListener onLongClickListener; // escuchador long
    private Context contexto;

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //obtenemos el identificador del objeto a mover
        int id = (int)obtenerId(fromPosition);
        //obtenemos el valor del parametro position de la BD
        int posIn = obtenerPosicion(fromPosition);
        int posFin = obtenerPosicion(toPosition);
        //inicializo la base de datos
        ConsultaBD.inicializaBD(contexto);
        //obtengo el route_id
        int route_id = ConsultaBD.getRouteId(id);
        //Realizo el swap en la base de datos
        ConsultaBD.swapPosition(id,posFin,route_id);
        //vuelvo a cargar el cursor
        c = ConsultaBD.listadoPOIItinerario(route_id);

        //Realiza la animacion
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        //obtengo el id del punto a borrar
        int id = (int)obtenerId(position);
        //inicializo la base de datos
        ConsultaBD.inicializaBD(contexto);
        //obtengo el usuario
        int route = ConsultaBD.getRouteId(id);
        //elimino la ruta
        ConsultaBD.deletePoiRoute(id);
        //vuelvo a cargar el cursor con los itinerarios
        c = ConsultaBD.listadoPOIItinerario(route);

        notifyItemRemoved(position);
            }

    //lo utilizaremos desde la actividad
    interface OnItemClickListener {
        public void onClick(AdaptadorPuntosInteres.ViewHolder holder, long id);
    }

    // Constructor
    public AdaptadorPuntosInteres(Context contexto, Cursor c, AdaptadorPuntosInteres.OnItemClickListener escucha) {
        this.c = c;
        this.escucha = escucha;
        this.contexto = contexto;
    }

    //ViewHolder con los elementos a modificar
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
    public AdaptadorPuntosInteres.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //inflamos la vista
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_itinerario, parent, false); //Cambiar elemnto lista por el nombre del layout del elemento del reciclerview

        vista.setOnLongClickListener(onLongClickListener);//aplicamos escuchador long a cada vista
        return new AdaptadorPuntosInteres.ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(AdaptadorPuntosInteres.ViewHolder holder, int position) {
        //siguiente elemento del cursor
        c.moveToPosition(position);

        //Modifico los elementos de la vista
        holder.titulo.setText(c.getString(c.getColumnIndex("title")));
        int tele = c.getInt(c.getColumnIndex("visto"));
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

    private int obtenerPosicion(int posicion) {
        if (c != null) {
            if (c.moveToPosition(posicion)) {
                return c.getInt(c.getColumnIndex("position")); //se puede especipicar el id, por ejemplo route_id, pero _id es general y funcionara siempre
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
        id = obtenerId(position);
        return id;
    }
}
