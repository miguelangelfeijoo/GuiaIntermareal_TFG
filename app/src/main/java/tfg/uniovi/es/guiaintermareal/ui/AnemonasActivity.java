package tfg.uniovi.es.guiaintermareal.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import tfg.uniovi.es.guiaintermareal.MainActivity;
import tfg.uniovi.es.guiaintermareal.R;

public class AnemonasActivity extends MainActivity {

    private TextView vTitle, vDescription, vEcology;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anemonas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Se ha avistado esta especie. Añadir marcador al mapa.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        String nombre = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String ecology = getIntent().getStringExtra("ecology");
        String imageUrl = getIntent().getStringExtra("image");

        vTitle = (TextView) findViewById(R.id.vTitle);
        vDescription = (TextView) findViewById(R.id.vDescription);
        vEcology = (TextView) findViewById(R.id.vEcology);

        vTitle.setText(nombre);
        vDescription.setText(description);
        vEcology.setText(ecology);
        setImage(getApplicationContext(),imageUrl);

    }

    private void setImage(Context ctx , String image){
        ImageView vImage = (ImageView)findViewById(R.id.vImage);
        Picasso.with(ctx).load(image).into(vImage);
    }

}
