package com.example.rentalapp;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView sideNavigationView;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    HomeFragment homeFragment;
    Toolbar toolbar;
    FirebaseAuth auth;
    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult o) {
                            int result = o.getResultCode();
                            Intent data = o.getData();

                            if (result == RESULT_OK) {
                                setLoginLogoutMenuOption();
                            }
                        }
                    }
            );

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
//        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.greena)));

        homeFragment = new HomeFragment();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav_drawer, R.string.close_nav_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        sideNavigationView = findViewById(R.id.navigation_drawer);
        sideNavigationView.setNavigationItemSelectedListener(this);
        setLoginLogoutMenuOption();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackground(null);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int bottom_nav_id = item.getItemId();
                if (bottom_nav_id == bottomNavigationView.getSelectedItemId()) {
                    return false;
                }
                if (bottom_nav_id == R.id.bot_nav_browse_properties) {
                    openFragment(homeFragment);
                    return true;
                }
                else if (bottom_nav_id == R.id.bot_nav_my_properties) {
                    if (Utils.isUserLoggedIn()) {
                        Intent browseMyPropertyIntent = new Intent(getApplicationContext(), BrowseMyProperty.class);
                        startActivity(browseMyPropertyIntent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Login to View your Property", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

//        Default Fragment
        fragmentManager = getSupportFragmentManager();
        openFragment(new HomeFragment());

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

    private void setLoginLogoutMenuOption() {
        Menu menu = sideNavigationView.getMenu();
        MenuItem loginLogoutMenuItem = menu.findItem(R.id.side_nav_login_logout);
        if (Utils.isUserLoggedIn()) {
            loginLogoutMenuItem.setTitle(getResources().getString(R.string.logout));
            loginLogoutMenuItem.setIcon(R.drawable.baseline_logout_24);
        } else {
            loginLogoutMenuItem.setTitle(getResources().getString(R.string.login));
            loginLogoutMenuItem.setIcon(R.drawable.baseline_login_24);
            // Refresh Home Fragment Here
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int sideNavOption = item.getItemId();

        Menu menu = sideNavigationView.getMenu();
        MenuItem loginMenuItem = menu.findItem(R.id.side_nav_login_logout);
//        MenuItem logoutMenuItem = menu.findItem(R.id.side_nav_logout);

        if (sideNavOption == R.id.side_nav_post_property) {
            if (Utils.isUserLoggedIn()) {
                Intent addPropertyIntent = new Intent(this, AddPropertyActivity.class);
                startActivity(addPropertyIntent);
            } else {
                Toast.makeText(this, "Please Login to Post your Property", Toast.LENGTH_SHORT).show();
            }
//            openFragment(new AddPropertyFragment());
        } else if (sideNavOption == R.id.side_nav_rent_estimate) {
            if (Utils.isUserLoggedIn()) {
                Intent rentEstimateIntent = new Intent(this, RentEstimationActivity.class);
                startActivity(rentEstimateIntent);
            } else {
                Toast.makeText(this, "Please Login for Rent Estimation", Toast.LENGTH_SHORT).show();
            }
//            openFragment(new AddPropertyFragment());
        } else if (sideNavOption == R.id.side_nav_login_logout) {
            drawerLayout.closeDrawer(GravityCompat.START);
            if (item.getTitle() == getResources().getString(R.string.login)) {
                Intent loginIntent = new Intent(this, LoginActivity.class);
                activityResultLauncher.launch(loginIntent);
            } else {
                Utils.logoutCurrentUser();
                setLoginLogoutMenuOption();
            }
        } else if (sideNavOption == R.id.side_nav_pay_rent) {
            Toast.makeText(this, "Rent Payment is future scope", Toast.LENGTH_SHORT).show();
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