package tfg.uniovi.es.guiaintermareal.adapter;


import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import tfg.uniovi.es.guiaintermareal.R;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>   {

    private ArrayList<String> data;


    public SearchAdapter(ArrayList<String> data){
        this.data = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titleSearchItem;

        public ViewHolder(View itemView) {
            super(itemView);
            titleSearchItem = (TextView) itemView.findViewById(R.id.titleSearchItem);
        }
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder holder, int position) {
        holder.titleSearchItem.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
