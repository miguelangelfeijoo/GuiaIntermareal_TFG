package tfg.uniovi.es.guiaintermareal.ui;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

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
        toolbar.setTitle(query);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        // Send a Query to the database
        database = FirebaseDatabase.getInstance();
        habitatRef = database.getReference().child("Habitat");
        myRef = habitatRef.child(query);

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
