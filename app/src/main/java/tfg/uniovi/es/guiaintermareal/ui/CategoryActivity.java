package tfg.uniovi.es.guiaintermareal.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import tfg.uniovi.es.guiaintermareal.MainActivity;
import tfg.uniovi.es.guiaintermareal.R;

public class CategoryActivity extends MainActivity{

    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private ProgressDialog mProgressDialog;
    public StorageReference mStorage;
    Uri picUri;
    String nombre, description, imageUrl, taxonomy, ecology, habitat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView vTitle, vDescription, vEcology, vTaxonomy, vHabitat;
        mStorage = FirebaseStorage.getInstance().getReference();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CategoryActivity.this, MapsActivity.class);
                i.putExtra("ref", mRootRef);
                i.putExtra("title", getIntent().getStringExtra("title"));
                startActivity(i);
            }
        });*/

        mProgressDialog = new ProgressDialog(this);
        FloatingActionButton identify = (FloatingActionButton) findViewById(R.id.identify);
        identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                File file=getOutputMediaFile(1);
                picUri = Uri.fromFile(file); // create
                i.putExtra(MediaStore.EXTRA_OUTPUT,picUri); // set the image file
                startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
                /*Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);*/
            }
        });

        nombre = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");
        ecology = getIntent().getStringExtra("ecology");
        imageUrl = getIntent().getStringExtra("image");
        taxonomy = getIntent().getStringExtra("taxonomy");
        habitat = getIntent().getStringExtra("habitat");

        vTitle = (TextView) findViewById(R.id.vTitle);
        vDescription = (TextView) findViewById(R.id.vDescription);
        vEcology = (TextView) findViewById(R.id.vEcology);
        vTaxonomy = (TextView) findViewById(R.id.vTaxonomy);
        vHabitat = (TextView) findViewById(R.id.vHabitat);

        vTitle.setText(nombre);
        vDescription.setText(description);
        vEcology.setText(ecology);
        vTaxonomy.setText(taxonomy);
        vHabitat.setText(habitat);
        setImage(getApplicationContext(),imageUrl);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (networkConnected(getApplicationContext())) {
            //Captura de foto
            if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                mProgressDialog.setMessage("Subiendo archivo...");
                mProgressDialog.show();

                //La conexion esta habilitada
                Uri uri = picUri;
                StorageReference filepath = mStorage.child("A confirmar").child(nombre).child(uri.getLastPathSegment());
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(CategoryActivity.this, "Imagen subida con exito!", Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CategoryActivity.this, "Ha habido un fallo al subir la imagen!!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }else{
            mProgressDialog.dismiss();
            Toast.makeText(CategoryActivity.this, "Es necesario tener conexion a Internet para subir la foto!!", Toast.LENGTH_LONG).show();
        }


    }

    public static boolean networkConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nwi = cm.getActiveNetworkInfo();
        return nwi != null && nwi.isConnectedOrConnecting();
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".png");
        } else {
            return null;
        }

        return mediaFile;
    }


    private void setImage(Context ctx , String image){
        ImageView vImage = (ImageView)findViewById(R.id.vImage);
        Picasso.with(ctx).load(image).into(vImage);
    }

}
