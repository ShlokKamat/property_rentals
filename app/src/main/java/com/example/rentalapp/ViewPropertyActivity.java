package com.example.rentalapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

public class ViewPropertyActivity extends AppCompatActivity {

    public static final String PROPERTY_PARCEL_KEY = "property_parcel_key";
    TextView propertyViewHeader, localityView, propertySizeView, expectedRentView;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("View Property");

        propertyViewHeader = findViewById(R.id.property_view_header);
        localityView = findViewById(R.id.locality_view);
        propertySizeView = findViewById(R.id.property_size_view);
        expectedRentView = findViewById(R.id.expected_rent_view);
        PropertyDataClass propertyInfo = getIntent().getParcelableExtra(PROPERTY_PARCEL_KEY);

        propertyViewHeader.setText(propertyInfo.getBhkType() + " in " + propertyInfo.getApartmentName());
        localityView.setText(propertyInfo.getLocality());
        propertySizeView.setText(propertyInfo.getPropertySize() + " sq ft");
        expectedRentView.setText("₹" + propertyInfo.getExpectedRent() + "\nper month");


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent addPropertyIntent = new Intent(AddPropertyActivity.this, MainActivity.class);
//                startActivity(addPropertyIntent);
                finish();
            }
        });
    }
}