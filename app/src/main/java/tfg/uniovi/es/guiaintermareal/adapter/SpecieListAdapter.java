package tfg.uniovi.es.guiaintermareal.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import tfg.uniovi.es.guiaintermareal.MainActivity;
import tfg.uniovi.es.guiaintermareal.R;
import tfg.uniovi.es.guiaintermareal.model.Specie;
import tfg.uniovi.es.guiaintermareal.ui.CategoryActivity;


public class SpecieListAdapter extends MainActivity {

    //View Holder For Recycler View
    public static class SpecieViewHolder extends RecyclerView.ViewHolder  {
        private final Context context;
        private int count;
        FirebaseDatabase database;
        DatabaseReference ref, myRef;

        public SpecieViewHolder(final View itemView) {
            super(itemView);
            context = itemView.getContext();
            database = FirebaseDatabase.getInstance();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count = MainActivity.firebaseRecyclerAdapter.getItemCount();
                    ref = MainActivity.firebaseRecyclerAdapter.getRef(getAdapterPosition());
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                                if (! (dataSnapshot.hasChild("subcategory"))) {
                                    //ES SUBCATEGORIA
                                    String key = dataSnapshot.getKey();
                                    loadSubcategoryData(ref.getParent().getKey(), key);
                                }else{
                                    //NO ES SUBCATEGORIA
                                    Intent intent = null;
                                    Specie sp = MainActivity.firebaseRecyclerAdapter.getItem(getAdapterPosition());
                                    for (int i = 0; i < count; i++){
                                        intent = new Intent(context, CategoryActivity.class);
                                        intent.putExtra("title", sp.getTitle());
                                        intent.putExtra("description", sp.getDescription());
                                        intent.putExtra("size", sp.getSize());
                                        intent.putExtra("ecology", sp.getEcology());
                                        intent.putExtra("image", sp.getImage());
                                        intent.putExtra("habitat", sp.getHabitat());
                                        intent.putExtra("taxonomy", sp.getTaxonomy());
                                        intent.putStringArrayListExtra("references", sp.getReferences());
                                        intent.putStringArrayListExtra("carousel", sp.getCarousel());
                                    }
                                    context.startActivity(intent);
                                }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });
        }

        private void loadSubcategoryData(String category, final String subcategory){
            mCategoryTitle = subcategory;
            mCategoryRef = "Categorias/" + category + "/" + mCategoryTitle;
            myRef = database.getReference(mCategoryRef);

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                int count;
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    count = (int) dataSnapshot.getChildrenCount();
                    MainActivity.toolbar.setTitle(subcategory);
                    //Filtrar los resultados de la referencia para no mostrar
                    //los dos ultimos referentes a los strings image y title
                    setRecyclerAdapter(count-2);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setTitle(String title){
            TextView post_title = (TextView)itemView.findViewById(R.id.titleText);
            post_title.setText(title);
        }

        public void setImage(final Context ctx , final String image){
            final ImageView post_image = (ImageView)itemView.findViewById(R.id.imageViewy);
            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(image).into(post_image);
                }
            });
        }

        public void setRecyclerAdapter(int count){
            firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<Specie, SpecieListAdapter.SpecieViewHolder>(Specie.class, R.layout.design_row, SpecieListAdapter.SpecieViewHolder.class, myRef.limitToFirst(count)) {

                        @Override
                        protected void populateViewHolder(SpecieListAdapter.SpecieViewHolder viewHolder, Specie model, int position) {
                            viewHolder.setTitle(model.getTitle());
                            viewHolder.setImage(context, model.getImage());
                        }
                    };
            mSpecieList.getRecycledViewPool().clear();
            mSpecieList.stopScroll();
            mSpecieList.setAdapter(firebaseRecyclerAdapter);
        }
    }
}
