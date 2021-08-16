package com.example.desty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.desty.profile.FollowListActivity;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ListView listRoutes;
    private ArrayList<Object[]> result;
    private Context context;

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
            System.out.println("Mode: "+ mode);
        }
        if (mode=="publisher"){
            headers = getProfileRoutes();
        }
        else {
            return;
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,headers);

        listRoutes = (ListView) findViewById(R.id.list_routes);
        listRoutes.setAdapter(adapter);
        listRoutes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListActivity.this,"clicked item "+position+ " "+ headers.get(position), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(context, RouteActivity.class);
                i.putExtra("id", result.get(position));
                startActivity(i);
            }
        });
    }

    private ArrayList<String> getProfileRoutes(){
        /// null ise işlem yapılmalı **************************************************
        result = MainActivity.getInstance().getProfileRoutes();
        ArrayList<String> headers = new ArrayList<>();
        for (Object[] a:result)
            headers.add("List id: " + a[2].toString());
        return headers;
    }
}