package com.example.rentalapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddPropertyActivity extends AppCompatActivity implements View.OnClickListener {

    CardView s1card, s2card, s3card, s4card;
    Button s1Next, s2Back, s2Next, s3Back, s3Next, s4Back, s4Next;
    TextInputEditText apartmentNameInput, propertySizeInput, propertyAgeInput, floorInput, totalFloorsInput, localityInput, expectedRentInput, expectedDepositInput;
    ImageView photosInput;
    String photoURL;
    Uri uri;
    AutoCompleteTextView bhkTypeInput;
    AutoCompleteTextView furnishingTypeInput;
    String[] bhkTypes = {"1RK", "1BHK", "2BHK", "3BHK", "4BHK"};
    String[] furnishingTypes = {"Not Furnished", "Semi Furnished", "Fully Furnished"};
    ArrayAdapter<String> stringArrayAdapter;

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

//        For all EditTexts and Dropdowns
        apartmentNameInput = findViewById(R.id.apartment_name_input);
        propertySizeInput = findViewById(R.id.property_size_input);
        propertyAgeInput = findViewById(R.id.property_age_input);
        floorInput = findViewById(R.id.floor_input);
        totalFloorsInput = findViewById(R.id.total_floors_input);
        localityInput = findViewById(R.id.locality_input);
        expectedRentInput = findViewById(R.id.expected_rent_input);
        expectedDepositInput = findViewById(R.id.expected_deposit_input);
        photosInput = findViewById(R.id.photos_input);

        bhkTypeInput = findViewById(R.id.bhk_type_input);
        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, bhkTypes);
        bhkTypeInput.setAdapter(stringArrayAdapter);

        furnishingTypeInput = findViewById(R.id.furnishing_type_input);
        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, furnishingTypes);
        furnishingTypeInput.setAdapter(stringArrayAdapter);

        bhkTypeInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(AddPropertyActivity.this, "BHK: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        furnishingTypeInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(AddPropertyActivity.this, "Furnish Type: " + item, Toast.LENGTH_SHORT).show();
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
            s3card.setVisibility(View.GONE);
            s4card.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.step4_back) {
            s4card.setVisibility(View.GONE);
            s3card.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.step4_save) {
            saveData();
        }
    }

    public void saveData() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                .child(Objects.requireNonNull(uri.getLastPathSegment()));

        AlertDialog.Builder builder = new AlertDialog.Builder(AddPropertyActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
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

        PropertyDataClass propertyData = new PropertyDataClass(
                documentReference.getId(),
                Objects.requireNonNull(apartmentNameInput.getText()).toString(),
                bhkTypeInput.getText().toString(),
                Objects.requireNonNull(propertySizeInput.getText()).toString(),
                Objects.requireNonNull(propertyAgeInput.getText()).toString(),
                Objects.requireNonNull(floorInput.getText()).toString(),
                Objects.requireNonNull(totalFloorsInput.getText()).toString(),
                Objects.requireNonNull(localityInput.getText()).toString(),
                Objects.requireNonNull(expectedRentInput.getText()).toString(),
                Objects.requireNonNull(expectedDepositInput.getText()).toString(),
                furnishingTypeInput.getText().toString(),
                photoURL);

        documentReference.set(propertyData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(AddPropertyActivity.this, "LEZZ GO", Toast.LENGTH_SHORT).show();
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
}