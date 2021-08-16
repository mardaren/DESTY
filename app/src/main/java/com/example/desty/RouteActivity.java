package com.example.desty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class RouteActivity extends AppCompatActivity {

    private Button buttonMap, buttonComments;
    private Object[] routeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        Intent intent = getIntent();
        routeId = (Object[]) intent.getSerializableExtra("id");
        //System.out.println(routeId);

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
        startActivity(i);
    }
}