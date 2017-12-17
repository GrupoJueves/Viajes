package org.masterandroid.wander;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
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
        com.makeramen.roundedimageview.RoundedImageView imagen;
        //ImageView foto;

        public ViewHolder(View vista) {
            super(vista);
            //referencio los elemtos a modificar
           usuario = (TextView) vista.findViewById(R.id.name_user);
           fecha = vista.findViewById(R.id.date);
           comentario = vista.findViewById(R.id.recyclerComentario);
           valoracion = vista.findViewById(R.id.recyclerRatingBar);
           imagen = vista.findViewById(R.id.imagen_usuario);

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
        //distinguir si tiene username
        String username = ""+c.getString(c.getColumnIndex("username"));
        if(!username.equals("") && !username.equals("null")){
            //Log.e("username","'"+username+"'");
            holder.usuario.setText(username);
        }else{
            holder.usuario.setText(c.getString(c.getColumnIndex("name"))+" "+c.getString(c.getColumnIndex("surname")));
        }



       holder.fecha.setText(DateFormat.getDateInstance().format(new Date(c.getLong(c.getColumnIndex("date")))));
        holder.comentario.setText(c.getString(c.getColumnIndex("comment")));
        holder.valoracion.setRating((float)(0+c.getInt(c.getColumnIndex("rating"))));

        String url = ""+c.getString(c.getColumnIndex("photo"));
        if(!url.equals("") && url != null){

            //ponerFoto(holder.imagen,url);
            Uri uri2 = Uri.parse(url);
            holder.imagen.setImageURI(uri2);

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



    protected void ponerFoto(ImageView imageView, String uri) {
        if (uri != null) {
            imageView.setImageBitmap(reduceBitmap(contexto, uri, 1024, 1024));
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




