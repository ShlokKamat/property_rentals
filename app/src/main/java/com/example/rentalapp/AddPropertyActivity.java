package com.example.rentalapp;

import static android.content.ContentValues.TAG;
import static androidx.core.util.ObjectsCompat.requireNonNull;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import com.example.rentalapp.ml.RentPredictionModel;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.index.qual.Positive;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AddPropertyActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, OnCameraIdleListener {

    CardView s1card, s2card, s3card, s4card, s5card;
    Button s1Next, s2Back, s2Next, s3Back, s3Next, s4Back, s4Next, s5Back, s5Next;
    TextInputEditText apartmentNameInput, propertySizeInput, propertyAgeInput, bathroomCountInput, floorInput, totalFloorsInput, localityInput, expectedRentInput, expectedDepositInput;
    TextView rentPredictionValue;
    GoogleMap gMap;
    ImageView photosInput;
    String photoURL;
    Double latitude, longitude;
    Uri uri;
    AutoCompleteTextView bhkTypeInput, furnishingTypeInput, parkingInput, waterSupplierInput, tenantPreferenceInput, securityInput;
    String[] bhkTypes = {"1RK", "1BHK", "2BHK", "3BHK", "4BHK"};
    String[] furnishingTypes = {"Not Furnished", "Semi Furnished", "Fully Furnished"};
    String[] parkingTypes = {"None", "Bike", "Car", "Both"};
    String[] waterSupplierTypes = {"Borewell", "Corporation", "Both"};
    String[] tenantPreferenceTypes = {"Bachelor", "Family", "Company", "All"};
    String[] securityTypes = {"No", "Yes"};

    ArrayAdapter<String> stringArrayAdapter;
    FirebaseAuth auth;
    float[] means, stds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_property);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addPropertyActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Post your Property");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent addPropertyIntent = new Intent(AddPropertyActivity.this, MainActivity.class);
//                startActivity(addPropertyIntent);
                finish();
            }
        });

        means = new float[]{970.90830822f,2.19974171f,4.11493758f,1.7572105f};
        stds = new float[]{689.26193329f,3.68153938f,4.34937374f,0.89890801f};

//        For all steps and buttons
        s1card = findViewById(R.id.step1_card);
        s2card = findViewById(R.id.step2_card);
        s3card = findViewById(R.id.step3_card);
        s4card = findViewById(R.id.step4_card);
        s5card = findViewById(R.id.step5_card);
        s1Next = findViewById(R.id.step1_save);
        s1Next.setOnClickListener(this);
        s2Back = findViewById(R.id.step2_back);
        s2Back.setOnClickListener(this);
        s2Next = findViewById(R.id.step2_save);
        s2Next.setOnClickListener(this);
        s3Back = findViewById(R.id.step3_back);
        s3Back.setOnClickListener(this);
        s3Next = findViewById(R.id.step3_save);
        s3Next.setOnClickListener(this);
        s4Back = findViewById(R.id.step4_back);
        s4Back.setOnClickListener(this);
        s4Next = findViewById(R.id.step4_save);
        s4Next.setOnClickListener(this);
        s5Back = findViewById(R.id.step5_back);
        s5Back.setOnClickListener(this);
        s5Next = findViewById(R.id.step5_save);
        s5Next.setOnClickListener(this);

//        For all EditTexts and Dropdowns
        apartmentNameInput = findViewById(R.id.apartment_name_input);
        propertySizeInput = findViewById(R.id.property_size_input);
        propertyAgeInput = findViewById(R.id.property_age_input);
        bathroomCountInput = findViewById(R.id.bathroom_count_input);
        floorInput = findViewById(R.id.floor_input);
        totalFloorsInput = findViewById(R.id.total_floors_input);
        localityInput = findViewById(R.id.locality_input);
        rentPredictionValue = findViewById(R.id.rent_prediction_value);
        expectedRentInput = findViewById(R.id.expected_rent_input);
        expectedDepositInput = findViewById(R.id.expected_deposit_input);
        photosInput = findViewById(R.id.photos_input);

        bhkTypeInput = findViewById(R.id.bhk_type_input);
        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, bhkTypes);
        bhkTypeInput.setAdapter(stringArrayAdapter);

        furnishingTypeInput = findViewById(R.id.furnishing_type_input);
        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, furnishingTypes);
        furnishingTypeInput.setAdapter(stringArrayAdapter);

        parkingInput = findViewById(R.id.parking_input);
        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, parkingTypes);
        parkingInput.setAdapter(stringArrayAdapter);

        waterSupplierInput = findViewById(R.id.water_supplier_input);
        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, waterSupplierTypes);
        waterSupplierInput.setAdapter(stringArrayAdapter);

        tenantPreferenceInput = findViewById(R.id.tenant_preference_input);
        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, tenantPreferenceTypes);
        tenantPreferenceInput.setAdapter(stringArrayAdapter);

        securityInput = findViewById(R.id.security_input);
        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, securityTypes);
        securityInput.setAdapter(stringArrayAdapter);

        bhkTypeInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
//                Toast.makeText(AddPropertyActivity.this, "BHK: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        furnishingTypeInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
//                Toast.makeText(AddPropertyActivity.this, "Furnish Type: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == Activity.RESULT_OK) {
                            Intent data = o.getData();
                            assert data != null;
                            uri = data.getData();
                            photosInput.setImageURI(uri);
                        } else {
                            Toast.makeText(AddPropertyActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        photosInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.GOOGLE_MAPS_PLACES_API_KEY);
        }

// Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,
                Place.Field.NAME,
//                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
//                Place.Field.ADDRESS_COMPONENTS
        ));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place);
                LatLng latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
//                gMap.addMarker(new MarkerOptions()
//                        .position(latLng)
//                        .title(place.getName()));
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.step1_save) {
            s1card.setVisibility(View.GONE);
            s2card.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.step2_back) {
            s2card.setVisibility(View.GONE);
            s1card.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.step2_save) {
            s2card.setVisibility(View.GONE);
            s3card.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.step3_back) {
            s3card.setVisibility(View.GONE);
            s2card.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.step3_save) {
            predictRentForProperty();
            s3card.setVisibility(View.GONE);
            s4card.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.step4_back) {
            s4card.setVisibility(View.GONE);
            s3card.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.step4_save) {
            s4card.setVisibility(View.GONE);
            s5card.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.step5_back) {
            s5card.setVisibility(View.GONE);
            s4card.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.step5_save) {
            saveData();
        }
    }

    private void predictRentForProperty() {
        try {
            RentPredictionModel model = RentPredictionModel.newInstance(this);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 9}, DataType.FLOAT32);
            float[] inputArray = new float[]{

                    (float) 250, //Size
                    (float) 4, //Floor
                    (float) 4, //Total Floor
                    (float) 1, //Bathrooms

                    (float) 1, //BHK
                    (float) 2, //Age
                    (float) 1, //Furnishing
                    (float) 1, //Parking
                    (float) 0  //Security
            };
            inputArray = scaleInputForModel(inputArray);
            inputFeature0.loadArray(inputArray);
//            inputFeature0.loadBuffer(inputArray);

            // Runs model inference and gets result.
            RentPredictionModel.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float predictedRent = outputFeature0.getFloatValue(0);


            rentPredictionValue.setText("Rs."+predictedRent);

            // Releases model resources if no longer used.
            model.close();
            Toast.makeText(this, "Prediction Success", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, "Prediction Error", Toast.LENGTH_SHORT).show();
            // TODO Handle the exception
        }
    }

    private float[] scaleInputForModel(float[] inputArray) {
        float[] scaledInput = new float[inputArray.length];
        for (int i = 0; i < inputArray.length; i++) {
            if(i<4){
                scaledInput[i] = (inputArray[i] - means[i]) / stds[i];
            } else {
                scaledInput[i] = inputArray[i];
            }
        }
        return scaledInput;
    }

    public void saveData() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                .child(Objects.requireNonNull(uri.getLastPathSegment()));

        AlertDialog.Builder builder = new AlertDialog.Builder(AddPropertyActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.save_progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                Uri urlImage = uriTask.getResult();
                photoURL = urlImage.toString();
                uploadData();
                dialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    public void uploadData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("properties").document();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        assert user != null;
        PropertyDataClass propertyData = new PropertyDataClass(
                documentReference.getId(),
                "possessionDate", //Get Value from User HERE
                user.getEmail(),
                Objects.requireNonNull(apartmentNameInput.getText()).toString(),
                bhkTypeInput.getText().toString(),
                Double.parseDouble(requireNonNull(propertySizeInput.getText()).toString()),
                Integer.parseInt(requireNonNull(propertyAgeInput.getText()).toString()),
                Integer.parseInt(requireNonNull(floorInput.getText()).toString()),
                Integer.parseInt(requireNonNull(totalFloorsInput.getText()).toString()),
                Integer.parseInt(requireNonNull("1")),//numberOfBathrooms
                waterSupplierInput.getText().toString(),
                parkingInput.getText().toString(),
                securityInput.getText().toString(),
                tenantPreferenceInput.getText().toString(),
                Objects.requireNonNull(localityInput.getText()).toString(),
                latitude,
                longitude,
                Double.parseDouble(requireNonNull(expectedRentInput.getText()).toString()),
                Double.parseDouble(requireNonNull(expectedDepositInput.getText()).toString()),
                furnishingTypeInput.getText().toString(),
                photoURL);

        documentReference.set(propertyData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(AddPropertyActivity.this, "Property Posted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddPropertyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        db.collection("properties").document(Objects.requireNonNull(apartmentNameInput.getText()).toString())
//                .set(propertyData)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(AddPropertyActivity.this, "SUCK-SESS", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(AddPropertyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });


//        FirebaseDatabase.getInstance().getReference("Properties Datas").child(Objects.requireNonNull(apartmentNameInput.getText()).toString())
//                .setValue(propertyData).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(AddPropertyActivity.this, "Saved", Toast.LENGTH_SHORT).show();
////                            finish();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(AddPropertyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setOnCameraIdleListener(this);
//        North Latitude Positive
//        South Latitude Negative
//        East Longitude Positive
//        West Longitude Negative
        LatLng defaultLocation = new LatLng(12.9716, 77.5946);


//        MarkerOptions markerOptions = new MarkerOptions()
//                .position(defaultLocation)
//                .title("Bengaluru");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//        gMap.addMarker(markerOptions);

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 11));
    }

    @Override
    public void onCameraIdle() {
        Toast.makeText(this, gMap.getCameraPosition().toString(), Toast.LENGTH_SHORT).show();
        latitude = gMap.getCameraPosition().target.latitude;
        longitude = gMap.getCameraPosition().target.longitude;
        //You will get LAT LONG here, upon click, get the location info
    }
}