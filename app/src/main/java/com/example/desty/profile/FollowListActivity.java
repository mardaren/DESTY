package com.example.desty.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.desty.MainActivity;
import com.example.desty.PublisherActivity;
import com.example.desty.R;
import com.example.desty.RouteActivity;

import java.util.ArrayList;

public class FollowListActivity extends AppCompatActivity {

    ListView followList;
    Context context;
    ArrayList<Object[]> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followlist);
        context = this;
        result = new ArrayList<>();

        followList = (ListView) findViewById(R.id.list_follows);
        ArrayList<String> headers = getFollowlist();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,headers);

        followList.setAdapter(adapter);
        followList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(context, PublisherActivity.class);
                i.putExtra("pid", result.get(position)[1].toString());
                startActivity(i);
            }
        });
    }

    private ArrayList<String> getFollowlist(){
        result = MainActivity.getInstance().getFollowlist();
        ArrayList<String> headers = new ArrayList<>();
        for (Object[] a:result){
            headers.add(a[2].toString());
        }
        return headers;
    }
}