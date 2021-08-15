package com.example.desty.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.desty.MainActivity;
import com.example.desty.R;
import com.example.desty.SearchResultsActivity;

import java.util.ArrayList;

public class ListsActivity extends AppCompatActivity {

    ListView userLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        userLists = (ListView) findViewById(R.id.list_userLists);

        ArrayList<String> headers = getList();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,headers);

        userLists.setAdapter(adapter);

        userLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListsActivity.this,"clicked item "+position+ " "+ headers.get(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayList<String> getList(){
        ArrayList<Object[]> result = MainActivity.getInstance().getUserLists();
        ArrayList<String> headers = new ArrayList<>();
        for (Object[] a:result){
            headers.add(a[1].toString());//***********************************************************
            /// burası degisecek
        }
        return headers;
    }

}