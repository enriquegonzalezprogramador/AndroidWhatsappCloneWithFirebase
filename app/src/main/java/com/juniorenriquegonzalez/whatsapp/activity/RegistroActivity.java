package com.juniorenriquegonzalez.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.juniorenriquegonzalez.whatsapp.R;
import com.juniorenriquegonzalez.whatsapp.config.ConfiguracionFirebase;
import com.juniorenriquegonzalez.whatsapp.helper.Base64Custom;
import com.juniorenriquegonzalez.whatsapp.helper.UsuarioFirebase;
import com.juniorenriquegonzalez.whatsapp.model.Usuario;

public class RegistroActivity extends AppCompatActivity {

    private TextInputEditText campoNombre, campoEmail, campoPassword;

    private FirebaseAuth autenticacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        campoNombre = findViewById((R.id.editRegistroNombre));
        campoEmail = findViewById(R.id.editRegistroEmail);
        campoPassword = findViewById(R.id.editRegistroPassword);

    }

    public void validarRegistroUsuario(View view) {

        String textoNombre = campoNombre.getText().toString();
        String textoEmail = campoEmail.getText().toString();
        String textoPassword = campoPassword.getText().toString();

        if( !textoNombre.isEmpty() ) {

            if( !textoEmail.isEmpty() ) {

                if( !textoPassword.isEmpty() ) {

                    Usuario usuario = new Usuario();
                    usuario.setNombre(textoNombre);
                    usuario.setEmail(textoEmail);
                    usuario.setPassword(textoPassword);

                    registrarUsuario(usuario);

                }else {
                    exibirMensaje("Ingrese una contrasena..");
                }

            }else {
                exibirMensaje("Ingrese un email..");
            }

        }else {
            exibirMensaje("Ingrese un Nombre..");
        }

    }

    public void registrarUsuario(Usuario usuario) {

        autenticacion = ConfiguracionFirebase.getFirebaseAutenticacion();
        autenticacion.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getPassword()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                    if ( task.isSuccessful() ) {

                            exibirMensaje("Usuario creado Exitosamente");

                        UsuarioFirebase.actualizarNombreUsuario(usuario.getNombre());

                            finish();

                            try{

                                String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                                usuario.setId(identificadorUsuario);

                                usuario.salvarUsuario();

                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                    } else {

                        String exepcion = "";
                        try{
                            throw task.getException();
                        }catch (FirebaseAuthWeakPasswordException e) {
                            exepcion = "Digite una contrasena mas fuerte";
                        }catch (FirebaseAuthInvalidCredentialsException e) {
                            exepcion = "Por favor, digite un email valido";
                        }catch (FirebaseAuthUserCollisionException e) {
                            exepcion = "Esta cuenta ya fue registrada";
                        }catch (Exception e) {
                            exepcion = "Error al registrar usuario" + e.getMessage();
                            e.printStackTrace();
                        }

                        exibirMensaje(exepcion);

                    }
            }
        });

    }

    public void exibirMensaje(String mensaje) {

        Toast.makeText(RegistroActivity.this, mensaje, Toast.LENGTH_SHORT).show();
    }
}