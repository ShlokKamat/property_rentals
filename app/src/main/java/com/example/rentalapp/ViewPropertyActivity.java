package com.example.rentalapp;

import static com.example.rentalapp.Utils.PROPERTY_PARCEL_KEY;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.rentalapp.ml.RentPredictionModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.marcinmoskala.arcseekbar.ArcSeekBar;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

public class ViewPropertyActivity extends AppCompatActivity {
    PropertyDataClass propertyInfo;
    boolean isPropertyEdited;
    private String possession;
    private String lastUpdated;
    private double latitude;
    private double longitude;
    TextView propertyViewHeader, localityView, propertySizeView, expectedRentView, bedroomView, bathroomView,
            floorView, furnishingView, parkingView, securityView, waterSupplierView, tenantPreferenceView,
            propertyAgeView, expectedDepositView, possessionView, lastUpdatedView;
    ImageView photosView;
    TextView rentLowerLimit, rentUpperLimit, rentEstimationInfo;
    ArcSeekBar arcSeekBar;
    LinearLayout ownerActions, tenantActions;
    Button editProperty, deleteProperty, contactOwner;
    FloatingActionButton locationMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_property);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewPropertyActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ownerActions = findViewById(R.id.owner_actions);
        tenantActions = findViewById(R.id.tenant_actions);

        propertyInfo = getIntent().getParcelableExtra(PROPERTY_PARCEL_KEY);
        isPropertyEdited = false;

        assert propertyInfo != null;
        if (propertyInfo.getPostedBy().equals(Utils.getUserEmail())) {
            tenantActions.setVisibility(View.GONE);
        } else {
            ownerActions.setVisibility(View.GONE);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("View Property");

        photosView = findViewById(R.id.photos_view);
        propertyViewHeader = findViewById(R.id.property_view_header);
        localityView = findViewById(R.id.locality_view);
        propertySizeView = findViewById(R.id.property_size_view);
        expectedRentView = findViewById(R.id.expected_rent_view);
        bedroomView = findViewById(R.id.bedroom_view);
        bathroomView = findViewById(R.id.bathroom_view);
        floorView = findViewById(R.id.floor_view);
        furnishingView = findViewById(R.id.furnishing_view);
        parkingView = findViewById(R.id.parking_view);
        securityView = findViewById(R.id.security_view);
        waterSupplierView = findViewById(R.id.water_supplier_view);
        tenantPreferenceView = findViewById(R.id.tenant_preference_view);
        propertyAgeView = findViewById(R.id.property_age_view);
        expectedDepositView = findViewById(R.id.expected_deposit_view);
        possessionView = findViewById(R.id.possession_view);
        lastUpdatedView = findViewById(R.id.last_updated_view);

        locationMapView = findViewById(R.id.location_map_view);

        arcSeekBar = findViewById(R.id.seekArc);
        int[] intArray = getResources().getIntArray(R.array.progressGradientColors);
        arcSeekBar.setProgressBackgroundGradient(intArray);
        rentLowerLimit = findViewById(R.id.rent_lower_limit);
        rentUpperLimit = findViewById(R.id.rent_upper_limit);
        rentEstimationInfo = findViewById(R.id.rent_estimation_info);

        editProperty = findViewById(R.id.edit_property);
        deleteProperty = findViewById(R.id.delete_property);
        contactOwner = findViewById(R.id.contact_property_owner);

        setValueInFields();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        contactOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{propertyInfo.getPostedBy()});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, Utils.emailSubject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, Utils.getEmailBody(propertyInfo.getApartmentName(), propertyInfo.getBhkType(), propertyInfo.getLocality(), Utils.getUserName()));
                emailIntent.setType("message/rfc822");
                try {
                    startActivity(Intent.createChooser(emailIntent, Utils.chooseEmailTitle));
                } catch (Exception e) {
                    Toast.makeText(ViewPropertyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        editProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPropertyEdited = true;
                Intent editPropertyIntent = new Intent(ViewPropertyActivity.this, EditPropertyActivity.class);
                editPropertyIntent.putExtra(PROPERTY_PARCEL_KEY, propertyInfo);
                startActivity(editPropertyIntent);
            }
        });

        deleteProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                AlertDialog.Builder builder = new AlertDialog.Builder(ViewPropertyActivity.this);
                                builder.setCancelable(false);
                                builder.setView(R.layout.delete_progress_layout);
                                AlertDialog spinnerDialog = builder.create();
                                spinnerDialog.show();

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference documentReference = db.collection("properties").document(propertyInfo.getDocumentId());
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(ViewPropertyActivity.this, "Property Deleted", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ViewPropertyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                // Code for Negative scenario
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewPropertyActivity.this);
                builder.setTitle("Delete Property?")
                        .setMessage("You will not be able to recover the property")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show();

            }
        });

        locationMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the latitude and longitude
                double latitude = propertyInfo.getLatitude();
                double longitude = propertyInfo.getLongitude();

                String label = propertyInfo.getApartmentName();

