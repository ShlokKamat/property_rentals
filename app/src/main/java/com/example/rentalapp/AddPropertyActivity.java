package com.example.rentalapp;

import static android.content.ContentValues.TAG;
import static androidx.core.util.ObjectsCompat.requireNonNull;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.rentalapp.ml.RentPredictionModel;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AddPropertyActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, OnCameraIdleListener {

    CardView s1card, s2card, s3card, s4card, s5card;
    Button s1Next, s2Back, s2Next, s3Back, s3Next, s4Back, s4Next, s5Back, s5Next;
    TextInputEditText apartmentNameInput, propertySizeInput, bathroomCountInput, floorInput, totalFloorsInput, possessionInput, expectedRentInput, expectedDepositInput;
    TextView rentPredictionValue;
    GoogleMap gMap;
    ImageView rentPredictionInfo, photosInput;
    String locality;
    Double latitude, longitude;
    Uri uri;
    PropertyDataClass propertyData;
    ChipGroup bhkTypeInput, propertyAgeInput, waterSupplierInput, furnishingTypeInput, tenantPreferenceInput, parkingInput;
    //    AutoCompleteTextView furnishingTypeInput, parkingInput, waterSupplierInput, tenantPreferenceInput, securityInput;
    RadioGroup securityInput;
    SimpleDateFormat possessionDateFormat;
    String[] bhkTypes = {"1RK", "1BHK", "2BHK", "3BHK", "4BHK"};
    String[] furnishingTypes = {"Not Furnished", "Semi Furnished", "Fully Furnished"};
    String[] parkingTypes = {"None", "Bike", "Car", "Both"};
    String[] waterSupplierTypes = {"Borewell", "Corporation", "Both"};
    String[] tenantPreferenceTypes = {"Bachelor", "Family", "Company", "All"};
    String[] securityTypes = {"No", "Yes"};

    ArrayAdapter<String> stringArrayAdapter;
    FirebaseAuth auth;

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
        bhkTypeInput = findViewById(R.id.bhk_type_input);
        propertySizeInput = findViewById(R.id.property_size_input);
        floorInput = findViewById(R.id.floor_input);
        totalFloorsInput = findViewById(R.id.total_floors_input);
        propertyAgeInput = findViewById(R.id.property_age_input);
        locality = "Bengaluru";
        bathroomCountInput = findViewById(R.id.bathroom_count_input);
        waterSupplierInput = findViewById(R.id.water_supplier_input);
        furnishingTypeInput = findViewById(R.id.furnishing_type_input);
        tenantPreferenceInput = findViewById(R.id.tenant_preference_input);
        parkingInput = findViewById(R.id.parking_input);
        securityInput = findViewById(R.id.security_input);
        possessionInput = findViewById(R.id.possession_input);
        possessionDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        rentPredictionValue = findViewById(R.id.rent_prediction_value);
        rentPredictionInfo = findViewById(R.id.rent_prediction_info);
        expectedRentInput = findViewById(R.id.expected_rent_input);
        expectedDepositInput = findViewById(R.id.expected_deposit_input);
        photosInput = findViewById(R.id.photos_input);

        propertyData = new PropertyDataClass();

//        bhkTypeInput = findViewById(R.id.bhk_type_input);
//        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, bhkTypes);
//        bhkTypeInput.setAdapter(stringArrayAdapter);

//        furnishingTypeInput = findViewById(R.id.furnishing_type_input);
//        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, furnishingTypes);
//        furnishingTypeInput.setAdapter(stringArrayAdapter);
//
//        parkingInput = findViewById(R.id.parking_input);
//        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, parkingTypes);
//        parkingInput.setAdapter(stringArrayAdapter);

//        waterSupplierInput = findViewById(R.id.water_supplier_input);
//        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, waterSupplierTypes);
//        waterSupplierInput.setAdapter(stringArrayAdapter);

//        tenantPreferenceInput = findViewById(R.id.tenant_preference_input);
//        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, tenantPreferenceTypes);
//        tenantPreferenceInput.setAdapter(stringArrayAdapter);

//        securityInput = findViewById(R.id.security_input);
//        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, securityTypes);
//        securityInput.setAdapter(stringArrayAdapter);

//        bhkTypeInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String item = parent.getItemAtPosition(position).toString();
////                Toast.makeText(AddPropertyActivity.this, "BHK: " + item, Toast.LENGTH_SHORT).show();
//            }
//        });
        possessionInput.setOnClickListener(v -> {
            possessionInput.setError(null);
            // Get current date
            final Calendar c = Calendar.getInstance();
            int year, month, day;

            // Check if EditText has a valid date
            String dateString = possessionInput.getText().toString();
            if (!dateString.isEmpty()) {
                try {
                    c.setTime(possessionDateFormat.parse(dateString));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            // Create DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // Set the selected date into the EditText
                            Calendar selectedDate = Calendar.getInstance();
                            selectedDate.set(year, monthOfYear, dayOfMonth);
                            String formattedDate = possessionDateFormat.format(selectedDate.getTime());
                            possessionInput.setText(formattedDate);
                        }
                    }, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        rentPredictionInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar.make(v, "Set Expected Rent within Predicted Range to maximise post response", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
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
                locality = place.getName();
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.step1_save) {
//            Toast.makeText(this, "Step 1 Validations Skipped", Toast.LENGTH_SHORT).show();
            if (isStep1InputValid()) {
                s1card.setVisibility(View.GONE);
                s2card.setVisibility(View.VISIBLE);
            }
        } else if (viewId == R.id.step2_back) {
            s2card.setVisibility(View.GONE);
            s1card.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.step2_save) {
            propertyData.setLatitude(latitude);
            propertyData.setLongitude(longitude);
            propertyData.setLocality(locality);
            s2card.setVisibility(View.GONE);
            s3card.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.step3_back) {
            s3card.setVisibility(View.GONE);
            s2card.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.step3_save) {
//            Toast.makeText(this, "Step 3 Validations Skipped", Toast.LENGTH_SHORT).show();
            if (isStep3InputValid()) {
                predictRent();
                s3card.setVisibility(View.GONE);
                s4card.setVisibility(View.VISIBLE);
            }
        } else if (viewId == R.id.step4_back) {
            s4card.setVisibility(View.GONE);
            s3card.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.step4_save) {
//                Toast.makeText(this, "Step 4 Validations Skipped", Toast.LENGTH_SHORT).show();
            if (isStep4InputValid()) {
                s4card.setVisibility(View.GONE);
                s5card.setVisibility(View.VISIBLE);
            }
        } else if (viewId == R.id.step5_back) {
            s5card.setVisibility(View.GONE);
            s4card.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.step5_save) {
            saveData();
        }
    }

    private boolean isStep1InputValid() {
        try {
            int selectedChipId;
            String apartmentName = Objects.requireNonNull(apartmentNameInput.getText()).toString();
            String bhkType = "";
            int propertySize = 0;
            int floor = 0;
            int totalFloors = 0;
            String propertyAge = "";

            //Apartment Name Input
            if (apartmentName.isEmpty()) {
                apartmentNameInput.setError("Apartment Name cannot be empty");
                return false;
            }

            //BHK Type Input
            selectedChipId = bhkTypeInput.getCheckedChipId();
            if (selectedChipId != View.NO_ID) {
                Chip selectedChip = findViewById(selectedChipId);
                bhkType = selectedChip.getText().toString();
            } else {
                Toast.makeText(this, "Please select BHK Option", Toast.LENGTH_SHORT).show();
                return false;
            }

            //Property Size Input
            if (Objects.requireNonNull(propertySizeInput.getText()).toString().isEmpty() || Integer.parseInt(Objects.requireNonNull(propertySizeInput.getText()).toString()) < 1 || Integer.parseInt(Objects.requireNonNull(propertySizeInput.getText()).toString()) > 5000) {
                propertySizeInput.setError("Property Size must be >0 & <5000");
                return false;
            } else {
                propertySize = Integer.parseInt(Objects.requireNonNull(propertySizeInput.getText()).toString());
            }

            //Floors Input
            if (Objects.requireNonNull(floorInput.getText()).toString().isEmpty()) {
                floorInput.setError("Floor must be >=0");
                return false;
            }
            if (Objects.requireNonNull(totalFloorsInput.getText()).toString().isEmpty() || Integer.parseInt(Objects.requireNonNull(totalFloorsInput.getText()).toString()) < Integer.parseInt(Objects.requireNonNull(floorInput.getText()).toString())) {
                totalFloorsInput.setError("Total Floor must be >= Floor");
                return false;
            }
            floor = Integer.parseInt(Objects.requireNonNull(floorInput.getText()).toString());
            totalFloors = Integer.parseInt(Objects.requireNonNull(totalFloorsInput.getText()).toString());

            //Property Age Input
            selectedChipId = propertyAgeInput.getCheckedChipId();
            if (selectedChipId != View.NO_ID) {
                Chip selectedChip = findViewById(selectedChipId);
                propertyAge = selectedChip.getText().toString() + " years";
            } else {
                Toast.makeText(this, "Please select Property Age Option", Toast.LENGTH_SHORT).show();
                return false;
            }

            propertyData.setApartmentName(apartmentName);
            propertyData.setBhkType(bhkType);
            propertyData.setPropertySize(propertySize);
            propertyData.setFloor(floor);
            propertyData.setTotalFloors(totalFloors);
            propertyData.setPropertyAge(propertyAge);

        } catch (NullPointerException | NumberFormatException e) {
            return false;
        }

        return true;
    }

    private boolean isStep3InputValid() {
        try {
            int selectedChipId;
            int selectedRadioButtonId;
            int numberOfBathrooms = 0;
            String waterSupplier = "";
            String furnishing = "";
            String tenantPreference = "";
            String parking = "";
            String security = "";
            String possessionDate = "";

            //No. of Bathrooms Input
            if (Objects.requireNonNull(bathroomCountInput.getText()).toString().isEmpty() || Integer.parseInt(Objects.requireNonNull(bathroomCountInput.getText()).toString()) < 1) {
                bathroomCountInput.setError("Must have at least 1 bathroom");
                return false;
            }
            numberOfBathrooms = Integer.parseInt(Objects.requireNonNull(bathroomCountInput.getText()).toString());


            //Water Supplier Input
            selectedChipId = waterSupplierInput.getCheckedChipId();
            if (selectedChipId != View.NO_ID) {
                Chip selectedChip = findViewById(selectedChipId);
                waterSupplier = selectedChip.getText().toString();
            } else {
                Toast.makeText(this, "Please select Water Supplier", Toast.LENGTH_SHORT).show();
                return false;
            }

            //Furnishing Input
            selectedChipId = furnishingTypeInput.getCheckedChipId();
            if (selectedChipId != View.NO_ID) {
                Chip selectedChip = findViewById(selectedChipId);
                furnishing = selectedChip.getText().toString();
                if (furnishing.equals("None")) {
                    furnishing = "Unfurnished";
                }
            } else {
                Toast.makeText(this, "Please select Furnishing", Toast.LENGTH_SHORT).show();
                return false;
            }

            //Tenant Input
            selectedChipId = tenantPreferenceInput.getCheckedChipId();
            if (selectedChipId != View.NO_ID) {
                Chip selectedChip = findViewById(selectedChipId);
                tenantPreference = selectedChip.getText().toString();
//                if (tenantPreference.equals("All Tenants")) {
//                    tenantPreference = "All";
//                }
            } else {
                Toast.makeText(this, "Please select Tenant Preference", Toast.LENGTH_SHORT).show();
                return false;
            }

            //Parking Input
            selectedChipId = parkingInput.getCheckedChipId();
            if (selectedChipId != View.NO_ID) {
                Chip selectedChip = findViewById(selectedChipId);
                parking = selectedChip.getText().toString();
            } else {
                Toast.makeText(this, "Please select Parking Availability", Toast.LENGTH_SHORT).show();
                return false;
            }

            //Security Input
            selectedRadioButtonId = securityInput.getCheckedRadioButtonId();
            if (selectedRadioButtonId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                security = selectedRadioButton.getText().toString();
            } else {
                // Show a message if no option is selected
                Toast.makeText(this, "Please select Property Security Option", Toast.LENGTH_SHORT).show();
                return false;
            }

            //Possession Date Input
            possessionDate = Objects.requireNonNull(possessionInput.getText()).toString();
            if (possessionDate.isEmpty()) {
                Toast.makeText(this, "Please select Possession Date", Toast.LENGTH_SHORT).show();
                possessionInput.setError("Please select Possession Date");
                return false;
            }

            propertyData.setNumberOfBathrooms(numberOfBathrooms);
            propertyData.setWaterSupplier(waterSupplier);
            propertyData.setFurnishingType(furnishing);
            propertyData.setTenantPreference(tenantPreference);
            propertyData.setParking(parking);
            propertyData.setSecurity(security);
            propertyData.setPossession(possessionDate);

        } catch (NullPointerException | NumberFormatException e) {
            return false;
        }

        return true;
    }

    private boolean isStep4InputValid() {
        try {
            double expectedRent = 0;
            double expectedDeposit = 0;

            //Property Size Input
            if (Objects.requireNonNull(expectedRentInput.getText()).toString().isEmpty()) {
                expectedRentInput.setError("Expected Rent cannot be empty");
                return false;
            }
            if (Objects.requireNonNull(expectedDepositInput.getText()).toString().isEmpty()) {
                expectedDepositInput.setError("Expected Deposit cannot be empty");
                return false;
            }
            expectedRent = Double.parseDouble(Objects.requireNonNull(expectedRentInput.getText()).toString());
            expectedDeposit = Double.parseDouble(Objects.requireNonNull(expectedDepositInput.getText()).toString());

            propertyData.setExpectedRent(expectedRent);
            propertyData.setExpectedDeposit(expectedDeposit);

        } catch (NullPointerException | NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void predictRent() {
        try {
            RentPredictionModel model = RentPredictionModel.newInstance(this);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 9}, DataType.FLOAT32);
            float[] mlInput = Utils.setInputArrayFromProperty(propertyData);
//            float[] inputArray = new float[]{
//                    (float) 250, //Size
//                    (float) 4, //Floor
//                    (float) 4, //Total Floor
//                    (float) 1, //Bathrooms
//                    (float) 1, //BHK
//                    (float) 2, //Age
//                    (float) 1, //Furnishing
//                    (float) 1, //Parking
//                    (float) 0  //Security
//            };
            mlInput = Utils.scaleInputForModel(mlInput);
            inputFeature0.loadArray(mlInput);

            // Runs model inference and gets result.
            RentPredictionModel.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float predictedRent = outputFeature0.getFloatValue(0);

            rentPredictionValue.setText("Rs." + predictedRent);

            // Releases model resources if no longer used.
            model.close();
            Toast.makeText(this, "Prediction Success", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, "Prediction Error", Toast.LENGTH_SHORT).show();
        }
    }


    public void saveData() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Property Images")
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
                propertyData.setPhotos(urlImage.toString());
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
        propertyData.setDocumentId(documentReference.getId());
        propertyData.setPostedBy(user.getEmail());

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