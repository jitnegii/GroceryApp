package com.groceryapp.adapters;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.maps.model.LatLng;
import com.groceryapp.R;

import java.util.List;


public class SearchedItemAdapter extends BaseAdapter {

    List<Address> addresses;
    private LocOnClickListener listener;

    public SearchedItemAdapter(List<Address> addresses) {

        this.addresses = addresses;

    }

    @Override
    public int getCount() {
        return addresses.size();
    }

    @Override
    public Object getItem(int position) {
        return addresses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return addresses.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.searched_loc_layout, parent, false);
            TextView city = convertView.findViewById(R.id.city);
            TextView state = convertView.findViewById(R.id.state);
            ConstraintLayout root = convertView.findViewById(R.id.root);

            Address address = addresses.get(position);
            city.setText(address.getFeatureName());
            state.setText(address.getAddressLine(0));

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(address);
                }
            });

        }

        return convertView;
    }

    public void setListener(LocOnClickListener listener) {
        this.listener = listener;
    }

    public interface LocOnClickListener {
        void onClick(Address address);
    }
}
