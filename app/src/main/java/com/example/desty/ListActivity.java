package com.example.desty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.desty.profile.FollowListActivity;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ListView listRoutes;
    private ArrayList<Object[]> result;
    private Context context;
    private int listId;
    private String pubId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        context = this;

        ArrayList<String> headers;
        ///dinamik olmalı
        Bundle bundle = getIntent().getExtras();
        String mode = "";


        if(bundle != null){
            mode = bundle.getString("mode");
            System.out.println(mode);
        }
        if (mode.equals("profile")){
            headers = getProfileRoutes();
        }
        else if (mode.equals("publisher")){
            System.out.println("pub");
            pubId = bundle.getString("pid");

            headers = getPublisherRoutes(Integer.parseInt(pubId));
            System.out.println(headers.toString());
        }
        else if (mode.equals("user")){
            listId = Integer.parseInt(bundle.getString("listId"));
            pubId = MainActivity.getInstance().userId + "";
            headers = getRoutes();
        }
        else {
            return;
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,headers);

        listRoutes = findViewById(R.id.list_routes);
        listRoutes.setAdapter(adapter);
        listRoutes.setOnItemClickListener((parent, view, position, id1) -> {
            Intent i = new Intent(context, RouteActivity.class);
            i.putExtra("id", result.get(position));
            startActivity(i);
        });
    }

    private ArrayList<String> getProfileRoutes(){
        /// null ise işlem yapılmalı **************************************************
        result = MainActivity.getInstance().getProfileRoutes();
        ArrayList<String> headers = new ArrayList<>();
        for (Object[] a:result)
            headers.add(a[2].toString());
        return headers;
    }

    private ArrayList<String> getPublisherRoutes(int id){
        /// null ise işlem yapılmalı **************************************************
        result = MainActivity.getInstance().getPublisherRoutes(id);
        ArrayList<String> headers = new ArrayList<>();
        for (Object[] a:result)
            headers.add(a[2].toString());
        return headers;
    }

    private ArrayList<String> getRoutes(){
        /// null ise işlem yapılmalı **************************************************
        result = MainActivity.getInstance().getRoutesFromList(listId);
        ArrayList<String> headers = new ArrayList<>();
        for (Object[] a:result)
            headers.add(a[2].toString());
        return headers;
    }
}