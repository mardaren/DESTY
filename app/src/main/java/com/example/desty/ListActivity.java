package com.example.desty;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.desty.profile.FollowListActivity;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ListView listRoutes;
    ArrayList<String> headers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listRoutes = (ListView) findViewById(R.id.list_routes);
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

        listRoutes.setAdapter(adapter);

        listRoutes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListActivity.this,"clicked item "+position+ " "+ headers.get(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayList<String> getProfileRoutes(){
        /// null ise işlem yapılmalı **************************************************
        ArrayList<Object[]> result = MainActivity.getInstance().getProfileRoutes();
        ArrayList<String> headers = new ArrayList<>();
        for (Object[] a:result){
            headers.add(a[0].toString());//***********************************************************
            /// burası degisecek
        }
        return headers;
    }
}