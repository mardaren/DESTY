package com.example.desty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class RouteActivity extends AppCompatActivity {

    private Button buttonMap, buttonComments;
    private TextView routeName, publisherName, desc, rating;
    private int pid;
    private Object[] route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        // ROUTE INFO
        Intent intent = getIntent();
        route = (Object[]) intent.getSerializableExtra("id");


        routeName = (TextView) findViewById(R.id.text_routename);
        publisherName = (TextView) findViewById(R.id.text_publisher);
        desc = (TextView) findViewById(R.id.text_route_desc);
        rating = (TextView) findViewById(R.id.text_rating);

        if(route!=null) {
            pid = Integer.parseInt(route[1].toString());
            routeName.setText(route[2].toString());
            desc.setText(route[3].toString());
            rating.setText(route[4].toString());
        }
        else{
            //pid bizim id'ye esit olacak ******************************************************
            pid = MainActivity.getInstance().userId;
            rating.setText(0+"");
        }
        publisherName.setText(getPublisher(pid));

        //ulke sehir de alinacak ***************************************************************

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
            System.out.println(pid);
            i.putExtra("pid", pid + "");
            startActivity(i);
    }

    private String getPublisher(int id){
        return MainActivity.getInstance().idToName(id);
    }
}