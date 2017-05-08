package tfg.uniovi.es.guiaintermareal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import tfg.uniovi.es.guiaintermareal.MainActivity;
import tfg.uniovi.es.guiaintermareal.R;
import tfg.uniovi.es.guiaintermareal.model.Specie;
import tfg.uniovi.es.guiaintermareal.ui.AlgasActivity;
import tfg.uniovi.es.guiaintermareal.ui.AnelidosActivity;
import tfg.uniovi.es.guiaintermareal.ui.AnemonasActivity;
import tfg.uniovi.es.guiaintermareal.ui.CrustaceosActivity;
import tfg.uniovi.es.guiaintermareal.ui.EquinodermosActivity;
import tfg.uniovi.es.guiaintermareal.ui.MoluscosActivity;
import tfg.uniovi.es.guiaintermareal.ui.PecesActivity;


public class SpecieListAdapter extends Activity{

    //View Holder For Recycler View
    public static class SpecieViewHolder extends RecyclerView.ViewHolder  {
        private final Context context;

        public SpecieViewHolder(final View itemView) {
            super(itemView);
            context = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = null;
                    Specie sp = MainActivity.firebaseRecyclerAdapter.getItem(getAdapterPosition());

                    switch (MainActivity.mCategoryTitle){
                        case "Algas y Liquenes":
                            intent = new Intent(context, AlgasActivity.class);
                            intent.putExtra("title", sp.getTitle());
                            intent.putExtra("description", sp.getDescription());
                            intent.putExtra("ecology", sp.getEcology());
                            intent.putExtra("image", sp.getImage());
                            break;

                        case "Esponjas, Anemonas y Corales":
                            intent = new Intent(context, AnemonasActivity.class);
                            intent.putExtra("title", sp.getTitle());
                            intent.putExtra("description", sp.getDescription());
                            intent.putExtra("ecology", sp.getEcology());
                            intent.putExtra("image", sp.getImage());
                            break;

                        case "Crustaceos":
                            intent = new Intent(context, CrustaceosActivity.class);
                            intent.putExtra("title", sp.getTitle());
                            intent.putExtra("description", sp.getDescription());
                            intent.putExtra("ecology", sp.getEcology());
                            intent.putExtra("image", sp.getImage());
                            break;

                        case "Moluscos":
                            intent = new Intent(context, MoluscosActivity.class);
                            intent.putExtra("title", sp.getTitle());
                            intent.putExtra("description", sp.getDescription());
                            intent.putExtra("ecology", sp.getEcology());
                            intent.putExtra("image", sp.getImage());
                            break;

                        case "Equinodermos":
                            intent = new Intent(context, EquinodermosActivity.class);
                            intent.putExtra("title", sp.getTitle());
                            intent.putExtra("description", sp.getDescription());
                            intent.putExtra("ecology", sp.getEcology());
                            intent.putExtra("image", sp.getImage());
                            break;

                        case "Peces":
                            intent = new Intent(context, PecesActivity.class);
                            intent.putExtra("title", sp.getTitle());
                            intent.putExtra("description", sp.getDescription());
                            intent.putExtra("ecology", sp.getEcology());
                            intent.putExtra("image", sp.getImage());
                            break;

                        case "Anelidos":
                            intent = new Intent(context, AnelidosActivity.class);
                            intent.putExtra("title", sp.getTitle());
                            intent.putExtra("description", sp.getDescription());
                            intent.putExtra("ecology", sp.getEcology());
                            intent.putExtra("image", sp.getImage());
                            break;
                    }

                    context.startActivity(intent);
                }
            });
        }

        public void setTitle(String title){
            TextView post_title = (TextView)itemView.findViewById(R.id.titleText);
            post_title.setText(title);
        }
        public void setImage(Context ctx , String image){
            ImageView post_image = (ImageView)itemView.findViewById(R.id.imageViewy);
            Picasso.with(ctx).load(image).into(post_image);
        }
    }
}
