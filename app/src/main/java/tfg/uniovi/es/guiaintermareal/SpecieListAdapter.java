package tfg.uniovi.es.guiaintermareal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by MiguelAngel on 05/05/2017.
 */

public class SpecieListAdapter extends Activity{

    //View Holder For Recycler View
    public static class SpecieViewHolder extends RecyclerView.ViewHolder  {
        private final Context context;
        public SpecieViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://es.wikipedia.org/wiki/Alga"));
                Intent browserChooserIntent = Intent.createChooser(browserIntent , "Choose browser of your choice");
                v.getContext().startActivity(browserChooserIntent);*/
                    Intent intent = new Intent(context,AlgasActivity.class);
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
