package com.jfloydconsult.matatu;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class GeoPointMarkerInfoWindowView implements GoogleMap.InfoWindowAdapter {
    private Context mContext;

    GeoPointMarkerInfoWindowView(Context context) {
        mContext = context;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        View v = ((Activity)mContext).getLayoutInflater().inflate(R.layout.map_info_window, null);
            TextView infoTitle = v.findViewById(R.id.tvName);
            TextView infoAddress = v.findViewById(R.id.tvAddress);
            TextView infoLatitude = v.findViewById(R.id.tvLatitude);
            TextView infoLongitude = v.findViewById(R.id.tvLongitude);
            if (infoTitle != null)
            {
                infoTitle.setText(String.format("Name: %s", marker.getTitle()));
            }
            if (infoAddress != null)
            {
                infoAddress.setText(String.format("Location: %s", marker.getSnippet()));
            }
            if (infoLatitude != null)
            {
                infoLatitude.setText(String.format("Latitude: %s", marker.getPosition().latitude));
            }
            if (infoLongitude != null)
            {
                infoLongitude.setText(String.format("Longitude: %s", marker.getPosition().longitude));
            }
            return v;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
