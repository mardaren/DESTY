package com.example.desty;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.example.desty.ui.main.SectionsPagerAdapter;
import com.example.desty.databinding.ActivityMainBinding;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    Connection db_conn = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new MainActivity.Connect().execute();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }
    private class Connect extends AsyncTask<Void, Void, Connection> {
        @Override
        protected Connection doInBackground(Void... urls) {

            Connection connection = null;
            String conn_url;

            try {
                conn_url = BuildConfig.DB_URL;
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                connection = DriverManager.getConnection(conn_url);
                if (connection != null) {
                    Log.i("Connection Status", "Connected");
                } else
                    Log.i("Connection Status", "Not Connected");

            } catch (SQLException throwable) {
                throwable.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            db_conn=connection;

            return connection;
        }
    }
    private class  NameSearch extends AsyncTask<String, String, String> {
        String name;

        @Override
        protected String doInBackground(String... strings) {
            PreparedStatement statement;
            String query = "SELECT * FROM [dbo].[Route] WHERE route_name like '%?%'";

            try {
                statement = db_conn.prepareStatement(query);
                statement.setString(1, name);
                ResultSet resultSet = statement.executeQuery();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            return null;
        }
    }

    private class  CitySearch extends AsyncTask<String, String, String> {
        String name;

        @Override
        protected String doInBackground(String... strings) {
            PreparedStatement statement;
            String query = "SELECT * FROM [dbo].[Route] WHERE city like '?'";

            try {
                statement = db_conn.prepareStatement(query);
                statement.setString(1, name);
                ResultSet resultSet = statement.executeQuery();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            return null;
        }
    }
    private class  CountrySearch extends AsyncTask<String, String, String> {
        String name;

        @Override
        protected String doInBackground(String... strings) {
            PreparedStatement statement;
            String query = "SELECT * FROM [dbo].[Route] WHERE country like '?'";

            try {
                statement = db_conn.prepareStatement(query);
                statement.setString(1, name);
                ResultSet resultSet = statement.executeQuery();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            return null;
        }
    }

    private class  AllSearch extends AsyncTask<String, String, String> {
        String countryName,cityName,name;

        @Override
        protected String doInBackground(String... strings) {
            PreparedStatement statement;
            String query = "SELECT * FROM [dbo].[Route] WHERE country like '?' and city like '?' and route_name like '%?%'";

            try {
                statement = db_conn.prepareStatement(query);
                statement.setString(1, countryName);
                statement.setString(2, cityName);
                statement.setString(3, name);
                ResultSet resultSet = statement.executeQuery();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            return null;
        }
    }
}