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
    public static FirebaseRecyclerAdapter<Specie, SpecieListAdapter.SpecieViewHolder> firebaseRecyclerAdapter;
    public static MenuItem rootItem;

    /* Variables de acceso a Firebase */
    FirebaseDatabase database;
    DatabaseReference myRef;

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

        rootItem = navigationView.getMenu().getItem(0).getSubMenu().getItem(0);
        rootItem.setChecked(true);

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
        setRecyclerAdapter();
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

       /* Navigation menu for species */
    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.algas_y_liquenes) {
            loadCategoryData("Algas y Liquenes", item);
        } else if (id == R.id.esponjas_anemonas_corales) {
            loadCategoryData("Esponjas, Anemonas y Corales", item);
        } else if (id == R.id.anelidos) {
            loadCategoryData("Anelidos", item);
        } else if (id == R.id.moluscos) {
            loadCategoryData("Moluscos", item);
        } else if (id == R.id.crustaceos) {
            loadCategoryData("Crustaceos", item);
        } else if (id == R.id.equinodermos) {
            loadCategoryData("Equinodermos", item);
        } else if (id == R.id.peces) {
            loadCategoryData("Peces", item);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setRecyclerAdapter(){
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

    public void loadCategoryData(String title, MenuItem item){
        if(item.getItemId() != R.id.algas_y_liquenes) {
            rootItem.setChecked(false);
            item.setChecked(true);
        }
        mCategoryTitle = title;
        setCategoryRef("Categorias/Especies/" + mCategoryTitle);
        myRef = database.getReference(getCategoryRef());
        setToolbarTitle();
        setRecyclerAdapter();
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



