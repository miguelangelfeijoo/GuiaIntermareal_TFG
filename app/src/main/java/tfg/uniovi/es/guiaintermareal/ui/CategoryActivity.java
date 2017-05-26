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
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import tfg.uniovi.es.guiaintermareal.MainActivity;
import tfg.uniovi.es.guiaintermareal.R;

import static tfg.uniovi.es.guiaintermareal.MainActivity.mRootRef;

public class CategoryActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static String TOOLBAR_ACTION_TYPE = "";
    private ProgressDialog mProgressDialog;
    public StorageReference mStorage;
    Uri picUri;
    String nombre, description, imageUrl, taxonomy, ecology, habitat;
    ArrayList<String> references;
    private FABToolbarLayout morph;
    TextView vTitle, vDescription, vEcology, vTaxonomy, vHabitat, vReferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("title"));
        setSupportActionBar(toolbar);

        mStorage = FirebaseStorage.getInstance().getReference();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        mProgressDialog = new ProgressDialog(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        morph = (FABToolbarLayout) findViewById(R.id.fabtoolbar);

        View gallery, camera, map, exit;
        gallery = findViewById(R.id.gallery);
        camera = findViewById(R.id.camera);
        map = findViewById(R.id.map);
        exit = findViewById(R.id.exit);

        fab.setOnClickListener(this);
        gallery.setOnClickListener(this);
        camera.setOnClickListener(this);
        map.setOnClickListener(this);
        exit.setOnClickListener(this);

        nombre = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");
        ecology = getIntent().getStringExtra("ecology");
        imageUrl = getIntent().getStringExtra("image");
        taxonomy = getIntent().getStringExtra("taxonomy");
        habitat = getIntent().getStringExtra("habitat");
        references = getIntent().getStringArrayListExtra("references");

        vTitle = (TextView) findViewById(R.id.vTitle);
        vDescription = (TextView) findViewById(R.id.vDescription);
        vEcology = (TextView) findViewById(R.id.vEcology);
        vTaxonomy = (TextView) findViewById(R.id.vTaxonomy);
        vHabitat = (TextView) findViewById(R.id.vHabitat);
        vReferences = (TextView) findViewById(R.id.vReferences);

        vTitle.setText(nombre);
        vDescription.setText(description);
        vEcology.setText(ecology);
        vTaxonomy.setText(taxonomy);
        vHabitat.setText(habitat);
        //String referenceList=" ";
        StringBuilder referenceList = new StringBuilder();
        for(int i=0; i<references.size();i++){
            referenceList.append(references.get(i));
            referenceList.append("\n");
        }
        vReferences.setText(referenceList);
        setImage(getApplicationContext(),imageUrl);
    }

    @Override
    public void onClick(View v){
        Intent intent;
        switch (v.getId()) {
            case R.id.fab:
                morph.show();
                break;

            case R.id.camera:
                intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                TOOLBAR_ACTION_TYPE = "camera";
                File file=getOutputMediaFile(1);
                picUri = Uri.fromFile(file); // create
                intent.putExtra(MediaStore.EXTRA_OUTPUT,picUri); // set the image file
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                break;

            case R.id.gallery:
                intent = new Intent(Intent.ACTION_PICK);
                TOOLBAR_ACTION_TYPE = "gallery";
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                break;

            case R.id.map:
                intent = new Intent(CategoryActivity.this, MapsActivity.class);

                intent.putExtra("ref", mRootRef);
                intent.putExtra("title", getIntent().getStringExtra("title"));
                startActivity(intent);
                break;

            case R.id.exit:
                break;
        }
        morph.hide();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (networkConnected(getApplicationContext())) {
            if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                mProgressDialog.setMessage("Subiendo archivo...");
                mProgressDialog.show();
                Uri uri;
                System.out.println("******FLAG: "+getIntent().getStringExtra("type"));
                if(TOOLBAR_ACTION_TYPE == "gallery") {
                    //La imagen se obtiene de la galeria
                    uri = data.getData();
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
                }else{
                    //La imagen se obtiene de la camara
                    uri = picUri;
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
