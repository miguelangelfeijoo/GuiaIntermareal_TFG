package tfg.uniovi.es.guiaintermareal.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.List;

import tfg.uniovi.es.guiaintermareal.R;


public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<String> mListDataHeader; // header titles
    public StorageReference storage;

    // child data in format of header title, child title
    private HashMap<String, List<String>> mListDataChild;
    ExpandableListView expandList;

    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData) {
        this.mContext = context;
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listChildData;
    }

    @Override
    public int getGroupCount() {
        return mListDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mListDataChild.get(
                this.mListDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mListDataChild.get(
                this.mListDataHeader.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        storage = FirebaseStorage.getInstance().getReference();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_header, null);
        }

        /*final ImageView groupIco = (ImageView) convertView.findViewById(R.id.iconimage);
        StorageReference iconRef = storage.child("Iconos/" + mListDataHeader.get(groupPosition).substring(0,3).toLowerCase() + ".png");
        iconRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {
                Picasso.with(mContext).load(uri).networkPolicy(NetworkPolicy.OFFLINE).into(groupIco, new Callback() {
                    @Override
                    public void onSuccess() {
                        //Nada que hacer aqui
                    }

                    @Override
                    public void onError() {
                        Picasso.with(mContext).load(uri).into(groupIco);
                    }
                });
            }
        });*/
        String uri = "@drawable/" + mListDataHeader.get(groupPosition).substring(0,4).toLowerCase();
        int imageResource = mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());

        final ImageView groupIco = (ImageView) convertView.findViewById(R.id.iconimage);
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.submenu);

        Drawable res = mContext.getResources().getDrawable(imageResource);
        groupIco.setImageDrawable(res);

        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_submenu, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.submenu);

        txtListChild.setText(childText);

        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
