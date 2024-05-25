package com.example.rentalapp;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment {

    Button homeSearchButton;
    TextView userGreeting;

    public void refreshUserGreeting() {
        if (Utils.isUserLoggedIn()) {
            String name = Utils.getUserName();
            userGreeting.setText("Hey " + name + "!");
        }
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        refreshUserGreeting();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        homeSearchButton = view.findViewById(R.id.home_search_btn);
        userGreeting = view.findViewById(R.id.user_greeting_text);

        homeSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browsePropertyIntent = new Intent(getContext(), BrowseAllPropertyActivity.class);
                startActivity(browsePropertyIntent);
            }
        });

        return view;
    }
}