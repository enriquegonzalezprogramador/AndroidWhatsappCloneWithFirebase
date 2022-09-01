package com.juniorenriquegonzalez.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.juniorenriquegonzalez.whatsapp.R;
import com.juniorenriquegonzalez.whatsapp.config.ConfiguracionFirebase;
import com.juniorenriquegonzalez.whatsapp.helper.Permisos;
import com.juniorenriquegonzalez.whatsapp.helper.UsuarioFirebase;
import com.juniorenriquegonzalez.whatsapp.model.Usuario;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracionesActivity extends AppCompatActivity {

    public String[] permisiosNecesarios = new String[] {

            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private ImageButton imageButtonCamera, imageButtonGaleria;

    private static final int SELECCION_CAMARA = 100;
    private static final int SELECCION_GALERIA = 200;
    private CircleImageView circleImageViewPerfil;
    private EditText editPerfilNombre;
    private StorageReference storageReference;
    private String identificadorUsuario;
    private ImageView imageActualizarNombre;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuraciones);

        storageReference = ConfiguracionFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        usuarioLogado = UsuarioFirebase.getDatosUsuarioLogado();

        //Validar permisos

        Permisos.validarPermisos(permisiosNecesarios, this, 1);

        inicializarComponentes();


        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Configuraciones");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();

        Uri url = usuario.getPhotoUrl();

        if (url != null) {

            Glide.with(ConfiguracionesActivity.this)
                    .load(url)
                    .into(circleImageViewPerfil);

        }else {
            circleImageViewPerfil.setImageResource(R.drawable.padrao);
        }

        editPerfilNombre.setText(usuario.getDisplayName());


        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (i.resolveActivity(getPackageManager()) != null) {

                   // startActivityForResult(i, SELECCION_CAMARA);
                    startActivityIfNeeded(i,SELECCION_CAMARA);
                }


            }
        });



        imageButtonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (i.resolveActivity(getPackageManager()) != null) {


                    startActivityIfNeeded(i,SELECCION_GALERIA);
                }
            }
        });

        imageActualizarNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nombre = editPerfilNombre.getText().toString();
                boolean retorno = UsuarioFirebase.actualizarNombreUsuario(nombre);

                if ( retorno ) {

                    usuarioLogado.setNombre(nombre);
                    usuarioLogado.actualizar();

                    exibirMessage("Nombre alterado exitosamente");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK ) {

            Bitmap imagen = null;

            try {

                switch ( requestCode ) {

                    case SELECCION_CAMARA:

                        imagen =(Bitmap) data.getExtras().get("data");

                        break;


                    case SELECCION_GALERIA:

                        Uri localImagenSeleccionada = data.getData();
                        imagen = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagenSeleccionada);

                        break;

                }

                if ( imagen != null) {

                    circleImageViewPerfil.setImageBitmap(imagen);

                    //RECUEPRAR DATOS BINARIOS DE LA IMAGEN PARA SUBIR A FIREBASE

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    imagen.compress(Bitmap.CompressFormat.JPEG, 70, baos);


                    byte[] datosImagen = baos.toByteArray();

                   final StorageReference imagenRef = storageReference
                                .child("imagens")
                            .child("perfil")
                            //.child(identificadorUsuario)
                            .child(identificadorUsuario+ ".jpeg");

                    UploadTask uploadTask = imagenRef.putBytes( datosImagen );

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            exibirMessage("Error al cargar imagen");

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            exibirMessage("Imagen subida exitosamente");

                            imagenRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    Uri url = task.getResult();
                                    actualizaFotoUsuario(url);

                                }
                            });
                        }
                    });

       /*             uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }
                            return imagenRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                Uri downloadUrl = task.getResult();
                                //listaURLFotos.add(downloadUrl.toString());

                                   // anuncio.setFotos(listaURLFotos);
                                    //anuncio.salvarAnuncio();

                                    finish();
                                    exibirMessage("Imagen uploaded!!!");

                            }
                        }
                    });*/
                }

            }catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void actualizaFotoUsuario(Uri url) {

       boolean retorno =  UsuarioFirebase.actualizarFotoUsuario(url);

       if ( retorno ) {
           usuarioLogado.setFoto(url.toString());
           usuarioLogado.actualizar();

           exibirMessage("Foto fue actualizada exitosamente!!!");
       }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for ( int permisoResultado: grantResults) {

                if (permisoResultado == PackageManager.PERMISSION_DENIED) {

                    alertaValidacionPermiso();

                }
        }
    }

    private void alertaValidacionPermiso() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Permisos Negados");
        builder.setMessage("Es necesario aceptar los permisos necesarios para utilizar la APP");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();



    }

    private void exibirMessage(String mensaje) {

        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    public void inicializarComponentes() {

        imageButtonCamera = findViewById(R.id.imageButtonCamara);
        imageButtonGaleria = findViewById(R.id.imageButtonGaleria);
        circleImageViewPerfil = findViewById(R.id.circleImageViewFotoPerfil);
        editPerfilNombre = findViewById(R.id.editPerfilNombre);
        imageActualizarNombre = findViewById(R.id.imageActualizarNombre);
    }
}