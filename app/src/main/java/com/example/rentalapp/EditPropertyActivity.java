package com.example.rentalapp;

import static android.content.ContentValues.TAG;

import static com.example.rentalapp.Utils.MAX_IMAGE_SIZE;
import static com.example.rentalapp.Utils.PROPERTY_PARCEL_KEY;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.rentalapp.ml.RentPredictionModel;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class EditPropertyActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, OnCameraIdleListener {

    PropertyDataClass oldPropertyInfo, newPropertyInfo;
    CardView s1card, s2card, s3card, s4card, s5card;
    Button s1Next, s2Back, s2Next, s3Back, s3Next, s4Back, s4Next, s5Back, s5Next;
    TextInputEditText apartmentNameInput, propertySizeInput, bathroomCountInput, floorInput, totalFloorsInput, possessionInput, expectedRentInput, expectedDepositInput;
    TextView rentPredictionValue;
    GoogleMap gMap;
    AutocompleteSupportFragment placesAutocompleteInput;
    ImageView rentPredictionInfo, photosInput;
    String locality;
    Double latitude, longitude;
    Uri imageUri;
    ChipGroup bhkTypeInput, propertyAgeInput, waterSupplierInput, furnishingTypeInput, tenantPreferenceInput, parkingInput;
    Chip chip1rk, chip1bhk, chip2bhk, chip3bhk, chip4bhk;
    Chip chip0to1, chip1to3, chip3to5, chip5to10, chipGt10;
    Chip chipBorewell, chipCorporation, chipWaterBoth;
    Chip chipUnfurnished, chipSemifurninshed, chipFullyfurnished;
    Chip chipBachelor, chipFamily, chipCompany, chipAllTenants;
    Chip chipNoParking, chipBike, chipCar, chipBikeAndCar;
    RadioGroup securityInput;
    RadioButton securityYes, securityNo;
    SimpleDateFormat possessionDateFormat;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_property);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editPropertyActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Edit your Property");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        oldPropertyInfo = getIntent().getParcelableExtra(PROPERTY_PARCEL_KEY);
        assert oldPropertyInfo != null;
        newPropertyInfo = new PropertyDataClass(oldPropertyInfo);

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
        chip1rk = findViewById(R.id.chip_1rk);
        chip1bhk = findViewById(R.id.chip_1bhk);
        chip2bhk = findViewById(R.id.chip_2bhk);
        chip3bhk = findViewById(R.id.chip_3bhk);
        chip4bhk = findViewById(R.id.chip_4bhk);

        propertySizeInput = findViewById(R.id.property_size_input);
        floorInput = findViewById(R.id.floor_input);
        totalFloorsInput = findViewById(R.id.total_floors_input);

        propertyAgeInput = findViewById(R.id.property_age_input);
        chip0to1 = findViewById(R.id.chip_0to1);
        chip1to3 = findViewById(R.id.chip_1to3);
        chip3to5 = findViewById(R.id.chip_3to5);
        chip5to10 = findViewById(R.id.chip_5to10);
        chipGt10 = findViewById(R.id.chip_gt10);

        placesAutocompleteInput = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        bathroomCountInput = findViewById(R.id.bathroom_count_input);

        waterSupplierInput = findViewById(R.id.water_supplier_input);
        chipBorewell = findViewById(R.id.chip_borewell);
        chipCorporation = findViewById(R.id.chip_corporation);
        chipWaterBoth = findViewById(R.id.chip_both_water_suppliers);

        furnishingTypeInput = findViewById(R.id.furnishing_type_input);
        chipUnfurnished = findViewById(R.id.chip_not_furnished);
        chipSemifurninshed = findViewById(R.id.chip_semi_furnished);
        chipFullyfurnished = findViewById(R.id.chip_fully_furnished);

        tenantPreferenceInput = findViewById(R.id.tenant_preference_input);
        chipBachelor = findViewById(R.id.chip_bachelor);
        chipFamily = findViewById(R.id.chip_family);
        chipCompany = findViewById(R.id.chip_company);
        chipAllTenants = findViewById(R.id.chip_all_tenants);

        parkingInput = findViewById(R.id.parking_input);
        chipNoParking = findViewById(R.id.chip_no_parking);
        chipBike = findViewById(R.id.chip_bike);
        chipCar = findViewById(R.id.chip_car);
        chipBikeAndCar = findViewById(R.id.chip_both_parking);

        securityInput = findViewById(R.id.security_input);
        securityNo = findViewById(R.id.security_no);
        securityYes = findViewById(R.id.security_yes);

        possessionInput = findViewById(R.id.possession_input);
        possessionDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        rentPredictionValue = findViewById(R.id.rent_prediction_value);
        rentPredictionInfo = findViewById(R.id.rent_prediction_info);
        expectedRentInput = findViewById(R.id.expected_rent_input);
        expectedDepositInput = findViewById(R.id.expected_deposit_input);
        photosInput = findViewById(R.id.photos_input);

        //Set the Property Values, in the different fields
        setValuesForFields();

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
                            imageUri = data.getData();
                            try {
                                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                                // Check the size of the image
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] imageBytes = baos.toByteArray();
                                int imageSize = imageBytes.length;

                                // If the image is larger than 50 KB, compress it
                                if (imageSize > MAX_IMAGE_SIZE) {
                                    int quality = 100;
                                    while (imageSize > MAX_IMAGE_SIZE && quality > 0) {
                                        baos.reset();
                                        selectedImage.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                                        imageBytes = baos.toByteArray();
                                        imageSize = imageBytes.length;
                                        quality -= 5; // Reduce quality by 5% each iteration
                                    }
                                    selectedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                }

                                // Insert the compressed image into MediaStore and get the Uri
                                Uri compressedImageUri = insertImageIntoMediaStore(selectedImage);
                                if (compressedImageUri != null) {
                                    // Set the compressed image to the ImageView
                                    newPropertyInfo.setPhotos("");
                                    imageUri = compressedImageUri;
                                    photosInput.setImageURI(compressedImageUri);
                                }
                            } catch (FileNotFoundException e) {
                                Toast.makeText(EditPropertyActivity.this, "File not found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(EditPropertyActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
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

        placesAutocompleteInput.setPlaceFields(Arrays.asList(Place.Field.ID,
                Place.Field.NAME,
//                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
//                Place.Field.ADDRESS_COMPONENTS
        ));

        placesAutocompleteInput.setOnPlaceSelectedListener(new PlaceSelectionListener() {
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

    private void setValuesForFields() {
        Glide.with(this)
                .load(newPropertyInfo.getPhotos())
                .placeholder(R.drawable.animated_loading_spinner)
                .into(photosInput);
        apartmentNameInput.setText(newPropertyInfo.getApartmentName());
        switch (newPropertyInfo.getBhkType()) {
            case "1 RK":
                chip1rk.setChecked(true);
                break;
            case "1 BHK":
                chip1bhk.setChecked(true);
                break;
            case "2 BHK":
                chip2bhk.setChecked(true);
                break;
            case "3 BHK":
                chip3bhk.setChecked(true);
                break;
            case "4 BHK":
                chip4bhk.setChecked(true);
                break;
        }
        propertySizeInput.setText(String.valueOf(newPropertyInfo.getPropertySize()));
        floorInput.setText(String.valueOf(newPropertyInfo.getFloor()));
        totalFloorsInput.setText(String.valueOf(newPropertyInfo.getTotalFloors()));
        switch (newPropertyInfo.getPropertyAge()) {
            case "0 – 1 years":
                chip0to1.setChecked(true);
                break;
            case "1 – 3 years":
                chip1to3.setChecked(true);
                break;
            case "3 – 5 years":
                chip3to5.setChecked(true);
                break;
            case "5 – 10 years":
                chip5to10.setChecked(true);
                break;
            case "> 10 years":
                chipGt10.setChecked(true);
                break;
        }
        locality = newPropertyInfo.getLocality();
        placesAutocompleteInput.setText(newPropertyInfo.getLocality());
        bathroomCountInput.setText(String.valueOf(newPropertyInfo.getNumberOfBathrooms()));
        switch (newPropertyInfo.getWaterSupplier()) {
            case "Borewell":
                chipBorewell.setChecked(true);
                break;
            case "Corporation":
                chipCorporation.setChecked(true);
                break;
            case "Both":
                chipWaterBoth.setChecked(true);
                break;
        }
        switch (newPropertyInfo.getFurnishingType()) {
            case "Unfurnished":
                chipUnfurnished.setChecked(true);
                break;
            case "Semi Furnished":
                chipSemifurninshed.setChecked(true);
                break;
            case "Fully Furnished":
                chipFullyfurnished.setChecked(true);
                break;
        }
        switch (newPropertyInfo.getTenantPreference()) {
            case "Bachelor":
                chipBachelor.setChecked(true);
                break;
            case "Family":
                chipFamily.setChecked(true);
                break;
            case "Company":
                chipCompany.setChecked(true);
                break;
            case "All Tenants":
                chipAllTenants.setChecked(true);
                break;
        }
        switch (newPropertyInfo.getParking()) {
            case "No":
                chipNoParking.setChecked(true);
                break;
            case "Bike":
                chipBike.setChecked(true);
                break;
            case "Car":
                chipCar.setChecked(true);
                break;
            case "Bike & Car":
                chipBikeAndCar.setChecked(true);
                break;
        }
        switch (newPropertyInfo.getSecurity()) {
            case "No":
                securityNo.setChecked(true);
                break;
            case "Yes":
                securityYes.setChecked(true);
                break;
        }
        possessionInput.setText(newPropertyInfo.getPossession());
        expectedRentInput.setText(String.valueOf((int)newPropertyInfo.getExpectedRent()));
        expectedDepositInput.setText(String.valueOf((int)newPropertyInfo.getExpectedDeposit()));
    }

    private Uri insertImageIntoMediaStore(Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "compressed_image.jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            try (OutputStream outStream = getContentResolver().openOutputStream(uri)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                return uri;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
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
            newPropertyInfo.setLatitude(latitude);
            newPropertyInfo.setLongitude(longitude);
            newPropertyInfo.setLocality(locality);
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

            newPropertyInfo.setApartmentName(apartmentName);
            newPropertyInfo.setBhkType(bhkType);
            newPropertyInfo.setPropertySize(propertySize);
            newPropertyInfo.setFloor(floor);
            newPropertyInfo.setTotalFloors(totalFloors);
            newPropertyInfo.setPropertyAge(propertyAge);

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

            newPropertyInfo.setNumberOfBathrooms(numberOfBathrooms);
            newPropertyInfo.setWaterSupplier(waterSupplier);
            newPropertyInfo.setFurnishingType(furnishing);
            newPropertyInfo.setTenantPreference(tenantPreference);
            newPropertyInfo.setParking(parking);
            newPropertyInfo.setSecurity(security);
            newPropertyInfo.setPossession(possessionDate);

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

            newPropertyInfo.setExpectedRent(expectedRent);
            newPropertyInfo.setExpectedDeposit(expectedDeposit);

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
            float[] mlInput = Utils.setInputArrayFromProperty(newPropertyInfo);
            mlInput = Utils.scaleInputForModel(mlInput);
            inputFeature0.loadArray(mlInput);

            // Runs model inference and gets result.
            RentPredictionModel.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float predictedRent = outputFeature0.getFloatValue(0);

            int rentLimits[] = Utils.getRentLowerAndUpperLimit(predictedRent);

            rentPredictionValue.setText("₹" + Utils.formatToIndianCurrency(Utils.roundToNearestThousand(rentLimits[0])) + " – ₹" + Utils.formatToIndianCurrency(Utils.roundToNearestThousand(rentLimits[1])));

            // Releases model resources if no longer used.
            model.close();
            Toast.makeText(this, "Prediction Success", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, "Prediction Error", Toast.LENGTH_SHORT).show();
        }
    }


    public void saveData() {

        if (oldPropertyInfo.equals(newPropertyInfo)) {
            Toast.makeText(this, "No changes made to property", Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else {
            Calendar selectedDate = Calendar.getInstance();
            String formattedDate = possessionDateFormat.format(selectedDate.getTime());
            newPropertyInfo.setLastUpdated(formattedDate);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(EditPropertyActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.save_progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        //Check if Image is Changed, else just call updateData
        if (!oldPropertyInfo.getPhotos().equals(newPropertyInfo.getPhotos())) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Property Images")
                    .child(Objects.requireNonNull(imageUri.getLastPathSegment()));
            storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete()) ;
                    Uri urlImage = uriTask.getResult();
                    newPropertyInfo.setPhotos(urlImage.toString());
                    updateData(dialog);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    finish();
                }
            });
        } else {
            updateData(dialog);
        }
    }

    private void updateData(AlertDialog dialog) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("properties").document(oldPropertyInfo.getDocumentId());
        documentReference.set(newPropertyInfo, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditPropertyActivity.this, "Property Updated", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditPropertyActivity.this, "Failed to update property: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                    }
                });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setOnCameraIdleListener(this);
        LatLng defaultLocation = new LatLng(newPropertyInfo.getLatitude(), newPropertyInfo.getLongitude());

        //Set Default Location
        MarkerOptions markerOptions = new MarkerOptions()
                .position(defaultLocation)
                .title(newPropertyInfo.getApartmentName());
        gMap.addMarker(markerOptions);

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 16));
    }

    @Override
    public void onCameraIdle() {
        Toast.makeText(this, gMap.getCameraPosition().toString(), Toast.LENGTH_SHORT).show();
        latitude = gMap.getCameraPosition().target.latitude;
        longitude = gMap.getCameraPosition().target.longitude;
    }
}