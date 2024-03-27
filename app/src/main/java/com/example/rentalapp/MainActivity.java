package com.example.rentalapp;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    Toolbar toolbar;
    FloatingActionButton fab;

//    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        firestore = FirebaseFirestore.getInstance();
//
//        Map<String, Object> users = new HashMap<>();
//        users.put("firstName","NAME");
//        users.put("lastName","LASTTT");
//        users.put("age",25);
//
//        firestore.collection("users").add(users).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//            @Override
//            public void onSuccess(DocumentReference documentReference) {
//                Toast.makeText(MainActivity.this, "SAVED TO FIREBASE", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(MainActivity.this, "FAILED TO FIREBASE", Toast.LENGTH_SHORT).show();
//            }
//        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.floatingID);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav_drawer, R.string.close_nav_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView sideNavigationView = findViewById(R.id.navigation_drawer);
        sideNavigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackground(null);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int bottom_nav_id = item.getItemId();
//                if (bottom_nav_id == R.id.home_option) {
//                    openFragment(new HomeFragment());
//                    return true;
//                } else
                    if (bottom_nav_id == R.id.bot_nav_browse_properties) {
                    openFragment(new BrowsePropertyFragment());
                    return true;
                }
//                    else if (bottom_nav_id == R.id.add_property_option) {
//                    openFragment(new AddPropertyFragment());
//                    return true;
//                }
                    else if (bottom_nav_id == R.id.bot_nav_pay_rent) {
                    openFragment(new PaymentFragment());
                    return true;
                }
                return false;
            }
        });

//        Default Fragment
        fragmentManager = getSupportFragmentManager();
        openFragment(new BrowsePropertyFragment());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomNavigationView.getMenu().findItem(R.id.bot_nav_dummy_item).setChecked(true);
                openFragment(new HomeFragment());
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int sideNavOption = item.getItemId();
        if (sideNavOption == R.id.side_nav_my_properties) {
            Toast.makeText(MainActivity.this, "No Property Posted Yet", Toast.LENGTH_SHORT).show();
        } else if (sideNavOption == R.id.side_nav_post_property) {
            Intent addPropertyIntent = new Intent(this, AddPropertyActivity.class);
            startActivity(addPropertyIntent);
//            openFragment(new AddPropertyFragment());
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        //Selection highlight disabled
        return false;
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}