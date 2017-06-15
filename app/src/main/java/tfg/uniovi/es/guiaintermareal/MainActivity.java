package tfg.uniovi.es.guiaintermareal;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import tfg.uniovi.es.guiaintermareal.adapter.SpecieListAdapter;
import tfg.uniovi.es.guiaintermareal.model.Specie;
import tfg.uniovi.es.guiaintermareal.ui.AboutActivity;
import tfg.uniovi.es.guiaintermareal.ui.RuntimePermission;
import tfg.uniovi.es.guiaintermareal.ui.SearchActivity;

import static tfg.uniovi.es.guiaintermareal.ui.CategoryActivity.networkConnected;

public class MainActivity extends RuntimePermission{

    //**************************************************************************************************
    //                                      VAR DECLARATION
    //**************************************************************************************************
    public static String mCategoryTitle = "Algas y Liquenes";
    public static String mCategoryRef = "Categorias/";

    private static final int REQUEST_PERMISSION = 10;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    public static RecyclerView mSpecieList;
    public static FirebaseRecyclerAdapter<Specie, SpecieListAdapter.SpecieViewHolder> firebaseRecyclerAdapter;

    View view_Group;
    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    //Guardamos los arrays con las etiquetas de categorias/subcategorias para trabajar sobre ellas
    public static List<String> fbCategories;
    public static HashMap<String, List<String>> fbSubcategories;
    public static HashMap<String, List<String>> fbChildren;

    private ProgressDialog mProgressDialog;
    public StorageReference mStorage;
    Uri picUri;

    /* Variables de acceso a Firebase */
    FirebaseDatabase database;
    DatabaseReference myRef, categoryRef;

    //**************************************************************************************************
    //                                   ACTIVITY FUNCTIONS
    //**************************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mCategoryTitle);
        setSupportActionBar(toolbar);

