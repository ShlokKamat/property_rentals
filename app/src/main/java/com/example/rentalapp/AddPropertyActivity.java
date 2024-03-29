package com.example.rentalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

public class AddPropertyActivity extends AppCompatActivity implements View.OnClickListener {

    CardView s1, s2, s3, s4;
    Button s1Next, s2Prev, s2Next, s3Prev, s3Next, s4Prev, s4Next;
    String[] bhkTypes = {"1RK", "1BHK", "2BHK", "3BHK", "4BHK"};
    String[] furnishingTypes = {"Not Furnished", "Semi Furnished", "Fully Furnished"};
    AutoCompleteTextView bhkAutoCompleteTextView;
    AutoCompleteTextView furnishingAutoCompleteTextView;
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
        s1 = findViewById(R.id.postPropertyS1);
        s2 = findViewById(R.id.postPropertyS2);
        s3 = findViewById(R.id.postPropertyS3);
        s4 = findViewById(R.id.postPropertyS4);
        s1Next = findViewById(R.id.postPropertyS1Next);
        s1Next.setOnClickListener(this);
        s2Prev = findViewById(R.id.postPropertyS2Prev);
        s2Prev.setOnClickListener(this);
        s2Next = findViewById(R.id.postPropertyS2Next);
        s2Next.setOnClickListener(this);
        s3Prev = findViewById(R.id.postPropertyS3Prev);
        s3Prev.setOnClickListener(this);
        s3Next = findViewById(R.id.postPropertyS3Next);
        s3Next.setOnClickListener(this);
        s4Prev = findViewById(R.id.postPropertyS4Prev);
        s4Prev.setOnClickListener(this);
        s4Next = findViewById(R.id.postPropertyS4Next);
        s4Next.setOnClickListener(this);

        bhkAutoCompleteTextView = findViewById(R.id.bhkType);
        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, bhkTypes);
        bhkAutoCompleteTextView.setAdapter(stringArrayAdapter);

        furnishingAutoCompleteTextView = findViewById(R.id.furnishingType);
        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, furnishingTypes);
        furnishingAutoCompleteTextView.setAdapter(stringArrayAdapter);

        bhkAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(AddPropertyActivity.this, "BHK: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        furnishingAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(AddPropertyActivity.this, "Furnish Type: " + item, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.postPropertyS1Next) {
            s1.setVisibility(View.GONE);
            s2.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.postPropertyS2Prev) {
            s2.setVisibility(View.GONE);
            s1.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.postPropertyS2Next) {
            s2.setVisibility(View.GONE);
            s3.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.postPropertyS3Prev) {
            s3.setVisibility(View.GONE);
            s2.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.postPropertyS3Next) {
            s3.setVisibility(View.GONE);
            s4.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.postPropertyS4Prev) {
            s4.setVisibility(View.GONE);
            s3.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.postPropertyS4Next) {
            Toast.makeText(this, "PROPERTY POSTED", Toast.LENGTH_SHORT).show();
        }
    }
}