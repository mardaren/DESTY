package com.example.desty.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.desty.ListActivity;
import com.example.desty.MainActivity;
import com.example.desty.R;
import com.example.desty.SearchResultsActivity;

import java.util.ArrayList;

public class ListsActivity extends AppCompatActivity {

    private ListView userLists;
    private ArrayList<Object[]> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);
        result = new ArrayList<>();

        userLists = (ListView) findViewById(R.id.list_userLists);

        ArrayList<String> headers = getList();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,headers);

        userLists.setAdapter(adapter);

        userLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ListsActivity.this, ListActivity.class);
                i.putExtra("mode","user");
                i.putExtra("listId", result.get(position)[0].toString());
                startActivity(i);
            }
        });
    }

    private ArrayList<String> getList(){
        result = MainActivity.getInstance().getUserLists();
        ArrayList<String> headers = new ArrayList<>();
        for (Object[] a:result){
            headers.add(a[2].toString());
        }
        return headers;
    }

}