package cardview.firebase.example.com.firebasecardview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import cardview.firebase.example.com.firebasecardview.model.Specie;



public class MainActivity extends AppCompatActivity {

    public String mCategoryRef = "Categorias/Especies/Algas";
    private RecyclerView mSpecieList;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Recycler View
        mSpecieList = (RecyclerView)findViewById(R.id.blog_list);
        mSpecieList.setHasFixedSize(true);
        mSpecieList.setLayoutManager(new LinearLayoutManager(this));

        // Send a Query to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(mCategoryRef);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Specie, SpecieViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Specie, SpecieViewHolder>(
                        Specie.class,
                        R.layout.design_row,
                        SpecieViewHolder.class,
                        myRef)  {



                    @Override
                    protected void populateViewHolder(SpecieViewHolder viewHolder, Specie model,int position) {
                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setImage(getApplicationContext(), model.getImage());
                    }
                };
        mSpecieList.setAdapter(firebaseRecyclerAdapter);
    }
    //View Holder For Recycler View
    public static class SpecieViewHolder extends RecyclerView.ViewHolder  {
        View mView;
        public SpecieViewHolder(View itemView) {
            super(itemView);
            mView= itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://es.wikipedia.org/wiki/Alga"));
                    Intent browserChooserIntent = Intent.createChooser(browserIntent , "Choose browser of your choice");
                    v.getContext().startActivity(browserChooserIntent);
                }
            });
        }
        public void setTitle(String title){
            TextView post_title = (TextView)mView.findViewById(R.id.titleText);
            post_title.setText(title);
        }
        public void setImage(Context ctx , String image){
            ImageView post_image = (ImageView)mView.findViewById(R.id.imageViewy);
            // We Need TO pass Context
            Picasso.with(ctx).load(image).into(post_image);
        }
    }

    public String getCategoryRef(){
        return mCategoryRef;
    }

    public void setCategoryRef(String ref){
        mCategoryRef = ref;
    }
}