// Create a Uri from an intent string. Use the result to create an Intent.
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)", latitude, longitude, latitude, longitude, label);
                Uri gmmIntentUri = Uri.parse(uri);

// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

// Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

// Attempt to start an activity that can handle the Intent
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
    }

    private void setValueInFields() {
        if (!propertyInfo.getPhotos().isEmpty()) {
            Glide.with(this)
                    .load(propertyInfo.getPhotos())
                    .placeholder(R.drawable.animated_loading_spinner)
                    .thumbnail(0.05f)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(photosView);
        } else {
            photosView.setImageResource(R.drawable.no_house_photo);
        }
        propertyViewHeader.setText(propertyInfo.getBhkType() + " in " + propertyInfo.getApartmentName());
        localityView.setText(propertyInfo.getLocality());
        propertySizeView.setText(propertyInfo.getPropertySize() + " sq ft");
        expectedRentView.setText("₹" + Utils.formatToIndianCurrency(propertyInfo.getExpectedRent()) + "\nper month");
        bedroomView.setText(propertyInfo.getBhkType().split(" ")[0] + "\nBedroom");
        bathroomView.setText(propertyInfo.getNumberOfBathrooms() + "\nBathroom");
        floorView.setText(propertyInfo.getFloor() + " out of " + propertyInfo.getTotalFloors() + "\nFloor");
        furnishingView.setText(propertyInfo.getFurnishingType() + "\nFurnishing");
        parkingView.setText(propertyInfo.getParking() + "\nParking");
        securityView.setText("Security\n" + propertyInfo.getSecurity());
        if (propertyInfo.getWaterSupplier().equals("Both")) {
            waterSupplierView.setText("Borewell & Corporation Water");
        } else {
            waterSupplierView.setText(propertyInfo.getWaterSupplier() + "\nWater Supply");
        }
        tenantPreferenceView.setText("Tenant Preference\n" + propertyInfo.getTenantPreference());
        propertyAgeView.setText("Property Age\n" + propertyInfo.getPropertyAge());
        expectedDepositView.setText("Expected Deposit\n₹" + Utils.formatToIndianCurrency(propertyInfo.getExpectedDeposit()));
        possessionView.setText("Possession Date\n" + propertyInfo.getPossession());
        lastUpdatedView.setText("Last Updated\n" + propertyInfo.getLastUpdated());
        predictRent();
    }

    private void predictRent() {
        try {
            RentPredictionModel model = RentPredictionModel.newInstance(this);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 9}, DataType.FLOAT32);
            float[] mlInput = Utils.setInputArrayFromProperty(propertyInfo);
            mlInput = Utils.scaleInputForModel(mlInput);
            inputFeature0.loadArray(mlInput);

            // Runs model inference and gets result.
            RentPredictionModel.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float predictedRent = outputFeature0.getFloatValue(0);

            int rentLimits[] = Utils.getRentLowerAndUpperLimit(predictedRent);

            int currentRentPercentage = (int) Utils.calculatePercentage(Utils.roundToNearestThousand(rentLimits[0]),Utils.roundToNearestThousand(rentLimits[1]),propertyInfo.getExpectedRent());
            arcSeekBar.setProgress(currentRentPercentage);

            rentLowerLimit.setText("₹"+ Utils.formatToIndianCurrency(Utils.roundToNearestThousand(rentLimits[0])));
            rentUpperLimit.setText("₹"+ Utils.formatToIndianCurrency(Utils.roundToNearestThousand(rentLimits[1])));

            rentEstimationInfo.setText("The rent is ₹" + Utils.formatToIndianCurrency((int) propertyInfo.getExpectedRent()) + ". ");

            switch (currentRentPercentage / 10) {
                case 0: case 1: case 2:
                    rentEstimationInfo.setText(rentEstimationInfo.getText().toString() + getResources().getString(R.string.owner_rent_low_analysis));
                    break;
                case 3: case 4: case 5:
                    rentEstimationInfo.setText(rentEstimationInfo.getText().toString() + getResources().getString(R.string.owner_rent_medium_analysis));
                    break;
                case 6: case 7: case 8:
                    rentEstimationInfo.setText(rentEstimationInfo.getText().toString() + getResources().getString(R.string.owner_rent_high_analysis));
                    break;
                case 9: case 10:
                    rentEstimationInfo.setText(rentEstimationInfo.getText().toString() + getResources().getString(R.string.owner_rent_abysmal_analysis));
                    break;
                default:
                    rentEstimationInfo.setText("Issue with Rent Estimation Analysis");
            }

            // Releases model resources if no longer used.
            model.close();
            Toast.makeText(this, "Estimation Success", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, "Estimation Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPropertyEdited) {
            isPropertyEdited = false;
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference documentReference = db.collection("properties").document(propertyInfo.getDocumentId());
            documentReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                propertyInfo = documentSnapshot.toObject(PropertyDataClass.class);
                                if (propertyInfo != null) {
                                    setValueInFields();
                                    Toast.makeText(ViewPropertyActivity.this, "Property Data Refreshed", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ViewPropertyActivity.this, "Property Not Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ViewPropertyActivity.this, "Error in refreshing the property", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}