package com.example.dgurnani.placesihavebeentoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> places = new ArrayList<>();
    static ArrayList<LatLng> locations = new ArrayList<>();
    static ArrayAdapter arrayAdapter;

    private Button friendsButton;
    private Button socialMediaButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        friendsButton = (Button) findViewById(R.id.FriendsButton);
        socialMediaButton = (Button) findViewById(R.id.SocialMediaButton);

        ListView listView = (ListView) findViewById(R.id.listView);

        places.add("Add a new Place by tapping here!");
        locations.add(new LatLng(0, 0));

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, places);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("placeNumber", i);

                startActivity(intent);
            }
        });
    }

    public void HandleMediaButtons(View view) {

        Intent intent;

        switch (view.getId()) {
            case (R.id.FriendsButton):
                intent = new Intent(getApplicationContext(), FriendViewActivity.class);
                startActivity(intent);
                break;
            case R.id.SocialMediaButton:
                intent = new Intent(getApplicationContext(), ShareOnSocialMediaActivity.class);
                startActivity(intent);
                break;
        }
    }



}
