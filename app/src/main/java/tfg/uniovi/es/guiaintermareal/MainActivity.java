package tfg.uniovi.es.guiaintermareal;

import android.*;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.List;
import java.util.Locale;

import tfg.uniovi.es.guiaintermareal.adapter.SpecieListAdapter;
import tfg.uniovi.es.guiaintermareal.model.Specie;
import tfg.uniovi.es.guiaintermareal.ui.AboutActivity;
import tfg.uniovi.es.guiaintermareal.ui.RuntimePermission;

import static tfg.uniovi.es.guiaintermareal.ui.CategoryActivity.networkConnected;

public class MainActivity extends RuntimePermission{

    //**************************************************************************************************
    //                                      VAR DECLARATION
    //**************************************************************************************************
    public static String mCategoryTitle = "Algas y líquenes";
    public static String mCategoryRef = "Categorias/";

    private static final int REQUEST_PERMISSION = 10;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    public static RecyclerView mSpecieList;
    public static FirebaseRecyclerAdapter<Specie, SpecieListAdapter.SpecieViewHolder> firebaseRecyclerAdapter;
    public static Toolbar toolbar;

    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    private ProgressDialog mProgressDialog;
    public StorageReference mStorage;
    FirebaseAuth mAuth;
    Uri picUri;

    public static String dataType = "Category";

    private DrawerLayout mDrawer;

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mCategoryTitle);
        setSupportActionBar(toolbar);

        //mAuth = FirebaseAuth.getInstance();

        mStorage = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);


        requestAppPermissions(new String[]{
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                R.string.msg,REQUEST_PERMISSION);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);
        expandableList.setDivider(null);
        expandableList.setGroupIndicator(null);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        // Send a Query to the database
        database = FirebaseDatabase.getInstance();
        categoryRef = database.getReference().child("Categorias");
        myRef = categoryRef.child(mCategoryTitle);
        categoryRef.keepSynced(true);
        myRef.keepSynced(true);

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
                loadSubcategoryData(String.valueOf(listDataHeader.get(groupPosition)),mMenuAdapter.getChild(groupPosition, childPosition).toString());
                expandableListView.collapseGroup(groupPosition);
                mDrawer.closeDrawers();
                return false;

            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long id) {
                    loadCategoryData(String.valueOf(listDataHeader.get(groupPosition)));
                    if (listDataChild.get(listDataHeader.get(groupPosition)).isEmpty()){
                        mDrawer.closeDrawers();
                    }
                    expandableListView.collapseGroup(groupPosition);
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

        /*FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            signInAnonymously();
        }*/

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        if (dataType.equals("Subcategory")) {
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                int count;

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    count = (int) dataSnapshot.getChildrenCount();
                    setRecyclerAdapter(count - 2);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            setRecyclerAdapter();
        }

    }
    /*private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
    }*/

    @Override
    public void onBackPressed() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
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
    public void prepareDrawerListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int pos = 0;
                listDataHeader.clear();
                listDataChild.clear();
                for(DataSnapshot dsp : dataSnapshot.getChildren()){
                    listDataHeader.add(dsp.getKey());
                    List<String> heading = new ArrayList<>();
                    for (DataSnapshot d : dsp.getChildren()) {
                        String key = d.getKey();
                        if (! (d.hasChild("subcategory"))) {
                            if (!key.equals("subcategory") && !(key.equals("image")) && !(key.equals("title"))) {
                                heading.add(d.getKey());
                            }
                        }
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

    //**************************************************************************************************
    //                                         ACTIONBAR MENU
    //**************************************************************************************************

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Busqueda por habitat...");
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
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;
        switch(item.getItemId()){
            case R.id.action_identify:
                if((ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                    intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = getOutputMediaFile(1);
                    picUri = Uri.fromFile(file); // create
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri); // set the image file
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }else{
                    Toast.makeText(getApplicationContext(), "Es necesario conceder los permisos para usar el botón!", Toast.LENGTH_SHORT).show();
                }
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
                final StorageReference filepath = mStorage.child("Identificame").child(uri.getLastPathSegment());
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(MainActivity.this, "Imagen subida con exito!", Toast.LENGTH_LONG).show();
                        sendEmail(filepath.getName());
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

    private void sendEmail(String imagen){

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + "intermareal.guia@gmail.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Identificame");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Se ha enviado la imagen " + imagen + " " +
                "para su identificacion");

        try {
            startActivity(Intent.createChooser(emailIntent, "Confirma el envio por email usando..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "No hay clientes de email instalados.", Toast.LENGTH_SHORT).show();
        }

    }

    /** Create a File for saving an image */
    private File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Intermareal");

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
    public void setRecyclerAdapter(int count){
        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Specie, SpecieListAdapter.SpecieViewHolder>(Specie.class, R.layout.design_row, SpecieListAdapter.SpecieViewHolder.class, myRef.limitToFirst(count)) {

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
        dataType = "Category";
        mCategoryTitle = title;
        mCategoryRef = "Categorias/" + mCategoryTitle;
        myRef = database.getReference(mCategoryRef);

        setToolbarTitle();
        setRecyclerAdapter();
    }

    public void loadSubcategoryData(String category, final String subcategory){
        dataType = "Subcategory";
        mCategoryTitle = subcategory;
        mCategoryRef = "Categorias/" + category+ "/" + mCategoryTitle;
        myRef = database.getReference(mCategoryRef);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            int count;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    count = (int) dataSnapshot.getChildrenCount();
                    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                    toolbar.setTitle(subcategory);
                    //Filtrar los resultados de la referencia para no mostrar
                    //los dos ultimos referentes a los strings image y title
                    setRecyclerAdapter(count-2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //**************************************************************************************************
    //                                       AUX FUNCTIONS
    //**************************************************************************************************
    /* Sets category title on Toolbar */
    public void setToolbarTitle(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mCategoryTitle);
    }

    //**************************************************************************************************
    //                                        PERMISSIONS
    //**************************************************************************************************
    public void onPermissionsGranted(int requestCode) {
        //Do anything when permisson granted
        Toast.makeText(getApplicationContext(), "Permisos condedidos!", Toast.LENGTH_LONG).show();
    }

}

