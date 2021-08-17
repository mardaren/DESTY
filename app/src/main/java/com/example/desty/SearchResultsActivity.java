package com.example.desty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {

    private ListView listResults;
    private Context context;
    private ArrayList<Object[]> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_search_results);

        // LIST
        ArrayList<String> headers = sendQuery();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,headers);

        listResults = (ListView) findViewById(R.id.list_results);
        listResults.setAdapter(adapter);
        listResults.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(context, RouteActivity.class);
            i.putExtra("id", results.get(position));
            startActivity(i);
        });
    }

    private ArrayList<String> sendQuery(){
        Intent intent = getIntent();
        results = (ArrayList<Object []>) intent.getSerializableExtra("result");
        ArrayList<String> headers = new ArrayList<>();
        for(Object[] a:results)
            headers.add(a[2].toString());
        return headers;
    }
}