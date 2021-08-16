package com.example.desty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RouteActivity extends AppCompatActivity {

    private Button buttonMap, buttonComments;
    private TextView routeName, publisherName, desc, rating;
    private Object[] route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        // ROUTE INFO
        Intent intent = getIntent();
        route = (Object[]) intent.getSerializableExtra("id");
        for(Object a: route){
            System.out.print(a.toString() +  "-");
        }
        System.out.println();

        routeName = (TextView) findViewById(R.id.text_routename);
        routeName.setText(route[2].toString());

        publisherName = (TextView) findViewById(R.id.text_publisher);
        publisherName.setText(getPublisher(route[1].toString()));/////dene*************************

        desc = (TextView) findViewById(R.id.text_route_desc);
        desc.setText(route[3].toString());

        rating = (TextView) findViewById(R.id.text_rating);
        rating.setText(route[4].toString());

        //ulke sehir de alinacak

        buttonMap = (Button) findViewById(R.id.button_map);
        buttonMap.setOnClickListener(v -> {
            Intent i = new Intent(this, LocationActivity.class);
            startActivity(i);
        });

        buttonComments= (Button) findViewById(R.id.button_comments);
        buttonComments.setOnClickListener(v -> {
            Intent i = new Intent(this, CommentsActivity.class);
            startActivity(i);
        });
    }

    public void onClickPublisher(View v){
        Intent i = new Intent(this, PublisherActivity.class);
        i.putExtra("publisher_id", (Integer) route[1]);
        startActivity(i);
    }

    private String getPublisher(String id){
        int i = Integer.parseInt(id);
        return MainActivity.getInstance().idToName(i);
    }
}