package com.juniorenriquegonzalez.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.juniorenriquegonzalez.whatsapp.R;
import com.juniorenriquegonzalez.whatsapp.adapter.MensajesAdapter;
import com.juniorenriquegonzalez.whatsapp.config.ConfiguracionFirebase;
import com.juniorenriquegonzalez.whatsapp.helper.Base64Custom;
import com.juniorenriquegonzalez.whatsapp.helper.Permisos;
import com.juniorenriquegonzalez.whatsapp.helper.UsuarioFirebase;
import com.juniorenriquegonzalez.whatsapp.model.Conversa;
import com.juniorenriquegonzalez.whatsapp.model.Mensaje;
import com.juniorenriquegonzalez.whatsapp.model.Usuario;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView textViewNombre, editMensage;
    private CircleImageView circleImageViewFoto;
    private Usuario usuarioDestinatario;
    private DatabaseReference database;
    private DatabaseReference mensajesRef;
    private ChildEventListener childEventListenerMensajes;

    private StorageReference storage;

    private ImageView imageCamera;

    private String idUsuarioRemitente;
    private String idUsuarioDestinatario;

    private RecyclerView recyclerMensajes;
    private MensajesAdapter adapter;
    private List<Mensaje> mensajes = new ArrayList<>();

    private static final int SELECCION_CAMARA = 100;

    public String[] permisiosNecesarios = new String[] {

            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar t = findViewById(R.id.toolbarChat);
        t.setTitle("");

        //Configuraciones Iniciales;

        textViewNombre = findViewById(R.id.textViewNombreChat);
        circleImageViewFoto = findViewById(R.id.circleImageFotoChat);
        editMensage = findViewById(R.id.editTextMensaje);

        recyclerMensajes = findViewById(R.id.recyclerMensajes);

        imageCamera = findViewById(R.id.buttonPhotoChat);

        // Recuperar datos del usuario remitente

        idUsuarioRemitente = UsuarioFirebase.getIdentificadorUsuario();


        //Recuperar datos del usuario destinatario

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            usuarioDestinatario = (Usuario) bundle.getSerializable("chatContacto");
            textViewNombre.setText(usuarioDestinatario.getNombre());

            String foto = usuarioDestinatario.getFoto();

            if(foto != null ) {

                Uri url = Uri.parse(usuarioDestinatario.getFoto());

                Glide.with(ChatActivity.this)
                            .load(url)
                            .into(circleImageViewFoto);
            }else {
                circleImageViewFoto.setImageResource(R.drawable.padrao);
            }

            //recuperar datos usuario destinatario

            idUsuarioDestinatario = Base64Custom.codificarBase64(usuarioDestinatario.getEmail());
        }

        //configuracion adapter

        adapter = new MensajesAdapter(mensajes, this);

        //Configuracion del recycler view de mensajes

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager((getApplicationContext()));

        recyclerMensajes.setLayoutManager(layoutManager);
        recyclerMensajes.setHasFixedSize(true);
        recyclerMensajes.setAdapter(adapter);

        database = ConfiguracionFirebase.getFirebaseDatabase();
        storage = ConfiguracionFirebase.getFirebaseStorage();
        mensajesRef = database.child("mensajes")
                .child(idUsuarioRemitente)
                .child( idUsuarioDestinatario);

        Permisos.validarPermisos(permisiosNecesarios, ChatActivity.this, 1);

        //Evento de click en la camara

        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (i.resolveActivity(getPackageManager()) != null) {

                    // startActivityForResult(i, SELECCION_CAMARA);
                    startActivityIfNeeded(i, SELECCION_CAMARA);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            Bitmap imagen = null;

            try {

                switch ( requestCode ) {

                    case SELECCION_CAMARA:

                        imagen =(Bitmap) data.getExtras().get("data");

                        break;
                }

                if ( imagen != null) {

                    //RECUEPRAR DATOS BINARIOS DE LA IMAGEN PARA SUBIR A FIREBASE

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    imagen.compress(Bitmap.CompressFormat.JPEG, 70, baos);

                    byte[] datosImagen = baos.toByteArray();

                    //Cira nome da imagen

                    String nomeImagen = UUID.randomUUID().toString();

                    //CONFIGURAR LAS REFERENCIAS DE FIREBASE

                    final StorageReference imagenRef = storage
                            .child("imagens")
                            .child("fotos")
                            .child(idUsuarioRemitente)
                            .child(nomeImagen+ ".jpeg");

                    UploadTask uploadTask = imagenRef.putBytes( datosImagen );

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                                Log.d("ERROR", "Error al hacer carga de imagen");

                                Toast.makeText(ChatActivity.this, "Error al hacer carda de la imagen",
                                        Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                           imagenRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                               @Override
                               public void onComplete(@NonNull Task<Uri> task) {
                                  String downloadUrl = task.getResult().toString();

                                  Mensaje mensaje = new Mensaje();
                                  mensaje.setIdUsuario( idUsuarioRemitente);
                                  mensaje.setMensaje("imagen.jpeg");
                                  mensaje.setImagen(downloadUrl);

                                  //SALVAR IMAGEN PARA EL REMITENTE
                                  salvarMensaje(idUsuarioRemitente, idUsuarioDestinatario, mensaje);

                                  //SALVAR IMAGEN PARA EL DESTINATARIO
                                   salvarMensaje(idUsuarioDestinatario, idUsuarioRemitente , mensaje);

                                   Toast.makeText(ChatActivity.this, "Sucesso ao enviar imagem",
                                           Toast.LENGTH_SHORT).show();

                               }
                           });

                        }
                    });
                }

                }catch(Exception e) {
                    e.printStackTrace();
            }

        }

    }

    public void enviarMensaje(View view) {
        String textoMensaje = editMensage.getText().toString();

        if (!textoMensaje.isEmpty()) {

            Mensaje mensaje = new Mensaje();
            mensaje.setIdUsuario(idUsuarioRemitente);
            mensaje.setMensaje(textoMensaje);

            //Guardar mensaje para el remitente

            salvarMensaje(idUsuarioRemitente, idUsuarioDestinatario, mensaje);

            //Guardar mensaje para el destinatario

            salvarMensaje(idUsuarioDestinatario, idUsuarioRemitente, mensaje);

            //Salvar Conversa

            salvarConversa(mensaje);

        } else {

            Toast.makeText(ChatActivity.this, "Digite un mensaje para enviar", Toast.LENGTH_LONG).show();

        }
    }

    private void salvarConversa(Mensaje msg) {

        Conversa conversaRemitente = new Conversa();
        conversaRemitente.setIdRemitente(idUsuarioRemitente);
        conversaRemitente.setIdDestinatario(idUsuarioDestinatario);
        conversaRemitente.setUltimaMensaje(msg.getMensaje());
        conversaRemitente.setUsuarioExibicion(usuarioDestinatario);

        conversaRemitente.salvar();
    }

    private void salvarMensaje(String idRemitente, String idDestinatario, Mensaje msg) {

        DatabaseReference database = ConfiguracionFirebase.getFirebaseDatabase();
        mensajesRef = database.child("mensajes");

        mensajesRef.child(idRemitente)
                .child(idDestinatario)
                .push()
                .setValue(msg);

        //Limpiar caja de texto mensaje

        editMensage.setText("");

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensaje();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensajesRef.removeEventListener(childEventListenerMensajes);
    }

    private void recuperarMensaje() {

        childEventListenerMensajes = mensajesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Mensaje mensaje = snapshot.getValue(Mensaje.class);
                mensajes.add(mensaje);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}