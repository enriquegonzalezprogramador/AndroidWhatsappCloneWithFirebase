package com.juniorenriquegonzalez.whatsapp.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.juniorenriquegonzalez.whatsapp.config.ConfiguracionFirebase;
import com.juniorenriquegonzalez.whatsapp.model.Usuario;

public class UsuarioFirebase {

    public static String getIdentificadorUsuario() {

        FirebaseAuth usuario = ConfiguracionFirebase.getFirebaseAutenticacion();
        String email = usuario.getCurrentUser().getEmail();
        String identificadorUsuario = Base64Custom.codificarBase64(email);

        return identificadorUsuario;

    }

    public static FirebaseUser getUsuarioAtual() {
        FirebaseAuth usuario = ConfiguracionFirebase.getFirebaseAutenticacion();
        return usuario.getCurrentUser();
    }

    public static boolean actualizarFotoUsuario(Uri url) {

        try {

            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url)
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Log.d("Perfil", "Error al actualizar foto");
                    }
                }
            });
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    public static boolean actualizarNombreUsuario(String nombre) {

        try {

            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nombre)
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Log.d("Perfil", "Error al actualizar nombre del perfil");
                    }
                }
            });
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static Usuario getDatosUsuarioLogado() {

        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setEmail(firebaseUser.getEmail());
       usuario.setNombre(firebaseUser.getDisplayName());

       if ( firebaseUser.getPhotoUrl() == null ) {

           usuario.setFoto("");

       }else {
           usuario.setFoto(firebaseUser.getPhotoUrl().toString());
       }

       return usuario;
    }
}
