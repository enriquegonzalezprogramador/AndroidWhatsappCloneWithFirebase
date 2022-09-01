package com.juniorenriquegonzalez.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.juniorenriquegonzalez.whatsapp.R;
import com.juniorenriquegonzalez.whatsapp.config.ConfiguracionFirebase;
import com.juniorenriquegonzalez.whatsapp.model.Usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText campoEmail, campoPassword;
    private FirebaseAuth autenticacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autenticacion = ConfiguracionFirebase.getFirebaseAutenticacion();

        campoEmail = findViewById(R.id.editRegistroEmail);
        campoPassword = findViewById(R.id.editRegistroPassword);
    }

    public void logarUsuario(Usuario usuario) {

        autenticacion.signInWithEmailAndPassword(usuario.getEmail(), usuario.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if ( task.isSuccessful()) {

                    abrirPantallaPrincipal();

                }else {

                    String exepcion = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e) {
                        exepcion = "Este usuario no esta registrado";
                    }catch (FirebaseAuthInvalidCredentialsException e) {
                        exepcion = "Email o contrasena no estan cadastrados";
                    }catch (Exception e) {
                        exepcion = "Error durante logeo:" + e.getMessage();
                        e.printStackTrace();
                    }

                    exibirMensaje(exepcion);
                }
            }
        });

    }

    public void validarAutenticacionusuario(View view) {

        String email = campoEmail.getText().toString();
        String password = campoPassword.getText().toString();

        if (!email.isEmpty()) {

            if (!password.isEmpty()) {

                Usuario usuario = new Usuario();

                usuario.setEmail(email);
                usuario.setPassword(password);

                logarUsuario(usuario);

            } else {
                exibirMensaje("Ingrese una contrasena...");
            }

        } else {
            exibirMensaje("Ingrese un email...");
        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioActual = autenticacion.getCurrentUser();

        if ( usuarioActual != null ) {
            abrirPantallaPrincipal();
        }
    }

    public void abrirRegistro(View view) {

        Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
        startActivity(intent);
    }

    public void abrirPantallaPrincipal() {

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void exibirMensaje( String mensaje ) {

        Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();

    }
}