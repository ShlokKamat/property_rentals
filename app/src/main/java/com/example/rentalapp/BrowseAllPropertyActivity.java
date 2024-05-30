package com.example.rentalapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class BrowseAllPropertyActivity extends AppCompatActivity implements PropertyListInterface {

    public static final String PROPERTY_PARCEL_KEY = "property_parcel_key";
    private DocumentSnapshot lastVisible;
    private boolean isLoading = false;
    private boolean noMoreData = false;
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

        loadInitialRowData();

        propertyListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && !noMoreData && layoutManager != null && layoutManager.findLastVisibleItemPosition() == propertyDataArrayList.size() - 1) {
                    // Load more data when end of the list is reached
                    loadMoreData();
                    isLoading = true;
                }
            }
        });
    }

    private void loadInitialRowData() {
        db.collection("properties")//.orderBy("apartmentName")
                .limit(Utils.MAX_PROPERTIES_PER_LOAD)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            dialog.dismiss();
                            Log.e("FireStore Error", Objects.requireNonNull(error.getMessage()));
                            return;
                        }
                        if (!querySnapshot.isEmpty()) {

                            for (DocumentChange dc : querySnapshot.getDocumentChanges()) {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    propertyDataArrayList.add(dc.getDocument().toObject(PropertyDataClass.class));
                                } else if (dc.getType() == DocumentChange.Type.MODIFIED) {
                                    propertyDataArrayList.set(dc.getNewIndex(), dc.getDocument().toObject(PropertyDataClass.class));
                                } else if (dc.getType() == DocumentChange.Type.REMOVED) {
                                    propertyDataArrayList.remove(dc.getOldIndex());
                                }

                                propertyListAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }

                            lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                        } else {
                            noMoreData = true; // No more data to load
                        }

                    }
                });
    }

    private void loadMoreData() {
        if (lastVisible != null) {
            isLoading = true;
            dialog.show();
            db.collection("properties")
                    .startAfter(lastVisible)
                    .limit(Utils.MAX_PROPERTIES_PER_LOAD)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (!querySnapshot.isEmpty()) {
                                    int initialSize = propertyDataArrayList.size();
                                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                        PropertyDataClass property = document.toObject(PropertyDataClass.class);
                                        if (property != null) {
                                            property.setDocumentId(document.getId());
                                            propertyDataArrayList.add(property);
                                        }
                                    }
                                    propertyListAdapter.notifyItemRangeInserted(initialSize, querySnapshot.size());
                                    lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
//                                    Toast.makeText(BrowseAllPropertyActivity.this, "Loaded More " + querySnapshot.size(), Toast.LENGTH_SHORT).show();
                                } else {
//                                    Toast.makeText(BrowseAllPropertyActivity.this, "No more data", Toast.LENGTH_SHORT).show();
                                    noMoreData = true;
                                }
                            } else {
                                Log.w("Firestore", "Error getting documents.", task.getException());
                            }
                            isLoading = false;
                            dialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firestore Error", e.getMessage());
                            isLoading = false;  // Reset loading state
                            dialog.dismiss();  // Dismiss the dialog on error
                        }
                    });
        } else {
            Log.d("Firestore", "Last visible document is null");
            dialog.dismiss();  // Dismiss the dialog if there's no last visible document
        }
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