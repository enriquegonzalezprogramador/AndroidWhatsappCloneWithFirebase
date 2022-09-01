package com.juniorenriquegonzalez.whatsapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.juniorenriquegonzalez.whatsapp.R;
import com.juniorenriquegonzalez.whatsapp.activity.ChatActivity;
import com.juniorenriquegonzalez.whatsapp.activity.GrupoActivity;
import com.juniorenriquegonzalez.whatsapp.adapter.ContactosAdapter;
import com.juniorenriquegonzalez.whatsapp.config.ConfiguracionFirebase;
import com.juniorenriquegonzalez.whatsapp.helper.RecyclerItemClickListener;
import com.juniorenriquegonzalez.whatsapp.helper.UsuarioFirebase;
import com.juniorenriquegonzalez.whatsapp.model.Usuario;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactosFragment extends Fragment {

    private RecyclerView recyclerViewListaContactos;
    private ContactosAdapter adapter;
    private ArrayList<Usuario> listaContactos = new ArrayList<>();
    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerContacto;
    private FirebaseUser usuarioActual;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContactosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactosFragment newInstance(String param1, String param2) {
        ContactosFragment fragment = new ContactosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contactos, container, false);

        recyclerViewListaContactos = view.findViewById(R.id.recyclerViewListaContactos);

        usuarioRef = ConfiguracionFirebase.getFirebaseDatabase().child("usuarios");

        usuarioActual = UsuarioFirebase.getUsuarioAtual();

        //configurar adpater

        adapter = new ContactosAdapter(listaContactos, getActivity());

        //configurar recyclerView

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewListaContactos.setLayoutManager(layoutManager);
        recyclerViewListaContactos.setHasFixedSize(true);
        recyclerViewListaContactos.setAdapter( adapter );

        //Configura evento de click no recyclerView

        recyclerViewListaContactos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(), recyclerViewListaContactos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Usuario usuarioSeleccionado = listaContactos.get(position);
                                boolean cabecalho = usuarioSeleccionado.getEmail().isEmpty();

                                if (cabecalho) {

                                    Intent i = new Intent(getActivity(), GrupoActivity.class);
                                    startActivity(i);

                                }else {

                                    Intent i = new Intent(getActivity(), ChatActivity.class);
                                    i.putExtra("chatContacto", usuarioSeleccionado);
                                    startActivity(i);

                                }



                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );

            Usuario itemGrupo = new Usuario();
            itemGrupo.setNombre("Novo grupo");
            itemGrupo.setEmail("");

            listaContactos.add(itemGrupo);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContactos();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(valueEventListenerContacto);

    }

    public void recuperarContactos() {

       valueEventListenerContacto =  usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot datos: snapshot.getChildren()) {



                    Usuario usuario = datos.getValue(Usuario.class);

                    String emailUsuarioActual = usuarioActual.getEmail();

                    if( !emailUsuarioActual.equals(usuario.getEmail())) {

                        listaContactos.add(usuario);

                    }


                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}