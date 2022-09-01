package com.juniorenriquegonzalez.whatsapp.model;

import com.google.firebase.database.DatabaseReference;
import com.juniorenriquegonzalez.whatsapp.config.ConfiguracionFirebase;

public class Conversa {

    private String idRemitente;
    private String idDestinatario;
    private String ultimaMensaje;
    private Usuario usuarioExibicion;

    public Conversa() {

    }

    public void salvar() {
        DatabaseReference database = ConfiguracionFirebase.getFirebaseDatabase();
        DatabaseReference conversaRef = database.child("conversas");

        conversaRef.child(this.getIdRemitente())
                    .child(this.getIdDestinatario())
                    .setValue(this);
    }

    public String getIdRemitente() {
        return idRemitente;
    }

    public void setIdRemitente(String idRemitente) {
        this.idRemitente = idRemitente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMensaje() {
        return ultimaMensaje;
    }

    public void setUltimaMensaje(String ultimaMensaje) {
        this.ultimaMensaje = ultimaMensaje;
    }

    public Usuario getUsuarioExibicion() {
        return usuarioExibicion;
    }

    public void setUsuarioExibicion(Usuario usuarioExibicion) {
        this.usuarioExibicion = usuarioExibicion;
    }
}
