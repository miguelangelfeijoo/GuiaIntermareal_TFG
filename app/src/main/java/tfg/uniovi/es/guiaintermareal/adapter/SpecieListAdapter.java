package tfg.uniovi.es.guiaintermareal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import tfg.uniovi.es.guiaintermareal.MainActivity;
import tfg.uniovi.es.guiaintermareal.R;
import tfg.uniovi.es.guiaintermareal.model.Specie;
import tfg.uniovi.es.guiaintermareal.ui.CategoryActivity;


public class SpecieListAdapter extends Activity{

    //View Holder For Recycler View
    public static class SpecieViewHolder extends RecyclerView.ViewHolder  {
        private final Context context;
        private int count;

        public SpecieViewHolder(final View itemView) {
            super(itemView);
            context = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = null;
                    Specie sp = MainActivity.firebaseRecyclerAdapter.getItem(getAdapterPosition());
                    count = MainActivity.firebaseRecyclerAdapter.getItemCount();

                    for (int i = 0; i < count; i++){
                        intent = new Intent(context, CategoryActivity.class);
                        intent.putExtra("title", sp.getTitle());
                        intent.putExtra("description", sp.getDescription());
                        intent.putExtra("ecology", sp.getEcology());
                        intent.putExtra("image", sp.getImage());
                    }

                    context.startActivity(intent);
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
                    //Nada que hacer aqui
                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(image).into(post_image);
                }
            });
            //Picasso.with(ctx).load(image).into(post_image);
        }
    }
}
