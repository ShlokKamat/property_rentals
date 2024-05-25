package com.example.rentalapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class BrowseAllPropertyActivity extends AppCompatActivity implements PropertyListInterface {

    public static final String PROPERTY_PARCEL_KEY = "property_parcel_key";
    RecyclerView propertyListRecyclerView;
    ArrayList<PropertyDataClass> propertyDataArrayList;
    PropertyListAdapter propertyListAdapter;
    FirebaseFirestore db;
    AlertDialog.Builder builder;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_browse_all_property);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.BrowseAllPropertyActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.load_progress_layout);
        dialog = builder.create();
        dialog.show();

        // Inflate the layout for this fragment
        propertyListRecyclerView = findViewById(R.id.property_list_recycler_view);
        propertyListRecyclerView.setHasFixedSize(true);
        propertyListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        propertyDataArrayList = new ArrayList<PropertyDataClass>();
        propertyListAdapter = new PropertyListAdapter(this, propertyDataArrayList, this);

        propertyListRecyclerView.setAdapter(propertyListAdapter);

        EventChangeListener();
    }

    private void EventChangeListener() {
        db.collection("properties")//.orderBy("apartmentName")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            dialog.dismiss();
                            Log.e("FireStore Error", Objects.requireNonNull(error.getMessage()));
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                propertyDataArrayList.add(dc.getDocument().toObject(PropertyDataClass.class));
                            } else if (dc.getType() == DocumentChange.Type.MODIFIED) {
                                propertyDataArrayList.set(dc.getNewIndex(), dc.getDocument().toObject(PropertyDataClass.class));
                            }

                            propertyListAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }

                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }
    @Override
    public void onPropertyItemClick(int position) {
        Intent viewPropertyIntent = new Intent(this, ViewPropertyActivity.class);
        viewPropertyIntent.putExtra(PROPERTY_PARCEL_KEY, propertyDataArrayList.get(position));
        startActivity(viewPropertyIntent);
        //Click on Property Action
    }
}