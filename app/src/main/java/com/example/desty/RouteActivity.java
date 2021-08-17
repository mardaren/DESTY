package com.example.desty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RouteActivity extends AppCompatActivity {

    private Button buttonMap, buttonComments, buttonAdd;
    private TextView routeName, publisherName, desc, rating, location;
    private int route_id,pid;
    private Object[] route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        // ROUTE INFO
        Intent intent = getIntent();
        route = (Object[]) intent.getSerializableExtra("id");
        routeName = findViewById(R.id.text_route_name);
        publisherName = findViewById(R.id.text_publisher);
        desc = findViewById(R.id.text_route_desc);
        rating = findViewById(R.id.text_rating);
        location = findViewById(R.id.text_location);


        route_id = Integer.parseInt(route[0].toString());  // YANLIS ID
        pid = Integer.parseInt(route[1].toString());
        routeName.setText(route[2].toString());
        desc.setText(route[3].toString());
        rating.setText(route[4].toString());
        // hatalı
        publisherName.setText(route[1].toString());
        //hatali nullsa
        location.setText(route[6].toString().replace(" ","") + "/" + route[7].toString().replace(" ",""));
        //}
        buttonMap = findViewById(R.id.button_map);
        buttonMap.setOnClickListener(v -> {
            Intent i = new Intent(this, ShowRouteActivity.class);
            System.out.println(route_id);
            i.putExtra("Route_ID",route_id); // YANLISSSS
            startActivity(i);
        });

        buttonComments = findViewById(R.id.button_comments);
        buttonComments.setOnClickListener(v -> {
            Intent i = new Intent(this, CommentsActivity.class);
            startActivity(i);
        });

        buttonAdd = findViewById(R.id.button_addto_list);
        buttonAdd.setOnClickListener(v -> {
        });

    }

    public void onClickPublisher(View v){
            Intent i = new Intent(this, PublisherActivity.class);
            i.putExtra("pid",pid);
            startActivity(i);
    }
}