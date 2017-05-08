package tfg.uniovi.es.guiaintermareal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import tfg.uniovi.es.guiaintermareal.adapter.SpecieListAdapter;
import tfg.uniovi.es.guiaintermareal.model.Specie;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /* Var declaration */
    public static String mCategoryTitle = "Algas y Liquenes";
    public static String mCategoryRef = "Categorias/Especies/" + mCategoryTitle;
    public RecyclerView mSpecieList;

    /* Variables de acceso a Firebase */
    FirebaseDatabase database;
    DatabaseReference myRef;

    public static FirebaseRecyclerAdapter<Specie, SpecieListAdapter.SpecieViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mCategoryTitle);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Recycler View
        mSpecieList = (RecyclerView) findViewById(R.id.specie_list);
        mSpecieList.setHasFixedSize(true);
        mSpecieList.setLayoutManager(new LinearLayoutManager(this));

        // Send a Query to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(getCategoryRef());

    }

    @Override
    protected void onStart() {
        super.onStart();
         firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Specie, SpecieListAdapter.SpecieViewHolder>(Specie.class, R.layout.design_row, SpecieListAdapter.SpecieViewHolder.class, myRef) {

                    @Override
                    protected void populateViewHolder(SpecieListAdapter.SpecieViewHolder viewHolder, Specie model, int position) {
                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setImage(getApplicationContext(), model.getImage());
                    }
                };

        mSpecieList.getRecycledViewPool().clear();
        mSpecieList.stopScroll();
        mSpecieList.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public void onBackPressed() {
      DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
      if (drawer.isDrawerOpen(GravityCompat.START)) {
          drawer.closeDrawer(GravityCompat.START);
      } else {
          super.onBackPressed();
      }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

       /* Navigation menu for species */
    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.algas_y_liquenes) {
            item.setChecked(true);
            mCategoryTitle = "Algas y Liquenes";
            setCategoryRef("Categorias/Especies/" + mCategoryTitle);
            myRef = database.getReference(getCategoryRef());
            setToolbarTitle();
            onStart();
        } else if (id == R.id.esponjas_anemonas_corales) {
            item.setChecked(true);
            mCategoryTitle = "Esponjas, Anemonas y Corales";
            setCategoryRef("Categorias/Especies/" + mCategoryTitle);
            myRef = database.getReference(getCategoryRef());
            setToolbarTitle();
            onStart();
        } else if (id == R.id.anelidos) {
            item.setChecked(true);
            mCategoryTitle = "Anelidos";
            setCategoryRef("Categorias/Especies/" + mCategoryTitle);
            myRef = database.getReference(getCategoryRef());
            setToolbarTitle();
            onStart();
        } else if (id == R.id.moluscos) {
            item.setChecked(true);
            mCategoryTitle = "Moluscos";
            setCategoryRef("Categorias/Especies/" + mCategoryTitle);
            myRef = database.getReference(getCategoryRef());
            setToolbarTitle();
            onStart();
        } else if (id == R.id.crustaceos) {
            item.setChecked(true);
            mCategoryTitle = "Crustaceos";
            setCategoryRef("Categorias/Especies/" + mCategoryTitle);
            myRef = database.getReference(getCategoryRef());
            setToolbarTitle();
            onStart();
        } else if (id == R.id.equinodermos) {
            item.setChecked(true);
            mCategoryTitle = "Equinodermos";
            setCategoryRef("Categorias/Especies/" + mCategoryTitle);
            myRef = database.getReference(getCategoryRef());
            setToolbarTitle();
            onStart();
        } else if (id == R.id.peces) {
            item.setChecked(true);
            mCategoryTitle = "Peces";
            setCategoryRef("Categorias/Especies/" + mCategoryTitle);
            myRef = database.getReference(getCategoryRef());
            setToolbarTitle();
            onStart();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* Sets category title on Toolbar */
    public void setToolbarTitle(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mCategoryTitle);
    }

    /* Var access methods */
    public String getCategoryRef(){
        return mCategoryRef;
    }
    public void setCategoryRef(String ref){
        mCategoryRef = ref;
    }

}



