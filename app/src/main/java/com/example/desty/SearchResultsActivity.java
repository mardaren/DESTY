package com.example.desty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {

    ListView listResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        //get result arraylist
        Intent intent = getIntent();
        ArrayList<Object []> results = (ArrayList<Object []>) intent.getSerializableExtra("result");
        ArrayList<String> headers = new ArrayList<>();

        for(Object[] a:results){
            for(int j=0;j<8;j++){
                System.out.print((a[j].toString()) + "-");

            }
            headers.add(a[2].toString());
            System.out.println();
        }

        listResults = (ListView) findViewById(R.id.list_results);

        /*ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("a");
        arrayList.add("b");
        arrayList.add("c");
        arrayList.add("d");
        arrayList.add("e");
        arrayList.add("f");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,arrayList);*/

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,headers);

        listResults.setAdapter(adapter);

        listResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SearchResultsActivity.this,"clicked item "+position+ " "+ headers.get(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}