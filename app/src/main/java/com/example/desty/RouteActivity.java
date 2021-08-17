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
    private int pid, route_id;
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


        route_id = Integer.parseInt(route[0].toString());
        pid = Integer.parseInt(route[1].toString());
        routeName.setText(route[2].toString());
        desc.setText(route[3].toString());
        rating.setText("Rating: "+route[4].toString());

        String name = MainActivity.getInstance().idToName(pid);
        publisherName.setText(name);
        String location_str = route[6] + "/" + route[7];
        location_str = location_str.replace(" ","");
        location.setText(location_str);

        buttonMap = findViewById(R.id.button_map);
        buttonMap.setOnClickListener(v -> {
            Intent i = new Intent(this, ShowRouteActivity.class);
            System.out.println(route_id);
            i.putExtra("Route_ID",route_id);
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
            i.putExtra("pid",pid+"");
            startActivity(i);
    }
}