        mStorage = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);

        requestAppPermissions(new String[]{
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                R.string.msg,REQUEST_PERMISSION);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);
        expandableList.setDivider(null);
        expandableList.setGroupIndicator(null);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        // Send a Query to the database
        database = FirebaseDatabase.getInstance();
        categoryRef = database.getReference().child("Categorias");
        myRef = categoryRef.child(mCategoryTitle);
        categoryRef.keepSynced(true);

        //Obtenemos los labels y los guardamos en los arrays declarados
        /*fbCategories = getCategories();
        fbSubcategories = getSubcategories();
        fbChildren = getChildren();*/

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
                if (!expandableListView.isGroupExpanded(groupPosition)) {
                    //Expandido
                    loadCategoryData(String.valueOf(expandableListView.getItemAtPosition(groupPosition)));
                }
                return false;
            }
        });

        //Recycler View
        mSpecieList = (RecyclerView) findViewById(R.id.specie_list);
        mSpecieList.setHasFixedSize(true);
        mSpecieList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setRecyclerAdapter();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this).setIcon(R.mipmap.ic_info_outline).setTitle(R.string.exit_title)
                    .setMessage(R.string.exit_message)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }).setNegativeButton("Cancelar", null).show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expandableList.setIndicatorBounds(expandableList.getRight()- 80, expandableList.getWidth());
        } else {
            expandableList.setIndicatorBoundsRelative(expandableList.getRight()- 80, expandableList.getWidth());
        }
    }

    //**************************************************************************************************
    //                                DRAWER NAVIGATION & CONTENT
    //**************************************************************************************************
    /*private void prepareDrawerListData(){
        for(int pos = 0; pos < fbCategories.size(); pos++){
            listDataHeader.add(fbCategories.get(pos));
            List<String> heading = new ArrayList<>();

            for(int posSub = 0; posSub < fbSubcategories.size(); posSub++){
                heading.add(fbSubcategories.get(posSub).toString());
            }


            listDataChild.put(listDataHeader.get(pos), heading);// Header, Child data
            pos++;

            expandableList.setAdapter(mMenuAdapter);
        }
    }*/
    private void prepareDrawerListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        categoryRef.addValueEventListener(  new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int pos = 0;
                listDataHeader.clear();
                listDataChild.clear();
                for(DataSnapshot dsp : dataSnapshot.getChildren()){
                    listDataHeader.add(dsp.getKey());
                    List<String> heading = new ArrayList<>();
                    if (dsp.child("subcategory").getValue() == null) {
                        for (DataSnapshot d : dsp.getChildren()) {
                            String key = (String) d.getKey();
                            if (!key.equals("subcategory")) {
                                heading.add(d.getKey());
                            }
                        }
                    }
                    //if (heading.size() > 0) {
                    listDataChild.put(listDataHeader.get(pos), heading);// Header, Child data
                    //}
                    pos++;
                    expandableList.setAdapter(mMenuAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //**************************************************************************************************
    //                                         ACTIONBAR MENU
    //**************************************************************************************************

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                //LLAMADA A METODO DE LA CLASE SEARCHACTIVITY
                Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
                searchIntent.putExtra("query", query);
                startActivity(searchIntent);
                System.out.println("LA QUERY ES: " + query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;
        switch(item.getItemId()){
            case R.id.action_identify:
                intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File file=getOutputMediaFile(1);
                picUri = Uri.fromFile(file); // create
                intent.putExtra(MediaStore.EXTRA_OUTPUT,picUri); // set the image file
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                break;

            case R.id.action_about:
                intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    //**************************************************************************************************
    //                                   UPLOAD IMAGES TO DB
    //**************************************************************************************************
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (networkConnected(getApplicationContext())) {
            if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                mProgressDialog.setMessage("Subiendo archivo...");
                mProgressDialog.show();
                Uri uri;
                //La imagen se obtiene de la camara
                uri = picUri;
                StorageReference filepath = mStorage.child("Identificame").child(uri.getLastPathSegment());
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(MainActivity.this, "Imagen subida con exito!", Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Ha habido un fallo al subir la imagen!!", Toast.LENGTH_LONG).show();
                    }
                });
            }

        }else{
            mProgressDialog.dismiss();
            Toast.makeText(MainActivity.this, "Es necesario tener conexion a Internet para subir la foto!!", Toast.LENGTH_LONG).show();
        }
    }

    /** Create a File for saving an image */
    private  File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "GuiaIntermareal");

        /**Create the storage directory if it does not exist*/
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        /**Create a media file name*/
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile;
        if (type == 1){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".png");
        } else {
            return null;
        }

        return mediaFile;
    }

    //**************************************************************************************************
    //                                  RECYCLERVIEW FOR DATA
    //**************************************************************************************************
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
        setCategoryRef("Categorias/" + mCategoryTitle);
        myRef = database.getReference(getCategoryRef());
        /*myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()){
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
        });*/

        setToolbarTitle();
        setRecyclerAdapter();
    }

    //**************************************************************************************************
    //                                       AUX FUNCTIONS
    //**************************************************************************************************
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

    //**************************************************************************************************
    //                                        PERMISSIONS
    //**************************************************************************************************
    public void onPermissionsGranted(int requestCode) {
        //Do anything when permisson granted
        Toast.makeText(getApplicationContext(), "Permisos condedidos!", Toast.LENGTH_LONG).show();
    }

    //**************************************************************************************************
    //                                  GET FIREBASE DATA LABELS
    //**************************************************************************************************

    public List<String> getCategories(){
        fbCategories = new ArrayList<>();
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp : dataSnapshot.getChildren()) {
                    fbCategories.add(dsp.getKey());
                }
                //printCategories();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return fbCategories;
    }

    public HashMap<String, List<String>> getSubcategories(){
        fbSubcategories = new HashMap<>();

        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            List<String> listSubcategories = new ArrayList<>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    for (DataSnapshot dspSub : dsp.getChildren()) {
                        if (dsp.child("subcategory").getValue() == null) {
                            //ES SUBCATEGORIA
                            listSubcategories.add(dspSub.getKey());
                        }
                    }

                if (dsp.child("subcategory").getValue() == null) {
                    fbSubcategories.put(dsp.getKey(), listSubcategories);
                }
                listSubcategories = new ArrayList<>();
                }
                printSubcategories();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return fbSubcategories;
    }

    public HashMap<String, List<String>> getChildren(){
        fbChildren = new HashMap<>();

        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            List<String> listChildren = new ArrayList<>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    for (DataSnapshot dspSub : dsp.getChildren()) {
                        String spKey = (String) dspSub.getKey();
                        if (dsp.child("subcategory").getValue() != null) {
                            //NO ES SUBCATEGORIA
                            if(!spKey.equals("subcategory")) {
                                listChildren.add(spKey);
                            }
                        }else{
                            //ES SUBCATEGORIA
                            for(DataSnapshot dspSpecie : dspSub.getChildren()){
                                spKey = (String) dspSpecie.getKey();
                                if (spKey.equals("image") || spKey.equals("subcategory") || spKey.equals("title")) {

                                }else{
                                    listChildren.add(spKey);
                                }
                            }
                            fbChildren.put(dspSub.getKey(), listChildren);
                            listChildren = new ArrayList<>();
                        }
                    }

                    if (dsp.child("subcategory").getValue() != null) {
                        fbChildren.put(dsp.getKey(), listChildren);
                    }
                    listChildren = new ArrayList<>();
                }
                //printChildren();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return fbChildren;
    }

    public void printCategories(){
        for(int pos=0; pos < fbCategories.size(); pos++){
            System.out.println("********** FBCategories: " + fbCategories.get(pos));
        }
    }

    public void printSubcategories(){
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");

        Iterator it = fbSubcategories.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry e = (Map.Entry) it.next();
            System.out.println("***** FBSubcategories: " + e.getKey() + " " + e.getValue());
        }

    }

    public void printChildren(){
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");

        Iterator it = fbChildren.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry e = (Map.Entry) it.next();
            System.out.println("***** FBChildren: " + e.getKey() + " " + e.getValue());
        }
    }

}

