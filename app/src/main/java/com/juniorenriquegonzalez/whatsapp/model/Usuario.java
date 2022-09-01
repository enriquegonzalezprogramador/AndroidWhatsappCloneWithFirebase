package com.juniorenriquegonzalez.whatsapp.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.juniorenriquegonzalez.whatsapp.config.ConfiguracionFirebase;
import com.juniorenriquegonzalez.whatsapp.helper.UsuarioFirebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    private String id;
    private String nombre;
    private String email;
    private String password;
    private String foto;


    public Usuario() {
    }

    public void salvarUsuario() {

        DatabaseReference firebaseRef = ConfiguracionFirebase.getFirebaseDatabase();
        DatabaseReference usuario = firebaseRef.child("usuarios")
                                                .child( getId());

        usuario.setValue( this );

    }

    public void actualizar() {

        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference database = ConfiguracionFirebase.getFirebaseDatabase();

        DatabaseReference usuarioRef = database.child("usuarios")
                                                .child( identificadorUsuario );

        Map<String,Object> valoresUsuario = convertirParaMap();

        usuarioRef.updateChildren(valoresUsuario);

    }

    @Exclude
    public Map<String, Object> convertirParaMap() {

        HashMap<String, Object> usuarioMap = new HashMap<>();

            usuarioMap.put("email", getEmail());
            usuarioMap.put("nombre", getNombre());
            usuarioMap.put("foto", getFoto());

        return usuarioMap;

    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
