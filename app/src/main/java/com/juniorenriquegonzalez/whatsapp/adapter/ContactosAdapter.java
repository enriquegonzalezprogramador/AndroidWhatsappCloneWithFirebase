package com.juniorenriquegonzalez.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.juniorenriquegonzalez.whatsapp.R;
import com.juniorenriquegonzalez.whatsapp.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactosAdapter extends RecyclerView.Adapter<ContactosAdapter.MyViewHolder> {

    private List<Usuario> contactos;
    private Context context;

    public ContactosAdapter(List<Usuario> listaContactos, Context c) {

        this.contactos = listaContactos;
        this.context = c;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contactos,parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Usuario usuario = contactos.get(position);

        boolean cabecalho = usuario.getEmail().isEmpty();

        holder.nombre.setText(usuario.getNombre());
        holder.email.setText(usuario.getEmail());

        if (usuario.getFoto() != null) {

            Uri uri = Uri.parse(usuario.getFoto());

            Glide.with(context).load(uri).into(holder.foto);
        }else {
            if (cabecalho) {
                holder.foto.setImageResource(R.drawable.icone_grupo);
                holder.email.setVisibility(View.GONE);
            } else {
                holder.foto.setImageResource(R.drawable.padrao);
            }

        }

    }

    @Override
    public int getItemCount() {
        return contactos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView foto;
        TextView nombre, email;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageViewFotoContacto);
            nombre = itemView.findViewById(R.id.textNombreContacto);
            email = itemView.findViewById(R.id.textEmailContacto);
        }
    }
}
