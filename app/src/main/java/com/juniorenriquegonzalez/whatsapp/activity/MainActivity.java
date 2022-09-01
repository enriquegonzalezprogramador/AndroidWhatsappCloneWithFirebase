package com.juniorenriquegonzalez.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.juniorenriquegonzalez.whatsapp.R;
import com.juniorenriquegonzalez.whatsapp.config.ConfiguracionFirebase;
import com.juniorenriquegonzalez.whatsapp.fragment.ContactosFragment;
import com.juniorenriquegonzalez.whatsapp.fragment.ConversasFragment;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacion;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autenticacion = ConfiguracionFirebase.getFirebaseAutenticacion();



        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);

        //COnfigurar Abas

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                 .add("Conversas", ConversasFragment.class)
                 .add("Contactos", ContactosFragment.class)
                .create()
        );

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter( adapter );

        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);

        viewPagerTab.setViewPager( viewPager );

        //Configuracion sarchView

        searchView = findViewById(R.id.materialSearchPrincipal);

        //Listener para o search view

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

                ConversasFragment fragment = (ConversasFragment) adapter.getPage(0);

                fragment.recargarConversas();
            }
        });

        //Listener para caja de texto de busqueda
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
              //  Log.d("evento", "onQueryTextSubmit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.d("evento", "onQueryTextChange");

                ConversasFragment fragment = (ConversasFragment) adapter.getPage(0);

                if ( newText != null && !newText.isEmpty()) {

                  fragment.buscarConversas(newText.toLowerCase());

                }

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        //Configurar boton de busqueda

        MenuItem item = menu.findItem(R.id.menuBusqueda);

        searchView.setMenuItem(item);



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch ( item.getItemId() ) {

            case R.id.menuSair:

                    deslogarUsuario();
                    finish();

                break;

            case R.id.menuConfiguraciones:

                    abrirConfiguraciones();

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void deslogarUsuario() {

        try {

            autenticacion.signOut();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void abrirConfiguraciones() {

        Intent intent = new Intent(MainActivity.this, ConfiguracionesActivity.class);
        startActivity(intent);
    }
}