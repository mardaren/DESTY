package com.example.desty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

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

        buttonAdd = findViewById(R.id.button_add_to_list);

        buttonAdd.setOnClickListener(v -> {
            LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_routetolist,null);
            ListView list = layout.findViewById(R.id.popup_list);
            final PopupWindow popupWindow = new PopupWindow(RouteActivity.this);
            popupWindow.setContentView(layout);
            popupWindow.setFocusable(true);
            popupWindow.showAtLocation(layout , Gravity.CENTER, 0, 0);
            //int user_id = MainActivity.getInstance().userId;
            ArrayList<Object[]> userList = MainActivity.getInstance().getUserLists();
            ArrayList<String> headers = new ArrayList<>();
            for(Object[] a: userList){
                headers.add(a[2].toString());
            }

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,headers);
            list.setAdapter(adapter);

            list.setOnItemClickListener((parent, view, position, id1) -> {
                MainActivity.getInstance().addToList(Integer.parseInt(userList.get(position)[0].toString()),route_id);
                popupWindow.dismiss();
            });
        });

    }

    public void onClickPublisher(View v){
            Intent i = new Intent(this, PublisherActivity.class);
            i.putExtra("pid",pid+"");
            startActivity(i);
    }
}