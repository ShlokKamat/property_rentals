package com.example.rentalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentalapp.ml.RentPredictionModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.marcinmoskala.arcseekbar.ArcSeekBar;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class RentEstimationActivity extends AppCompatActivity {

    PropertyDataClass propertyData;
    CardView estimationInputCard, estimationOutputCard;
    ChipGroup bhkTypeInput, propertyAgeInput, waterSupplierInput, furnishingTypeInput, tenantPreferenceInput, parkingInput;
    TextInputEditText propertySizeInput, floorInput, totalFloorsInput, bathroomCountInput, currentRentInput;
    RadioGroup securityInput;
    Button estimateRentButton;
    TextView rentLowerLimit, rentUpperLimit, rentEstimationInfo;
    ArcSeekBar arcSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_estimation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Rent Estimation");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        propertyData = new PropertyDataClass();

        estimationInputCard = findViewById(R.id.estimation_input_card);
        estimationOutputCard = findViewById(R.id.estimation_output_card);

        bhkTypeInput = findViewById(R.id.bhk_type_input);
        propertySizeInput = findViewById(R.id.property_size_input);
        floorInput = findViewById(R.id.floor_input);
        totalFloorsInput = findViewById(R.id.total_floors_input);
        propertyAgeInput = findViewById(R.id.property_age_input);
        bathroomCountInput = findViewById(R.id.bathroom_count_input);
        waterSupplierInput = findViewById(R.id.water_supplier_input);
        furnishingTypeInput = findViewById(R.id.furnishing_type_input);
        tenantPreferenceInput = findViewById(R.id.tenant_preference_input);
        parkingInput = findViewById(R.id.parking_input);
        securityInput = findViewById(R.id.security_input);
        currentRentInput = findViewById(R.id.current_rent_input);

        estimateRentButton = findViewById(R.id.estimate_rent_button);
        arcSeekBar = findViewById(R.id.seekArc);
        int[] intArray = getResources().getIntArray(R.array.progressGradientColors);
        arcSeekBar.setProgressBackgroundGradient(intArray);
        rentLowerLimit = findViewById(R.id.rent_lower_limit);
        rentUpperLimit = findViewById(R.id.rent_upper_limit);
        rentEstimationInfo = findViewById(R.id.rent_estimation_info);

        estimateRentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid()) {
                    predictRent();
                    estimationOutputCard.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(RentEstimationActivity.this, "Please check your inputs", Toast.LENGTH_LONG).show();
                    estimationOutputCard.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean isInputValid() {
        try {
            int selectedChipId;
            String bhkType = "";
            int propertySize = 0;
            int floor = 0;
            int totalFloors = 0;
            String propertyAge = "";
            int numberOfBathrooms = 0;
            String waterSupplier = "";
            String furnishing = "";
            String tenantPreference = "";
            String parking = "";
            int selectedRadioButtonId;
            String security = "";
            int currentRent = 0;

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

            //Current Rent Input
            if (Objects.requireNonNull(currentRentInput.getText()).toString().isEmpty() || Integer.parseInt(Objects.requireNonNull(currentRentInput.getText()).toString()) < 1 || Integer.parseInt(Objects.requireNonNull(currentRentInput.getText()).toString()) > 1000000) {
                currentRentInput.setError("Property Rent must be >0 & <1000000");
                return false;
            } else {
                currentRent = Integer.parseInt(Objects.requireNonNull(currentRentInput.getText()).toString());
            }

            propertyData.setBhkType(bhkType);
            propertyData.setPropertySize(propertySize);
            propertyData.setFloor(floor);
            propertyData.setTotalFloors(totalFloors);
            propertyData.setPropertyAge(propertyAge);
            propertyData.setNumberOfBathrooms(numberOfBathrooms);
            propertyData.setWaterSupplier(waterSupplier);
            propertyData.setFurnishingType(furnishing);
            propertyData.setTenantPreference(tenantPreference);
            propertyData.setParking(parking);
            propertyData.setSecurity(security);
            propertyData.setExpectedRent(currentRent);

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

            int rentLimits[] = Utils.getRentLowerAndUpperLimit(predictedRent);

            int currentRentPercentage = (int) Utils.calculatePercentage(Utils.roundToNearestThousand(rentLimits[0]),Utils.roundToNearestThousand(rentLimits[1]),propertyData.getExpectedRent());
            arcSeekBar.setProgress(currentRentPercentage);

            rentLowerLimit.setText("₹"+ Utils.formatToIndianCurrency(Utils.roundToNearestThousand(rentLimits[0])));
            rentUpperLimit.setText("₹"+ Utils.formatToIndianCurrency(Utils.roundToNearestThousand(rentLimits[1])));

            rentEstimationInfo.setText("You are paying ₹" + Utils.formatToIndianCurrency((int) propertyData.getExpectedRent()) + ". ");

            switch (currentRentPercentage / 10) {
                case 0: case 1: case 2:
                        rentEstimationInfo.setText(rentEstimationInfo.getText().toString() + getResources().getString(R.string.tenant_rent_low_analysis));
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
}