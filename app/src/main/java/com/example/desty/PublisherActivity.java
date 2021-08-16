package com.example.desty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PublisherActivity extends AppCompatActivity {

    private int id;
    private Object[] info;
    private TextView pubName, rating, bio;
    private ImageView imgPublisher;
    private Button buttonRoutes, buttonFollow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher);

        // PUBLISHER INFO
        Intent intent = getIntent();
        id = Integer.parseInt((String) intent.getSerializableExtra("pid"));
        info = getPubInfo();
        /*
        for(Object a: info){
            if(a==null){
                System.out.print("null" +  "-");
            }
            else{
                System.out.print(a.toString() +  "-");
            }
        }
        System.out.println();*/

        //
        pubName = (TextView) findViewById(R.id.text_publishername);
        pubName.setText(info[1].toString());

        rating = (TextView) findViewById(R.id.text_rating_publisher);
        rating.setText(info[5].toString());

        bio = (TextView) findViewById(R.id.text_bio);
        bio.setText(info[4].toString());

        imgPublisher = (ImageView) findViewById(R.id.image_publisher);
        String url;
        if (info[3] == null){

        }
        else{
            url = info[3].toString();
        }

        //button
        buttonRoutes= (Button) findViewById(R.id.button_publisherroutes);
        buttonRoutes.setOnClickListener(v -> {
            Intent i = new Intent(this, ListActivity.class);
            i.putExtra("mode","publisher");
            i.putExtra("pid", info[0].toString());
            startActivity(i);
        });

        buttonFollow= (Button) findViewById(R.id.button_follow);
        buttonFollow.setOnClickListener(v -> {
            MainActivity.getInstance().follow(id);
        });

    }

    private Object[] getPubInfo(){
        return MainActivity.getInstance().getPublisherInfo(id);
    }

}