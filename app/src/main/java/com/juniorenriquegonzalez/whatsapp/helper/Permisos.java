package com.juniorenriquegonzalez.whatsapp.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permisos {

    public static boolean validarPermisos(String[] permisos, Activity activity, int requestCode) {

        if (Build.VERSION.SDK_INT >= 23) {

            List<String> listaPermisos = new ArrayList<>();


            for ( String permiso: permisos) {

              Boolean tienePermiso =  ContextCompat.checkSelfPermission(activity, permiso) == PackageManager.PERMISSION_GRANTED;

              if ( !tienePermiso ) listaPermisos.add(permiso);

            }

            //Verificar si la lsita de permisos esta vacia

            if (listaPermisos.isEmpty()) return true;

            String[] nuevosPermisos = new String[listaPermisos.size()];

            listaPermisos.toArray(nuevosPermisos);

            //Solicar permisos

            ActivityCompat.requestPermissions(activity, nuevosPermisos, requestCode);



        }

        return  true;
    }
}
