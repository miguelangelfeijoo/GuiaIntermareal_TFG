package cardview.firebase.example.com.firebasecardview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

//View Holder For Recycler View
public class SpecieViewHolder extends RecyclerView.ViewHolder  {
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