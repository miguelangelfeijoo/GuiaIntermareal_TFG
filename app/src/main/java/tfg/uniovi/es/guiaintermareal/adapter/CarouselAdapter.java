package tfg.uniovi.es.guiaintermareal.adapter;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import tfg.uniovi.es.guiaintermareal.R;

public class CarouselAdapter extends PagerAdapter{

    Context context;
    ArrayList<String> images;
    LayoutInflater layoutInflater;

    public CarouselAdapter(Context context, ArrayList<String> images){
        this.context = context;
        this.images = images;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position){
        View itemView = layoutInflater.inflate(R.layout.pager_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        Picasso.with(context).load(images.get(position)).into(imageView);

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((LinearLayout) object);
    }

}
