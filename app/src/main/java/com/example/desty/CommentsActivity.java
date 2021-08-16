package com.example.desty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {

    private ListView comments;
    private int listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Intent intent = getIntent();
        listId = Integer.parseInt((String) intent.getSerializableExtra("list_id"));
        ArrayList<String> headers = new ArrayList<>();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,headers);

        comments = (ListView) findViewById(R.id.list_routes);
        comments.setAdapter(adapter);
        comments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // EKLEME YAPILABILIR
            }
        });

        //
        comments = (ListView) findViewById(R.id.list_comments);
    }
}