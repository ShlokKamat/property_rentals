package com.example.rentalapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class PropertyListAdapter extends RecyclerView.Adapter<PropertyListAdapter.PropertyViewHolder> {

    Context context;
    ArrayList<PropertyDataClass> propertyDataArrayList;

    public PropertyListAdapter(Context context, ArrayList<PropertyDataClass> propertyDataClassArrayList) {
        this.context = context;
        this.propertyDataArrayList = propertyDataClassArrayList;
    }

    @NonNull
    @Override
    public PropertyListAdapter.PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.property_list_item, parent, false);
        return new PropertyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyListAdapter.PropertyViewHolder holder, int position) {
        PropertyDataClass property = propertyDataArrayList.get(position);

        holder.bhkType.setText(property.getBhkType());
        String localityString = "at " + property.getLocality();
        holder.locality.setText(localityString);
        holder.furnishingType.setText(property.getFurnishingType());
        String rentString = "₹ " + property.getExpectedRent();
        holder.expectedRent.setText(rentString);
//        holder.propertyPhoto.setImageBitmap();
    }

    @Override
    public int getItemCount() {
        return propertyDataArrayList.size();
    }

    public static class PropertyViewHolder extends RecyclerView.ViewHolder {

        TextView bhkType, locality, furnishingType, expectedRent;
        ImageView propertyPhoto;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            bhkType = itemView.findViewById(R.id.bhk_type_row);
            locality = itemView.findViewById(R.id.locality_row);
            furnishingType = itemView.findViewById(R.id.furnishing_type_row);
            expectedRent = itemView.findViewById(R.id.expected_rent_row);
            propertyPhoto = itemView.findViewById(R.id.property_photo_row);
        }
    }
}
