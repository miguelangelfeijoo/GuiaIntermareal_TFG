package cardview.firebase.example.com.firebasecardview;

import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import cardview.firebase.example.com.firebasecardview.model.Specie;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /* Var declaration */
    public String mCategoryTitle = "Algas y Liquenes";
    public String mCategoryRef = "Categorias/Especies/" + mCategoryTitle;
    private RecyclerView mSpecieList;

    /* Firebase variables */
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
        drawer.setDrawerListener(toggle);
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
        FirebaseRecyclerAdapter<Specie, SpecieViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Specie, SpecieViewHolder>(
                        Specie.class,
                        R.layout.design_row,
                        SpecieViewHolder.class,
                        myRef) {

                    @Override
                    protected void populateViewHolder(SpecieViewHolder viewHolder, Specie model, int position) {
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

    //View Holder For Recycler View
    public static class SpecieViewHolder extends RecyclerView.ViewHolder  {
        private final Context context;
        public SpecieViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://es.wikipedia.org/wiki/Alga"));
                Intent browserChooserIntent = Intent.createChooser(browserIntent , "Choose browser of your choice");
                v.getContext().startActivity(browserChooserIntent);*/
                Intent intent = new Intent(context,AlgasActivity.class);
                context.startActivity(intent);
                }
            });
        }

        public void setTitle(String title){
            TextView post_title = (TextView)itemView.findViewById(R.id.titleText);
            post_title.setText(title);
        }
        public void setImage(Context ctx , String image){
            ImageView post_image = (ImageView)itemView.findViewById(R.id.imageViewy);
            // We Need TO pass Context
            Picasso.with(ctx).load(image).into(post_image);
        }
    }

    /* Navigation menu for species */
    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.algas_y_liquenes) {
            mCategoryTitle = "Algas y Liquenes";
            setCategoryRef("Categorias/Especies/" + mCategoryTitle);
            myRef = database.getReference(getCategoryRef());
            setToolbarTitle();
            onStart();
        } else if (id == R.id.esponjas_anemonas_corales) {
            mCategoryTitle = "Esponjas, Anemonas y Corales";
            setCategoryRef("Categorias/Especies/" + mCategoryTitle);
            myRef = database.getReference(getCategoryRef());
            setToolbarTitle();
            onStart();
        } else if (id == R.id.anelidos) {
            mCategoryTitle = "Anelidos";
            setCategoryRef("Categorias/Especies/" + mCategoryTitle);
            myRef = database.getReference(getCategoryRef());
            setToolbarTitle();
            onStart();
        } else if (id == R.id.moluscos) {
            mCategoryTitle = "Moluscos";
            setCategoryRef("Categorias/Especies/" + mCategoryTitle);
            myRef = database.getReference(getCategoryRef());
            setToolbarTitle();
            onStart();
        } else if (id == R.id.crustaceos) {
            mCategoryTitle = "Crustaceos";
            setCategoryRef("Categorias/Especies/" + mCategoryTitle);
            myRef = database.getReference(getCategoryRef());
            setToolbarTitle();
            onStart();
        } else if (id == R.id.equinodermos) {
            mCategoryTitle = "Equinodermos";
            setCategoryRef("Categorias/Especies/" + mCategoryTitle);
            myRef = database.getReference(getCategoryRef());
            setToolbarTitle();
            onStart();
        } else if (id == R.id.peces) {
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



