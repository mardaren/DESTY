package com.example.desty.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.desty.MainActivity;
import com.example.desty.R;

import java.util.ArrayList;

public class FollowListActivity extends AppCompatActivity {

    ListView followList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followlist);

        followList = (ListView) findViewById(R.id.list_follows);
        ArrayList<String> headers = getFollowlist();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,headers);

        followList.setAdapter(adapter);

        followList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(FollowListActivity.this,"clicked item "+position+ " "+ headers.get(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private ArrayList<String> getFollowlist(){
        ArrayList<Object[]> result = MainActivity.getInstance().getFollowlist();
        ArrayList<String> headers = new ArrayList<>();
        for (Object[] a:result){
            headers.add(a[1].toString());//***********************************************************
            /// burası degisecek
        }
        return headers;
    }
}