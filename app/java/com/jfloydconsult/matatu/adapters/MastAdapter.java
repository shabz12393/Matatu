package com.jfloydconsult.matatu.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jfloydconsult.edas.R;
import com.jfloydconsult.edas.model.Masts;

import java.util.List;

public class MastAdapter extends ArrayAdapter<Masts> {
    private Context mContext;
    public MastAdapter(Context context, int resource, List<Masts> masts) {
        super(context, resource, masts);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View v, ViewGroup parent) {
        if(v == null)
            v = LayoutInflater.from(mContext).inflate(R.layout.list_item_mast,parent,false);
        Masts masts = getItem(position); TextView mLocationTextView;
        TextView mBaseTextView;
        TextView mDistanceTextView;

        mLocationTextView = v
                .findViewById(R.id.list_item_mast_location_text_view);
        mBaseTextView = v
                .findViewById(R.id.list_item_mast_base_text_view);
        mDistanceTextView = v
                .findViewById(R.id.list_item_mast_distance_text_view);
        if(masts!=null){
            mLocationTextView.setText(masts.getGeoPoints().getGeoLocation());
            mBaseTextView.setText(masts.getGeoPoints().getGeoBase());
            mDistanceTextView.setText(masts.getDistance());
        }


        return v;
    }
}
