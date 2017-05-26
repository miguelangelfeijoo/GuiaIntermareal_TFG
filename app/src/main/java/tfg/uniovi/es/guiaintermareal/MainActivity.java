package tfg.uniovi.es.guiaintermareal;

import android.graphics.Color;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tfg.uniovi.es.guiaintermareal.adapter.SpecieListAdapter;
import tfg.uniovi.es.guiaintermareal.model.Specie;
import tfg.uniovi.es.guiaintermareal.ui.RuntimePermission;

public class MainActivity extends RuntimePermission{

    /* Var declaration */
    public static String mCategoryTitle = "Algas y Liquenes";
    public static String mRootRef = "Categorias/";
    private static final int REQUEST_PERMISSION = 10;

    public RecyclerView mSpecieList;
    public static FirebaseRecyclerAdapter<Specie, SpecieListAdapter.SpecieViewHolder> firebaseRecyclerAdapter;

    View view_Group;
    private DrawerLayout mDrawerLayout;
    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<String> listDataHeader;
    List<Object> listChildValues;
    HashMap<String, List<String>> listDataChild;

    /* Variables de acceso a Firebase */
    FirebaseDatabase database;
    DatabaseReference myRef, rootRef;

    //**************************************************************************************************

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expandableList.setIndicatorBounds(expandableList.getRight()- 80, expandableList.getWidth());
        } else {
            expandableList.setIndicatorBoundsRelative(expandableList.getRight()- 80, expandableList.getWidth());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mCategoryTitle);
        setSupportActionBar(toolbar);

        requestAppPermissions(new String[]{
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                R.string.msg,REQUEST_PERMISSION);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        // Send a Query to the database
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference().child("Categorias");
        myRef = rootRef.child(mCategoryTitle);
        rootRef.keepSynced(true);


        // ***** CARGAMOS LOS DATOS DEL DRAWER ******
        prepareDrawerListData();

        mMenuAdapter = new tfg.uniovi.es.guiaintermareal.adapter.ExpandableListAdapter(this, listDataHeader, listDataChild);
        expandableList.setAdapter(mMenuAdapter);

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView,
                                        View view,
                                        int groupPosition,
                                        int childPosition, long id) {
               Toast.makeText(MainActivity.this,
                        "Header: "+String.valueOf(expandableListView.getItemAtPosition(groupPosition)) +
                                "\nItem: "+ String.valueOf(childPosition), Toast.LENGTH_SHORT).show();

                view.setSelected(true);
                if (view_Group != null) {
                    view_Group.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                expandableListView.collapseGroup(groupPosition);
                drawer.closeDrawers();
                return false;

            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long id) {
                int count = expandableListView.getCount();
                if (expandableListView.isGroupExpanded(groupPosition)){
                    //Colapsado
                }else {
                    //Expandido
                    for (int c = 0; c < count; c++) {
                        loadCategoryData(String.valueOf(expandableListView.getItemAtPosition(groupPosition)));
                    }
                }
                return false;
            }
        });

        //Recycler View
        mSpecieList = (RecyclerView) findViewById(R.id.specie_list);
        mSpecieList.setHasFixedSize(true);
        mSpecieList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void prepareDrawerListData() {
        listDataHeader = new ArrayList<String>();
        listChildValues = new ArrayList<Object>();
        listDataChild = new HashMap<String, List<String>>();

        rootRef.addValueEventListener(  new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int pos = 0;
                listDataHeader.clear();
                listDataChild.clear();
                for(DataSnapshot dsp : dataSnapshot.getChildren()){
                    listDataHeader.add(dsp.getKey());
                    List<String> heading = new ArrayList<String>();
                    for (DataSnapshot d : dsp.getChildren()) {
                        heading.add(d.getKey());
                    }
                    listDataChild.put(listDataHeader.get(pos), heading);// Header, Child data
                    pos++;
                    expandableList.setAdapter(mMenuAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    public void loadCategoryData(String title){
        mCategoryTitle = title;
        setRootRef("Categorias/" + mCategoryTitle);
        myRef = database.getReference(getRootRef());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()){
                    //Aqui salen Cefalopodos, Bivalvos, Quitones y Caracoles en el dsp
                    if(dsp.child("subcategory").getValue() != null ){
                        //ES SUBCATEGORIA
                    }else{
                        //NO ES SUBCATEGORIA
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setToolbarTitle();
        setRecyclerAdapter();
    }

    /* Sets category title on Toolbar */
    public void setToolbarTitle(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mCategoryTitle);
    }

    /* Var access methods */
    public String getRootRef(){
        return mRootRef;
    }
    public void setRootRef(String ref){
        mRootRef = ref;
    }

    public void onPermissionsGranted(int requestCode) {
        //Do anything when permisson granted
        Toast.makeText(getApplicationContext(), "Permisos condedidos!", Toast.LENGTH_LONG).show();
    }

}



