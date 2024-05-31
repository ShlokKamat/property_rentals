package com.example.rentalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import java.util.ArrayList;

public class PropertyListAdapter extends RecyclerView.Adapter<PropertyListAdapter.PropertyViewHolder> {

    private final PropertyListInterface propertyListInterface;
    Context context;
    ArrayList<PropertyDataClass> propertyDataArrayList;

    public PropertyListAdapter(Context context, ArrayList<PropertyDataClass> propertyDataClassArrayList, PropertyListInterface propertyListInterface) {

        this.context = context;
        this.propertyDataArrayList = propertyDataClassArrayList;
        this.propertyListInterface = propertyListInterface;
    }

    @NonNull
    @Override
    public PropertyListAdapter.PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.property_list_item, parent, false);
        return new PropertyViewHolder(view, propertyListInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyListAdapter.PropertyViewHolder holder, int position) {
        PropertyDataClass property = propertyDataArrayList.get(position);

        String bhkTypeString = property.getBhkType();
        holder.bhkType.setText(bhkTypeString);
        String apartmentNameString = property.getApartmentName();
        holder.apartmentName.setText(apartmentNameString);
        String localityString = property.getLocality();
        holder.locality.setText(localityString);
        String propertySizeString = property.getPropertySize() + " sq ft";
        holder.propertySize.setText(propertySizeString);
        holder.furnishingType.setText(property.getFurnishingType());
        String rentString = "₹" + Utils.formatToIndianCurrency(property.getExpectedRent());
        holder.expectedRent.setText(rentString);
        Glide.with(context)
                .load(property.getPhotos())
                .placeholder(R.drawable.animated_loading_spinner)
                .thumbnail(0.05f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.propertyPhoto);
    }

    @Override
    public int getItemCount() {
        return propertyDataArrayList.size();
    }

    public static class PropertyViewHolder extends RecyclerView.ViewHolder {

        TextView bhkType, apartmentName, locality, propertySize, furnishingType, expectedRent;
        ImageView propertyPhoto;

        public PropertyViewHolder(@NonNull View itemView, PropertyListInterface propertyListInterface) {
            super(itemView);
            bhkType = itemView.findViewById(R.id.bhk_type_row);
            apartmentName = itemView.findViewById(R.id.apartment_name_row);
            locality = itemView.findViewById(R.id.locality_row);
            propertySize = itemView.findViewById(R.id.property_size_row);
            furnishingType = itemView.findViewById(R.id.furnishing_type_row);
            expectedRent = itemView.findViewById(R.id.expected_rent_row);
            propertyPhoto = itemView.findViewById(R.id.property_photo_row);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (propertyListInterface != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            propertyListInterface.onPropertyItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
