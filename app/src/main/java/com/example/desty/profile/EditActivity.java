package com.example.desty.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.desty.ListActivity;
import com.example.desty.MainActivity;
import com.example.desty.R;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonChangeImage, buttonApply;
    EditText newUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //sonra implement edilecek ********************************************************
        buttonChangeImage = (Button) findViewById(R.id.button_change_image);

        buttonApply = (Button) findViewById(R.id.button_apply);
        newUsername = (EditText) findViewById(R.id.editTextProfileName);

        newUsername.setOnClickListener(this);

        buttonApply.setOnClickListener(v -> {
            String newName = newUsername.getText().toString();
            newName = (newName.equals("New Profile Name"))?null:newName;
            String newUrl = null;
            System.out.println(newName);
            sendQuery(newName,newUrl);
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        newUsername.setText("");
    }

    private void sendQuery(String name, String url){
        MainActivity.getInstance().edit(name,url);
    }

}