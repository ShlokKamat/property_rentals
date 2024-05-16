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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView sideNavigationView;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    Toolbar toolbar;
    FirebaseAuth auth;
    //    FirebaseFirestore firestore;
    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult o) {
                            int result = o.getResultCode();
                            Intent data = o.getData();

                            if (result == RESULT_OK) {
                                Toast.makeText(MainActivity.this, "BACK FROM LOGIN ACT", Toast.LENGTH_SHORT).show();
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
//                if (bottom_nav_id == R.id.home_option) {
//                    openFragment(new HomeFragment());
//                    return true;
//                } else
                if (bottom_nav_id == R.id.bot_nav_browse_properties) {
                    openFragment(new HomeFragment());
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

//    private void setLoginMenuOption() {
//        Menu menu = sideNavigationView.getMenu();
//        MenuItem loginMenuItem = menu.findItem(R.id.side_nav_login_logout);
////        MenuItem logoutMenuItem = menu.findItem(R.id.side_nav_logout);
//        auth = FirebaseAuth.getInstance();
//        if (auth.getCurrentUser() != null) {
////            logoutMenuItem.setVisible(true);
//            loginMenuItem.setVisible(false);
//        } else {
//            loginMenuItem.setVisible(true);
////            logoutMenuItem.setVisible(false);
//        }
//    }

    private void setLoginLogoutMenuOption() {
        Menu menu = sideNavigationView.getMenu();
        MenuItem loginLogoutMenuItem = menu.findItem(R.id.side_nav_login_logout);
        if (isUserLoggedIn()) {
            Toast.makeText(this, "Logout Option Set", Toast.LENGTH_SHORT).show();
            loginLogoutMenuItem.setTitle(getResources().getString(R.string.logout));
            loginLogoutMenuItem.setIcon(R.drawable.baseline_logout_24);
        } else {
            Toast.makeText(this, "Login Option Set", Toast.LENGTH_SHORT).show();
            loginLogoutMenuItem.setTitle(getResources().getString(R.string.login));
            loginLogoutMenuItem.setIcon(R.drawable.baseline_login_24);
        }
    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        Toast.makeText(this, "RESUMEDDDDDDDDD", Toast.LENGTH_SHORT).show();
//        setLoginMenuOption();
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int sideNavOption = item.getItemId();

        Menu menu = sideNavigationView.getMenu();
        MenuItem loginMenuItem = menu.findItem(R.id.side_nav_login_logout);
//        MenuItem logoutMenuItem = menu.findItem(R.id.side_nav_logout);

        if (sideNavOption == R.id.side_nav_my_properties) {
            Toast.makeText(MainActivity.this, "No Property Posted Yet", Toast.LENGTH_SHORT).show();
        } else if (sideNavOption == R.id.side_nav_post_property) {
            if (isUserLoggedIn()) {
                Intent addPropertyIntent = new Intent(this, AddPropertyActivity.class);
                startActivity(addPropertyIntent);
            } else {
                Toast.makeText(this, "Please Login to Post your Property", Toast.LENGTH_SHORT).show();
            }
//            openFragment(new AddPropertyFragment());
        } else if (sideNavOption == R.id.side_nav_login_logout) {
            drawerLayout.closeDrawer(GravityCompat.START);
            if (item.getTitle() == getResources().getString(R.string.login)) {
                Intent loginIntent = new Intent(this, LoginActivity.class);
                activityResultLauncher.launch(loginIntent);
            } else {
                logoutCurrentUser();
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        //Selection highlight disabled
        return false;
    }

    private boolean isUserLoggedIn() {
        auth = FirebaseAuth.getInstance();
        return auth.getCurrentUser() != null;
    }

    private void logoutCurrentUser() {
        auth = FirebaseAuth.getInstance();
        auth.signOut();
        setLoginLogoutMenuOption();
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}