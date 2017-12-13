package org.masterandroid.wander;

/**
 * Created by Ivan on 13/12/2017.
 */

public class iconos {

    public static int iconoCategoria(int categoria){
        int retorno = 0;
        switch (categoria){
            case 1:

            default:
                retorno = R.drawable.tienda;
                break;
        }
        return retorno;
    }


}
