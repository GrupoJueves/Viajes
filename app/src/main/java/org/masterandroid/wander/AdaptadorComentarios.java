package org.masterandroid.wander;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by rodriii on 11/12/17.
 */

public class AdaptadorComentarios extends RecyclerView.Adapter<AdaptadorComentarios.ViewHolder> {

    private LayoutInflater inflador;//Crea Layouts a partir del XML
    private Context contexto;

    // Constructor
    public AdaptadorComentarios(Context contexto) {
        this.contexto = contexto;
    }

    //ViewHolder con los elementos a modificar
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView comentario;
        RatingBar ratingBar;

        public ViewHolder(View vista) {
            super(vista);
            //referencio los elemtos a modificar
            ratingBar = vista.findViewById(R.id.recyclerRatingBar);
            comentario = vista.findViewById(R.id.recyclerComentario);
        }
    }

    //Creamos el viewHolder con las vistas de los elementos sin personalizar
    @Override
    public AdaptadorComentarios.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //inflamos la vista
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rating, parent, false); //Cambiar elemnto lista por el nombre del layout del elemento del reciclerview

        return new AdaptadorComentarios.ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(AdaptadorComentarios.ViewHolder holder, int position) {
        holder.ratingBar.setNumStars(5);
        holder.ratingBar.setRating(3);
        holder.comentario.setText("Comentario1");
        //ConsultaBD
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
