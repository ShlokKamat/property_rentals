package com.example.rentalapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

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
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class BrowseMyProperty extends AppCompatActivity implements PropertyListInterface {

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
    TextInputEditText propertySearchInput;
    ImageButton searchButton, filterAndSortButton;
    boolean isFilter1rk = false, isFilter1bhk = false, isFilter2bhk = false, isFilter3bhk = false, isFilter4bhk = false, isFilterUnfurnished = false, isFilterSemifurnished = false, isFilterFullyfurnished = false;
    boolean isSortRentAsc = false, isSortRentDesc = false, isSortSizeAsc = false, isSortSizeDesc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_browse_my_property);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.BrowseMyPropertyActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.load_progress_layout);
        dialog = builder.create();
        dialog.show();

        propertySearchInput = findViewById(R.id.property_search_input);
        searchButton = findViewById(R.id.search_button);
        filterAndSortButton = findViewById(R.id.filter_and_sort_button);

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
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadInitialRowData();
            }
        });
        filterAndSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterAndSortDialog();
            }
        });
    }

    private void loadInitialRowData() {
        Query initialQuery = db.collection("properties").whereEqualTo("postedBy", Utils.getUserEmail());

        dialog.show();
        propertyDataArrayList.clear();
        noMoreData = false;
        if (propertySearchInput.getText().toString().isEmpty()) {
            initialQuery = addFilterAndSortQueries(initialQuery);
            initialQuery = initialQuery.limit(Utils.MAX_PROPERTIES_PER_LOAD);
        } else {
            initialQuery = initialQuery
                    .whereGreaterThanOrEqualTo("locality", propertySearchInput.getText().toString())
                    .whereLessThanOrEqualTo("locality", propertySearchInput.getText().toString() + "\uf8ff");
            initialQuery = addFilterAndSortQueries(initialQuery);
            initialQuery = initialQuery.limit(Utils.MAX_PROPERTIES_PER_LOAD);
        }
        initialQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
            Query loadMoreQuery = db.collection("properties").whereEqualTo("postedBy", Utils.getUserEmail());

            isLoading = true;
            dialog.show();
            if (propertySearchInput.getText().toString().isEmpty()) {
                loadMoreQuery = addFilterAndSortQueries(loadMoreQuery);
                loadMoreQuery = loadMoreQuery.startAfter(lastVisible)
                        .limit(Utils.MAX_PROPERTIES_PER_LOAD);
            } else {
                loadMoreQuery = loadMoreQuery
                        .whereGreaterThanOrEqualTo("locality", propertySearchInput.getText().toString())
                        .whereLessThanOrEqualTo("locality", propertySearchInput.getText().toString() + "\uf8ff");
                loadMoreQuery = addFilterAndSortQueries(loadMoreQuery);
                loadMoreQuery = loadMoreQuery.startAfter(lastVisible)
                        .limit(Utils.MAX_PROPERTIES_PER_LOAD);
            }
            loadMoreQuery.get()
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

    private void showFilterAndSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.filter_and_sort_dialog, null);
        builder.setView(dialogView);

        Chip filter1rk, filter1bhk, filter2bhk, filter3bhk, filter4bhk, filterUnfurnished, filterSemiFurnished, filterFullyFurnished;
        filter1rk = dialogView.findViewById(R.id.chip_1rk);
        filter1bhk = dialogView.findViewById(R.id.chip_1bhk);
        filter2bhk = dialogView.findViewById(R.id.chip_2bhk);
        filter3bhk = dialogView.findViewById(R.id.chip_3bhk);
        filter4bhk = dialogView.findViewById(R.id.chip_4bhk);
        filterUnfurnished = dialogView.findViewById(R.id.chip_not_furnished);
        filterSemiFurnished = dialogView.findViewById(R.id.chip_semi_furnished);
        filterFullyFurnished = dialogView.findViewById(R.id.chip_fully_furnished);

        Chip sortRentAsc, sortRentDesc, sortSizeAsc, sortSizeDesc;
        sortRentAsc = dialogView.findViewById(R.id.chip_rent_ascending);
        sortRentDesc = dialogView.findViewById(R.id.chip_rent_descending);
        sortSizeAsc = dialogView.findViewById(R.id.chip_size_ascending);
        sortSizeDesc = dialogView.findViewById(R.id.chip_size_descending);

        Button clearFilter = dialogView.findViewById(R.id.clear_filters_button);
        Button clearSort = dialogView.findViewById(R.id.clear_sort_button);
        Button applyButton = dialogView.findViewById(R.id.apply_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);

        AlertDialog filterDialog = builder.create();
        if (isFilter1rk)
            filter1rk.setChecked(true);
        if (isFilter1bhk)
            filter1bhk.setChecked(true);
        if (isFilter2bhk)
            filter2bhk.setChecked(true);
        if (isFilter3bhk)
            filter3bhk.setChecked(true);
        if (isFilter4bhk)
            filter4bhk.setChecked(true);
        if (isFilterUnfurnished)
            filterUnfurnished.setChecked(true);
        if (isFilterSemifurnished)
            filterSemiFurnished.setChecked(true);
        if (isFilterFullyfurnished)
            filterFullyFurnished.setChecked(true);
        if (isSortRentAsc)
            sortRentAsc.setChecked(true);
        if (isSortRentDesc)
            sortRentDesc.setChecked(true);
        if (isSortSizeAsc)
            sortSizeAsc.setChecked(true);
        if (isSortSizeDesc)
            sortSizeDesc.setChecked(true);

        clearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter1rk.setChecked(false);
                isFilter1rk = false;
                filter1bhk.setChecked(false);
                isFilter1bhk = false;
                filter2bhk.setChecked(false);
                isFilter2bhk = false;
                filter3bhk.setChecked(false);
                isFilter3bhk = false;
                filter4bhk.setChecked(false);
                isFilter4bhk = false;
                filterUnfurnished.setChecked(false);
                isFilterUnfurnished = false;
                filterSemiFurnished.setChecked(false);
                isFilterSemifurnished = false;
                filterFullyFurnished.setChecked(false);
                isFilterFullyfurnished = false;
            }
        });
        clearSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortRentAsc.setChecked(false);
                isSortRentAsc = false;
                sortRentDesc.setChecked(false);
                isSortRentDesc = false;
                sortSizeAsc.setChecked(false);
                isSortSizeAsc = false;
                sortSizeDesc.setChecked(false);
                isSortSizeDesc = false;
            }
        });
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean anyFilter = anyFiltersChecked();
                boolean anySort = anySortChecked();
                if (anyFilter || anySort) {
                    loadInitialRowData();
                }
                filterDialog.dismiss();
            }

            private boolean anyFiltersChecked() {
                checkAndSetFilters();
                return filter1rk.isChecked() ||
                        filter1bhk.isChecked() ||
                        filter2bhk.isChecked() ||
                        filter3bhk.isChecked() ||
                        filter4bhk.isChecked() ||
                        filterUnfurnished.isChecked() ||
                        filterSemiFurnished.isChecked() ||
                        filterFullyFurnished.isChecked();
            }

            private void checkAndSetFilters() {
                isFilter1rk = filter1rk.isChecked();
                isFilter1bhk = filter1bhk.isChecked();
                isFilter2bhk = filter2bhk.isChecked();
                isFilter3bhk = filter3bhk.isChecked();
                isFilter4bhk = filter4bhk.isChecked();
                isFilterUnfurnished = filterUnfurnished.isChecked();
                isFilterSemifurnished = filterSemiFurnished.isChecked();
                isFilterFullyfurnished = filterFullyFurnished.isChecked();
            }

            private boolean anySortChecked() {
                checkAndSetSort();
                return sortRentAsc.isChecked() ||
                        sortRentDesc.isChecked() ||
                        sortSizeAsc.isChecked() ||
                        sortSizeDesc.isChecked();
            }

            private void checkAndSetSort() {
                isSortRentAsc = sortRentAsc.isChecked();
                isSortRentDesc = sortRentDesc.isChecked();
                isSortSizeAsc = sortSizeAsc.isChecked();
                isSortSizeDesc = sortSizeDesc.isChecked();
            }


        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
            }
        });

        filterDialog.show();
    }

    private Query addFilterAndSortQueries(Query initialQuery) {
        if (isFilter1rk)
            initialQuery = initialQuery.whereEqualTo("bhkType", "1 RK");
        if (isFilter1bhk)
            initialQuery = initialQuery.whereEqualTo("bhkType", "1 BHK");
        if (isFilter2bhk)
            initialQuery = initialQuery.whereEqualTo("bhkType", "2 BHK");
        if (isFilter3bhk)
            initialQuery = initialQuery.whereEqualTo("bhkType", "3 BHK");
        if (isFilter4bhk)
            initialQuery = initialQuery.whereEqualTo("bhkType", "4 BHK");

        if (isFilterUnfurnished)
            initialQuery = initialQuery.whereEqualTo("furnishingType", "Unfurnished");
        if (isFilterSemifurnished)
            initialQuery = initialQuery.whereEqualTo("furnishingType", "Semi Furnished");
        if (isFilterFullyfurnished)
            initialQuery = initialQuery.whereEqualTo("furnishingType", "Fully Furnished");

        if (isSortRentAsc)
            initialQuery = initialQuery.orderBy("expectedRent", Query.Direction.ASCENDING);
        if (isSortRentDesc)
            initialQuery = initialQuery.orderBy("expectedRent", Query.Direction.DESCENDING);
        if (isSortSizeAsc)
            initialQuery = initialQuery.orderBy("propertySize", Query.Direction.ASCENDING);
        if (isSortSizeDesc)
            initialQuery = initialQuery.orderBy("propertySize", Query.Direction.DESCENDING);

        return initialQuery;
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