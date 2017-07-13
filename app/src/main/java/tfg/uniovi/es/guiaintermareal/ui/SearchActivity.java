package tfg.uniovi.es.guiaintermareal.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import tfg.uniovi.es.guiaintermareal.R;
import tfg.uniovi.es.guiaintermareal.adapter.SearchAdapter;

public class SearchActivity extends AppCompatActivity {

    /* Variables de acceso a Firebase */
    FirebaseDatabase database;
    DatabaseReference myRef, habitatRef;
    ArrayList<String> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        String query = getIntent().getStringExtra("query");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        query = Character.toUpperCase(query.charAt(0)) + query.substring(1).toLowerCase();
        toolbar.setTitle("Especies de " + query);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Send a Query to the database
        database = FirebaseDatabase.getInstance();
        habitatRef = database.getReference().child("Habitat");
        habitatRef.keepSynced(true);
        myRef = habitatRef.child(query);


        ImageView img = (ImageView) findViewById(R.id.habitatImage);

        Drawable res ;
        switch(query){
            case "Rio":
                res = getApplicationContext().getResources().getDrawable(R.drawable.rio);
                img.setImageDrawable(res);
                img.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case "Pedrero":
                res = getApplicationContext().getResources().getDrawable(R.drawable.pedrero);
                img.setImageDrawable(res);
                img.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case "Mar":
                res = getApplicationContext().getResources().getDrawable(R.drawable.mar);
                img.setImageDrawable(res);
                img.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            default:
                res = getApplicationContext().getResources().getDrawable(R.drawable.no_encontrado);
                img.setImageDrawable(res);
                break;
        }


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_list);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SearchAdapter(getSearchList()));
    }

    private ArrayList<String> getSearchList(){

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp : dataSnapshot.getChildren()){
                    data.add(dsp.getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return data;
    }

}
