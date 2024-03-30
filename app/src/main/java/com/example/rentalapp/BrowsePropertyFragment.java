package com.example.rentalapp;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class BrowsePropertyFragment extends Fragment {

    RecyclerView propertyListRecyclerView;
    ArrayList<PropertyDataClass> propertyDataArrayList;
    PropertyListAdapter propertyListAdapter;
    FirebaseFirestore db;
    AlertDialog.Builder builder;
    AlertDialog dialog;

    public BrowsePropertyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        builder = new AlertDialog.Builder(this.getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse_property, container, false);
        propertyListRecyclerView = view.findViewById(R.id.property_list_recycler_view);
        propertyListRecyclerView.setHasFixedSize(true);
        propertyListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        db = FirebaseFirestore.getInstance();
        propertyDataArrayList = new ArrayList<PropertyDataClass>();
        propertyListAdapter = new PropertyListAdapter(this.getContext(), propertyDataArrayList);

        propertyListRecyclerView.setAdapter(propertyListAdapter);

        EventChangeListener();


        return view;
    }

    private void EventChangeListener() {
        db.collection("properties")//Add orderBy is needed
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
}